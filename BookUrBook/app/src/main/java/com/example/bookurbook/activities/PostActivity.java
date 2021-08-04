package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;

import android.widget.TextView;
import android.widget.Toast;

import com.example.bookurbook.MailAPISource.JavaMailAPI;
import com.example.bookurbook.R;
import com.example.bookurbook.fragments.ReportDialog;
import com.example.bookurbook.fragments.ReportPostDialogListener;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Chat;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages the controls of the post activity and does necessary changes in order to update
 * the database, view and model class datas.
 */
public class PostActivity extends AppCompatActivity implements ReportPostDialogListener
{

    //instance variables
    private Post post;
    private PostList postList;
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private int previousActivity;
    private Toolbar toolbar;
    private TextView postTitleTextView;
    private TextView postSellerTextView;
    private TextView postUniversityTextView;
    private TextView postCourseTextView;
    private TextView postPriceTextView;
    private TextView postDescriptionTextView;
    private ImageButton reportButton;
    private ImageButton wishlistButton;
    private ImageButton chatButton;
    private ImageButton homeButton;
    private ImageView postPic;
    private ImageView adminDeleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //method code
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//sets the toolbar as the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post");

        chatButton = findViewById(R.id.chat_image_button);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        post = (Post) getIntent().getSerializableExtra("post");
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");
        postList = (PostList) getIntent().getSerializableExtra("postlist");


        previousActivity = (Integer) getIntent().getExtras().get("previousActivity");

        postPic = findViewById(R.id.postImageView);
        postTitleTextView = findViewById(R.id.postTitleTextView);
        postSellerTextView = findViewById(R.id.postSellerTextView);
        postUniversityTextView = findViewById(R.id.postUniversityTextView);
        homeButton = findViewById(R.id.homeButton);
        postCourseTextView = findViewById(R.id.postCourseTextView);
        postPriceTextView = findViewById(R.id.postPriceTextView);
        postDescriptionTextView = findViewById(R.id.postDescriptionTextView);
        adminDeleteButton = findViewById(R.id.adminDeleteButton);
        reportButton = findViewById(R.id.reportButton);
        wishlistButton = findViewById(R.id.wishlistButton);
        postTitleTextView.setText(post.getTitle());
        postSellerTextView.setText("Seller: " + post.getOwner().getUsername());
        postUniversityTextView.setText("University: " + post.getUniversity());
        postCourseTextView.setText("Course: " + post.getCourse());
        postPriceTextView.setText("Price: " + post.getPrice() + "₺");
        postDescriptionTextView.setText(post.getDescription());
        Picasso.get().load(post.getPicture()).into(postPic);

        if (post.getOwner().getUsername().equals(currentUser.getUsername()))
        {
            chatButton.setVisibility(View.GONE);
            wishlistButton.setVisibility(View.GONE);
            reportButton.setVisibility(View.GONE);
        }
        if (currentUser instanceof Admin && !post.getOwner().getUsername().equals(currentUser.getUsername()))
            adminDeleteButton.setVisibility(View.VISIBLE);

        if (currentUser.getReportNum() >= 10)
        {
            badRepAlert();
        }


