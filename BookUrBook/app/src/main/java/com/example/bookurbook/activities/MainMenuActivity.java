package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for the Register Screen
 *
 * @author Veni Vidi Code
 * @version 2020 Fall
 */
public class MainMenuActivity extends AppCompatActivity
{
    //properties
    private View topleft;
    private View topright;
    private View botleft;
    private View botright;
    private ImageView wishlist;
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private PostList postList;
    private User currentPostOwner;
    private ImageButton adminPanelButton;
    private TextView adminPanelTextView;
    private Toolbar toolbar;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin) //getting the user object from previous screen
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");
        toolbar = findViewById(R.id.mainMenuToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Menu");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        topleft = findViewById(R.id.topleft);
        topright = findViewById(R.id.topright);
        botleft = findViewById(R.id.botleft);
        botright = findViewById(R.id.botright);
        wishlist = findViewById(R.id.wishlist);
        adminPanelButton = findViewById(R.id.adminPanelButton);
        adminPanelTextView = findViewById(R.id.adminPanelTextView);

        if (currentUser instanceof Admin) //setting admin panel button visible if the user is admin.
        {
            adminPanelButton.setVisibility(View.VISIBLE);
            adminPanelTextView.setVisibility(View.VISIBLE);
        }

        //what happens on click on the post list
        topleft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postList = new PostList(); //creates an postlist
                Intent pass = new Intent(MainMenuActivity.this, PostListActivity.class); //sets the intent

                //gets the current user's database data
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        List<String> blockedUsernames = (List<String>) documentSnapshot.get("blockedusers"); //gets the blockedusers

                        //scans through all of the posts existing
                        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    for (DocumentSnapshot document : task.getResult())
                                    {
                                        db.collection("users").whereEqualTo("username", document.getString("username")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                for (DocumentSnapshot doc : task.getResult())
                                                {
                                                    currentPostOwner = new RegularUser(doc.getString("username"), doc.getString("email"), doc.getString("avatar"));
                                                    currentPostOwner.setBanned(doc.getBoolean("banned"));
                                                    currentPostOwner.setReportNum(((List<String>) doc.get("reporters")).size());
                                                }
                                                if (!document.getBoolean("sold")) //if the post is marked as not sold
                                                {
                                                    if (!blockedUsernames.contains(currentPostOwner.getUsername()) && !currentPostOwner.isBanned()) //if the current post owner user is not blocked by the current user and
                                                    { //not banned, their posts will appear on the postlist
                                                        postList.addPost(new Post(document.getString("description"), document.getString("title"), document.getString("university")
                                                                , document.getString("course"), document.getLong("price").intValue(), document.getString("picture"), currentPostOwner, (String) document.get("id")));
                                                    }
                                                }
                                                pass.putExtra("currentUser", currentUser);
                                                pass.putExtra("postlist", postList);
                                                startActivity(pass);
                                                finish();
                                            }
                                        });
                                    }
                                }
                                pass.putExtra("currentUser", currentUser);
                                pass.putExtra("postlist", postList);
                                startActivity(pass);
                            }
                        });

                    }
                });
            }
        });

        //what happens on the my chats button
        topright.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pass = new Intent(MainMenuActivity.this, MyChatsActivity.class);
                db.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        List<String> blockedUsernamesList = (List<String>) documentSnapshot.get("blockedusers"); //gets the blockedusers and passes it with the current user
                        ArrayList<String> blockedUsernames = new ArrayList<String>();
                        blockedUsernames.addAll(blockedUsernamesList);
                        pass.putExtra("blockedUsernames", blockedUsernames);
                        pass.putExtra("currentUser", currentUser);
                        startActivity(pass);
                    }
                });
            }
        });

        //what happens on the myposts button
        botleft.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postList = new PostList(); //creates an empty list
                Intent pass = new Intent(MainMenuActivity.this, MyPostsActivity.class); //sets the intent and goes through all of the posts
                db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot document : task.getResult())
                            {//specifically searchs for current user's posts
                                db.collection("users").whereEqualTo("username", document.getString("username")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                                    {
                                        for (DocumentSnapshot doc : task.getResult())
                                        {
                                            currentPostOwner = new RegularUser(doc.getString("username"), doc.getString("email"), doc.getString("avatar"));

                                        }
                                        if (document.getString("username").equals(currentUser.getUsername()))
                                        {
                                            postList.addPost(new Post(document.getString("description"), document.getString("title"), document.getString("university")
                                                    , document.getString("course"), document.getLong("price").intValue(), document.getString("picture"), currentUser, (String) document.get("id")));
                                            postList.getPostArray().get(postList.getPostArray().size() - 1).setSold(document.getBoolean("sold")); //sets if it is sold or not
                                        }
                                        pass.putExtra("currentUser", currentUser);
                                        pass.putExtra("postlist", postList);
                                        startActivity(pass);
                                        finish();
                                    }
                                });
                            }
                        }
                        pass.putExtra("currentUser", currentUser);
                        pass.putExtra("postlist", postList);
                        startActivity(pass);
                    }
                });
            }
        });

        //what happens on the settings button
        botright.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pass = new Intent(MainMenuActivity.this, SettingsActivity.class);
                pass.putExtra("currentUser", currentUser); //just sends the current user
                startActivity(pass);
            }
        });

        //what happens on the wishlist button
        wishlist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                postList = new PostList(); //creates an empty postlist
                Intent pass = new Intent(MainMenuActivity.this, WishlistActivity.class); //sets the intent

                //looks for current user's wishlist in database
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        db.collection("posts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                            {
                                if (task.isSuccessful())
                                {
                                    for (DocumentSnapshot document : task.getResult())
                                    {
                                        db.collection("users").whereEqualTo("username", document.getString("username")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                List<String> wished = (List<String>) documentSnapshot.get("wishlist");
                                                List<String> blocklist = (List<String>) documentSnapshot.get("blockedusers");

                                                for (DocumentSnapshot doc : task.getResult())
                                                {
                                                    currentPostOwner = new RegularUser(doc.getString("username"), doc.getString("email"), doc.getString("avatar"));
                                                    currentPostOwner.setBanned(doc.getBoolean("banned"));
                                                    currentPostOwner.setReportNum(((List<String>) doc.get("reporters")).size());
                                                }
                                                if (!document.getBoolean("sold"))
                                                {
                                                    if (wished.contains(document.getString("id")) && !currentPostOwner.isBanned() && !blocklist.contains(currentPostOwner.getUsername())) //if the wishlist contains the current post and the user is not blocked the current post owner, it will be added to the PostList object
                                                    {
                                                        postList.addPost(new Post(document.getString("description"), document.getString("title"), document.getString("university")
                                                                , document.getString("course"), document.getLong("price").intValue(), document.getString("picture"), currentPostOwner, (String) document.get("id")));
                                                    }
                                                }
                                                currentUser.setWishList(postList.getPostArray());
                                                pass.putExtra("currentUser", currentUser);
                                                startActivity(pass);
                                                finish();
                                            }
                                        });
                                    }
                                } else
                                {
                                    pass.putExtra("currentUser", currentUser);
                                }
                            }
                        });
                    }

                });
            }
        });

        //what happens on the admin panel button
        adminPanelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayList<User> reportedUsers = new ArrayList<User>(); //we want to see who is reported
                Intent adminPanel = new Intent(MainMenuActivity.this, AdminPanelActivity.class); //sets the intent
                adminPanel.putExtra("currentUser", currentUser);
                //looks for all of the users who have got more than one reporters
                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        for (QueryDocumentSnapshot doc : task.getResult())
                        {
                            if (((List<String>) doc.get("reporters")).size() > 0)
                            {
                                if (doc.getBoolean("admin"))
                                    reportedUsers.add(new Admin(doc.getString("username"), doc.getString("email"), doc.getString("avatar")));
                                else
                                    reportedUsers.add(new RegularUser(doc.getString("username"), doc.getString("email"), doc.getString("avatar")));
                                reportedUsers.get(reportedUsers.size() - 1).setReportNum(((List<String>) doc.get("reporters")).size());
                                reportedUsers.get(reportedUsers.size() - 1).setBanned(doc.getBoolean("banned")); //sets their report and banned situation
                                if (reportedUsers.get(reportedUsers.size() - 1).getUsername().equals(currentUser.getUsername())) //We dont wanna ban ourselves lol.
                                    reportedUsers.remove(reportedUsers.size() - 1);
                            }
                        }
                        adminPanel.putExtra("userlist", reportedUsers);
                        startActivity(adminPanel);
                        finish();
                    }
                });
                adminPanel.putExtra("userlist", reportedUsers);
                startActivity(adminPanel);
                finish();
            }
        });

    }

    /**
     * This method literally does nothing on the BackButton pressed. If we did not override it,
     * it's super call would make the application buggy!
     */
    @Override
    public void onBackPressed()
    {
        //DO NOTHING
    }
}
