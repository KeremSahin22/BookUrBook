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
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Random;

/**
 * This class is created in order to manage the linkage between the model classes and the create post
 * layout.
 */
public class CreatePostActivity extends AppCompatActivity {
    //instance variables
    private Post post;
    private PostList postList;
    private User currentUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private boolean isPhotoPicked;
    private boolean isPostListPreviousActivity;
    private Toolbar toolbar;
    private EditText postTitleCreatePost;
    private Spinner spinner;
    private Spinner spinner2;
    private EditText postPrice;
    private EditText postDescriptionCreatePost;
    private ImageButton homeButton;
    private ImageButton applyButton;
    private ImageView photoUpload;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //method code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        //type casting in order to prevent issues related to only-admin features.
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");

        postList = (PostList) getIntent().getSerializableExtra("postlist");
        isPostListPreviousActivity = (Boolean) getIntent().getExtras().get("fromPostList");
        isPhotoPicked = false;

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        toolbar = findViewById(R.id.toolbar_without_report);
        //sets the toolbar as the action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Post");

        postTitleCreatePost = findViewById(R.id.postTitleCreatePost);
        spinner = findViewById(R.id.createPostSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CreatePostActivity.this, R.array.Universities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);//creates the drawbar for the courses in choosing a course
        spinner2 = findViewById(R.id.createPostSpinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(CreatePostActivity.this, R.array.Courses, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        photoUpload = findViewById(R.id.photoUpload);
        postPrice = findViewById(R.id.postPriceCreatePost);
        postDescriptionCreatePost = findViewById(R.id.postDescriptionCreatePost);

        photoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        homeButton = findViewById(R.id.homeButtonCreatePost);
        homeButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Goes to the home screen and sends the necessary information as intents for the database
             * to work correctly
             */
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(CreatePostActivity.this, MainMenuActivity.class);
                startIntent.putExtra("currentUser", currentUser);
                startActivity(startIntent);
            }
        });

        applyButton = findViewById(R.id.applyButtonCreatePost);
        applyButton.setOnClickListener(new View.OnClickListener() {
            /**
             * When applied, sends the necessary info to the database
             * @param v view of the current screen
             */
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreatePostActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure that you want to create the Post?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (postTitleCreatePost.getText().toString().equals("")) {
                            Toast.makeText(CreatePostActivity.this, "You need to enter a title!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else if (postPrice.getText().toString().equals("")) {
                            Toast.makeText(CreatePostActivity.this, "You need to enter the price!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {

                            updateDatabase();
                            Toast.makeText(CreatePostActivity.this, "You have successfully created the post!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
    private void choosePicture() {
        Intent galleryOpen = new Intent();
        galleryOpen.setType("image/*");
        galleryOpen.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryOpen, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(photoUpload);
            isPhotoPicked = true;
        }
    }

    /**
     * Sets the necessary updates for the database such as creation of the post, addition of this
     * post to the postlist and the storage of these datas.
     */
    private void updateDatabase() {

        String randomKey = randomKeyGenerator();
        if (isPhotoPicked) {
            StorageReference picRef = storage.getReference().child("posts/post_picture/" + randomKey);
            picRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    HashMap<String, Object> newData = new HashMap();
                                    postList.createPost(postDescriptionCreatePost.getText().toString(), postTitleCreatePost.getText().toString()
                                            , spinner.getSelectedItem().toString(), spinner2.getSelectedItem().toString(), Integer.parseInt(postPrice.getText().toString())
                                            , uri.toString(), currentUser, randomKey);
                                    post = postList.getPostArray().get(postList.getPostArray().size() - 1);
                                    newData.put("picture", uri.toString());
                                    newData.put("title", post.getTitle());
                                    newData.put("description", post.getDescription());
                                    newData.put("university", post.getUniversity());
                                    newData.put("course", post.getCourse());
                                    newData.put("price", post.getPrice());
                                    newData.put("username", post.getOwner().getUsername());
                                    newData.put("id", post.getId());
                                    newData.put("sold", false);
                                    db.collection("posts").document(post.getId()).set(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent pass;
                                            if (isPostListPreviousActivity)
                                                pass = new Intent(CreatePostActivity.this, PostListActivity.class);
                                            else
                                                pass = new Intent(CreatePostActivity.this, MyPostsActivity.class);
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
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            HashMap<String, Object> newData = new HashMap();
            postList.createPost(postDescriptionCreatePost.getText().toString(), postTitleCreatePost.getText().toString()
                    , spinner.getSelectedItem().toString(), spinner2.getSelectedItem().toString(), Integer.parseInt(postPrice.getText().toString())
                    , "https://i.pinimg.com/originals/2c/fc/93/2cfc93d7665f5d7728782700e50596e3.png", currentUser, randomKey);
            post = postList.getPostArray().get(postList.getPostArray().size() - 1);
            newData.put("picture", post.getPicture());
            newData.put("title", post.getTitle());
            newData.put("description", post.getDescription());
            newData.put("university", post.getUniversity());
            newData.put("course", post.getCourse());
            newData.put("price", post.getPrice());
            newData.put("username", post.getOwner().getUsername());
            newData.put("id", post.getId());
            newData.put("sold", false);
            db.collection("posts").document(post.getId()).set(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Intent pass = new Intent(CreatePostActivity.this, MyPostsActivity.class);
                    pass.putExtra("currentUser", currentUser);
                    pass.putExtra("postlist", postList);
                    startActivity(pass);
                    finish();
                }
            });
        }
    }

    /**
     * Is created in order to generate a new name for document in database
     * @return a randomly generated string
     */
    protected String randomKeyGenerator() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 30) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    /**
     * Sends the necessary intents according to the needs of the previous screen.
     */
    @Override
    public void onBackPressed() {
        Intent pass;
        if (isPostListPreviousActivity)
            pass = new Intent(CreatePostActivity.this, PostListActivity.class);
        else
            pass = new Intent(CreatePostActivity.this, MyPostsActivity.class);
        pass.putExtra("postlist", postList);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass);
        finish();
    }

    /**
     * Is created in order to make the back arrow in toolbar use the code of the onBackPressed method.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