        /**
         * Will add the post to the wishlist if it is not included in the wishlist. If it is already
         * in the wishlist, it will remove it from the wishlist.
         */
        wishlistButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        List<String> wishlist = Collections.emptyList();
                        wishlist = (List<String>) documentSnapshot.get("wishlist");
                        if (!wishlist.contains(post.getId()))
                        {
                            wishlist.add(post.getId());
                        }
                        HashMap<String, Object> newData = new HashMap<>();
                        newData.put("wishlist", wishlist);
                        db.collection("users").document(auth.getCurrentUser().getUid()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(PostActivity.this, post.getTitle() + " has been added your WishList", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

        /**
         * This button will open the report dialog and the user will be able to write their reports.
         */
        reportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openPostReportDialog();
            }
        });

        /**
         * Sends the necessary intents in order to go back to the main screen.
         */
        homeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent startIntent = new Intent(PostActivity.this, MainMenuActivity.class);
                startIntent.putExtra("currentUser", currentUser);
                startActivity(startIntent);
            }
        });

        /**
         * This button starts a chat with the post owner
         */
        chatButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                db.collection("chats").document(currentUser.getUsername() + ", " + post.getOwner().getUsername())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                Intent pass = new Intent(PostActivity.this, ChatActivity.class);
                                Chat chat = new Chat(currentUser, post.getOwner(), currentUser.getUsername() + ", " + post.getOwner().getUsername());
                                pass.putExtra("currentUser", currentUser);
                                pass.putExtra("fromPostActivity", true);
                                pass.putExtra("post", post);
                                pass.putExtra("postlist", postList);
                                pass.putExtra("clickedChat", chat);
                                pass.putExtra("previousActivity", previousActivity);
                                startActivity(pass);
                            } else
                            {
                                db.collection("chats").document(post.getOwner().getUsername() + ", " + currentUser.getUsername())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        if (document.exists())
                                        {
                                            Intent pass = new Intent(PostActivity.this, ChatActivity.class);
                                            Chat chat = new Chat(currentUser, post.getOwner(), post.getOwner().getUsername() + ", " + currentUser.getUsername());
                                            pass.putExtra("currentUser", currentUser);
                                            pass.putExtra("fromPostActivity", true);
                                            pass.putExtra("post", post);
                                            pass.putExtra("postlist", postList);
                                            pass.putExtra("clickedChat", chat);
                                            pass.putExtra("previousActivity", previousActivity);
                                            startActivity(pass);
                                        } else
                                        {
                                            Intent pass = new Intent(PostActivity.this, ChatActivity.class);
                                            Chat chat = new Chat(currentUser, post.getOwner(), post.getOwner().getUsername() + ", " + currentUser.getUsername());
                                            Map<String, Object> chatData = new HashMap<>();
                                            chatData.put("username1", post.getOwner().getUsername());
                                            chatData.put("username2", currentUser.getUsername());
                                            chatData.put("lastmessage", "");
                                            chatData.put("lastmessagedate", new Date());
                                            chatData.put("readbyuser1", true);
                                            chatData.put("readbyuser2", true);
                                            db.collection("chats").document(chat.getChatID()).set(chatData);
                                            pass.putExtra("currentUser", currentUser);
                                            pass.putExtra("clickedChat", chat);
                                            pass.putExtra("post", post);
                                            pass.putExtra("postlist", postList);
                                            pass.putExtra("fromPostActivity", true);
                                            pass.putExtra("previousActivity", previousActivity);
                                            startActivity(pass);
                                        }

                                    }
                                });
                            }
                        } else
                        {
                            Toast chatError = Toast.makeText(PostActivity.this, "Something is wrong. Please check your Internet connection.", Toast.LENGTH_LONG);
                        }
                    }
                });
            }

        });


        adminDeleteButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Admin delete button is created so that admins can delete any post if they see it as problematic.
             * If the post gets deleted, this method takes necessary actions in order to update the database.
             * @param v view of the current screen
             */
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);

                builder.setTitle("Admin Delete Panel");
                builder.setMessage("Are you sure that you want to delete the Post of this user?");

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

                                        Intent pass = new Intent(PostActivity.this, PostListActivity.class);
                                        for (int i = 0; postList.getPostArray().size() > i; i++)
                                        {
                                            if (postList.getPostArray().get(i).getId().equals(post.getId()))
                                            {
                                                Admin admin = new Admin(currentUser.getUsername(), currentUser.getEmail(), currentUser.getAvatar());
                                                admin.deletePost(postList.getPostArray().get(i), postList);
                                                //postList.getPostArray().remove(i);
                                            }
                                        }
                                        pass.putExtra("currentUser", currentUser);
                                        pass.putExtra("postlist", postList);
                                        Toast.makeText(PostActivity.this, "You have successfully deleted the post of this user!", Toast.LENGTH_LONG).show();
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
    }


    /**
     * This method is created in order to create a pop up dialog using ReportDialog class.
     */
    public void openPostReportDialog()
    {
        ReportDialog dialog = new ReportDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * When the report dialog is created, the listener inside the ReportDialog class will call this method and therefore
     * we will be able to access the description and the category from the post activity that was provided in the dialog
     * This method gets the necessary info and sends the report mail to our (Veni Vidi Code) mail.
     *
     * @param description the description provided by the user
     * @param category    category of the report (Abusive,Scam...)
     */
    @Override
    public void applyTexts(String description, String category)
    {
        post.report(description, category);
        String reportDetails = currentUser.getUsername() + " has reported the following post: \nPost ID: " + post.getId() + "\nPost Title: " + post.getTitle() + "\nPost Owner: "
                + post.getOwner().getUsername() + "\nPost Picture: " + post.getPicture() + "\nPost Description: " + post.getDescription() + ".\n\nThis post has been reported in category "
                + category + "\nwith the description: " + description;
        JavaMailAPI reportPost = new JavaMailAPI(PostActivity.this, "vvcbookurbook@gmail.com", post.getTitle() + " REPORT", reportDetails);
        reportPost.execute();
        db.collection("users").whereEqualTo("username", post.getOwner().getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                for (DocumentSnapshot doc : task.getResult())
                {
                    String reportedUserID = doc.getId();
                    List<String> reporters = (List<String>) doc.get("reporters");
                    if (!reporters.contains(currentUser.getUsername()))
                    {
                        reporters.add(currentUser.getUsername());
                    }
                    HashMap<String, Object> newData = new HashMap<>();
                    newData.put("reporters", reporters);
                    db.collection("users").document(reportedUserID).set(newData, SetOptions.merge());
                }

            }
        });
    }

    /**
     * This method creates a pop up dialog before entering the screen if the seller of the post
     * has been reported several times.
     */
    public void badRepAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
        builder.setTitle("Attention");
        builder.setMessage("This user has been reported several times. Be careful with the user or the post.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Sends the necessary intents according to the needs of the previous screen.
     */
    public void onBackPressed()
    {
        Intent pass;
        if (previousActivity == 1)
            pass = new Intent(PostActivity.this, PostListActivity.class);
        else if (previousActivity == 2)
            pass = new Intent(PostActivity.this, MyPostsActivity.class);
        else
            pass = new Intent(PostActivity.this, WishlistActivity.class);
        pass.putExtra("postlist", postList);
        pass.putExtra("currentUser", currentUser);
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