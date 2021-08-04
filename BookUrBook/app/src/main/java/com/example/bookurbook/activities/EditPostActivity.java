package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bookurbook.R;
import com.example.bookurbook.SendNotificationPack.APIService;
import com.example.bookurbook.SendNotificationPack.Client;
import com.example.bookurbook.SendNotificationPack.Data;
import com.example.bookurbook.SendNotificationPack.MyResponse;
import com.example.bookurbook.SendNotificationPack.NotificationSender;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class is created in order to manage the linkage between the model classes and the edit post
 * view, updating the informations of the database when a post gets edited.
 */
public class EditPostActivity extends AppCompatActivity
{
    //instance variables
    private Post post;
    private PostList postList;
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri imageUri;
    private ImageView photoUpload;
    private Toolbar toolbar;
    private EditText postTitleEditText;
    private Spinner spinner;
    private Spinner spinner2;
    private EditText postPrice;
    private EditText postDescriptionEditText;
    private ImageButton homeButton;
    private ImageButton deleteButton;
    private ImageButton applyButton;
    private boolean picChanged;
    private APIService apiService;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //method code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        picChanged = false;
        //type casting in order to prevent issues related to only-admin features.
        post = (Post) getIntent().getSerializableExtra("post");
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin)
        {
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        } else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");

        postList = (PostList) getIntent().getSerializableExtra("postlist");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        id = post.getId();

        toolbar = findViewById(R.id.toolbar_with_trashcan);
        //sets toolbar as the action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Post");

        postTitleEditText = findViewById(R.id.postTitleEditText);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditPostActivity.this, R.array.Universities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //creates the drawbar in the edit post
        spinner.setAdapter(adapter);
        for (int i = 0; i < spinner.getCount(); i++)
        {
            if (spinner.getItemAtPosition(i).toString().equals(post.getUniversity()))
                spinner.setSelection(i);
        }
        spinner2 = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(EditPostActivity.this, R.array.Courses, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        for (int i = 0; i < spinner2.getCount(); i++)
        {
            if (spinner2.getItemAtPosition(i).toString().equals(post.getCourse()))
                spinner2.setSelection(i);
        }

        postPrice = findViewById(R.id.postPriceEditText);
        postDescriptionEditText = findViewById(R.id.postDescriptionEditText);
        postTitleEditText.setText(post.getTitle());
        postPrice.setText(post.getPrice() + "");
        postDescriptionEditText.setText(post.getDescription());
        photoUpload = findViewById(R.id.photoUpload2);
        Picasso.get().load(post.getPicture()).into(photoUpload);

        photoUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                choosePicture();
            }
        });

        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Goes to the home screen and sends the necessary information as intents for the database
             * to work correctly
             */
            @Override
            public void onClick(View v)
            {
                Intent startIntent = new Intent(EditPostActivity.this, MainMenuActivity.class);
                startIntent.putExtra("currentUser", currentUser);
                startActivity(startIntent);
            }
        });
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Creates a alert dialog,and if the user presses ok, the post gets deleted in the app.
             */
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPostActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure that you want to delete the Post?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int which)
                    {


                        db.collection("posts").whereEqualTo("id", post.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {

                                (task.getResult().getDocuments().get(0).getReference()).delete().addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {

                                        Intent pass = new Intent(EditPostActivity.this, MyPostsActivity.class);
                                        for (int i = 0; postList.getPostArray().size() > i; i++)
                                        {
                                            if (postList.getPostArray().get(i).getId().equals(post.getId()))
                                                postList.getPostArray().remove(i);
                                        }
                                        pass.putExtra("currentUser", currentUser);
                                        pass.putExtra("postlist", postList);
                                        Toast.makeText(EditPostActivity.this, "You have successfully deleted the Post!", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        startActivity(pass);
                                        finish();
                                    }
                                });

                            }
                        });
                        //Then it will close the screen automatically!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Makes the necessary changes in order to update the edited datas on the database.
             * @param v view of the current activity
             */
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPostActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure that you want to apply the changes?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (postTitleEditText.getText().toString().equals(""))
                        {
                            Toast.makeText(EditPostActivity.this, "You need to enter a title!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else if (postPrice.getText().toString().equals(""))
                        {
                            Toast.makeText(EditPostActivity.this, "You need to enter the price!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else
                        {
                            updateDatabase();
                            Toast.makeText(EditPostActivity.this, "You have successfully applied your changes!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();

                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    /**
     * Opens the gallery of the used phone and then the user will select the image from there
     */
    private void choosePicture()
    {
        Intent galleryOpen = new Intent();
        galleryOpen.setType("image/*");
        galleryOpen.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryOpen, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            picChanged = true;
            Picasso.get().load(imageUri).into(photoUpload);
        }


    }

    /**
     * Sets the necessary updates for the database such as editing the previous data of the post, alerting
     * the postlist that the changes have been made.
     */
    private void updateDatabase()
    {
        boolean priceChanged = post.getPrice() != Integer.parseInt(postPrice.getText().toString()); //if price has changed
        if (priceChanged)
        {   //Send notification to users who added this post to their wishlists.
            db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot doc : task.getResult()) //check all users
                        {
                            List<String> list = (List<String>) doc.get("wishlist");
                            if (list != null && list.contains(post.getId())) //if the user added this post to his wishlist
                            {
                                db.collection("tokens").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                { //get the device token of that user
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        sendNotifications(documentSnapshot.get("token").toString(), "Price of a post in your wishlist has changed.", post.getTitle() + "'s price has changed.");
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
        if (picChanged)
        {
            StorageReference picRef = storage.getReference().child("posts/post_picture/" + post.getId());
            picRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            // Get a URL to the uploaded content
                            Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri uri)
                                {

                                    HashMap<String, Object> newData = new HashMap();
                                    post.editPost(postDescriptionEditText.getText().toString(),postTitleEditText.getText().toString()
                                            ,spinner.getSelectedItem().toString(),spinner2.getSelectedItem().toString()
                                            ,Integer.parseInt(postPrice.getText().toString()),uri.toString());
                                    newData.put("picture", uri.toString());
                                    newData.put("title", post.getTitle());
                                    newData.put("description", post.getDescription());
                                    newData.put("university", post.getUniversity());
                                    newData.put("course", post.getCourse());
                                    newData.put("price", post.getPrice());
                                    db.collection("posts").document(post.getId()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            Intent pass = new Intent(EditPostActivity.this, MyPostsActivity.class);
                                            for (int i = 0; postList.getPostArray().size() > i; i++)
                                            {
                                                if (postList.getPostArray().get(i).getId().equals(id))
                                                    postList.getPostArray().remove(i);
                                            }
                                            postList.addPost(post);
                                            ;
                                            pass.putExtra("currentUser", currentUser);
                                            pass.putExtra("postlist", postList);
                                            startActivity(pass);
                                            finish();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();
                        }
                    });
        } else
        {
            HashMap<String, Object> newData = new HashMap();
            post.editPost(postDescriptionEditText.getText().toString(), postTitleEditText.getText().toString()
                    , spinner.getSelectedItem().toString(), spinner2.getSelectedItem().toString()
                    , Integer.parseInt(postPrice.getText().toString()), post.getPicture());
            newData.put("title", post.getTitle());
            newData.put("description", post.getDescription());
            newData.put("university", post.getUniversity());
            newData.put("course", post.getCourse());
            newData.put("price", post.getPrice());
            db.collection("posts").document(post.getId()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Intent pass = new Intent(EditPostActivity.this, MyPostsActivity.class);
                    for (int i = 0; postList.getPostArray().size() > i; i++)
                    {
                        if (postList.getPostArray().get(i).getId().equals(id))
                            postList.getPostArray().remove(i);
                    }
                    postList.addPost(post);
                    ;
                    pass.putExtra("currentUser", currentUser);
                    pass.putExtra("postlist", postList);
                    startActivity(pass);
                    finish();
                }
            });
        }
    }


    /**
     * To send notification to other user by using database and SendNotificationPack
     *
     * @param usertoken device token of  other user
     * @param title     notification title
     * @param message   notification message
     */
    public void sendNotifications(String usertoken, String title, String message)
    {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>()
        {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
            {
                if (response.code() == 200)
                {
                    if (response.body().success != 1)
                    {
                        Toast.makeText(EditPostActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t)
            {

            }
        });
    }

    /**
     * Sends the necessary intents according to the needs of the previous screen.
     */
    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(EditPostActivity.this, MyPostsActivity.class);
        pass.putExtra("currentUser", currentUser);
        pass.putExtra("postlist", postList);
        startActivity(pass);
        finish();
    }

    /**
     * Is created in order to make the back arrow in toolbar use the code of the onBackPressed method.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}