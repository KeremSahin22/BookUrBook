package com.example.bookurbook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookurbook.adapters.MyPostsAdapter;
import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.squareup.picasso.Picasso;
/**
 * This class connects between My Posts view and its adapter class, accessing and using data from database also updating them according to actions
 */

public class MyPostsActivity extends AppCompatActivity
{

    //variables
    private ImageButton add;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView img;
    private TextView username, userType;
    private MyPostsAdapter adp;
    private PostList postList;
    private User currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        init(); //calling method to initialize variables
    }

    /**
     * This method will initialize the variables of the view
     */
    public void init()
    {
        toolbar = (Toolbar) findViewById(R.id.myPostsToolbar); //setting toolbar to the view
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        //initializing view variables
        this.add = findViewById(R.id.addButton);
        this.username = findViewById(R.id.username);
        this.userType = findViewById(R.id.user_type);
        this.img = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.recycler_id);

        //accessing current user from database
        if(getIntent().getSerializableExtra("currentUser") instanceof Admin) //if current user is admin
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser"); //else he/she is regular user

        postList = (PostList) getIntent().getSerializableExtra("postlist"); //accessing post list from database

        Picasso.get().load(currentUser.getAvatar()).into(img); //getting user avatar from database

        adp = new MyPostsAdapter(MyPostsActivity.this, postList.getPostArray()); //setting adapter for recycle view
        recyclerView.setAdapter(adp);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        username.setText(currentUser.getUsername());
        if(currentUser instanceof RegularUser)
            userType.setText("Regular User");
        else
            userType.setText("Admin User");

        add.setOnClickListener(new View.OnClickListener()
        {  //when add button is clicked
            @Override
            public void onClick(View v) {
                addPost();
            }
        });

    }



    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(MyPostsActivity.this, MainMenuActivity.class);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass);
        finish();
    }

    /**
     * This method will be called when add button is clicked
     */
    public void addPost()
    {
        Intent intent = new Intent(getBaseContext(), CreatePostActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("postlist", postList);
        intent.putExtra("fromPostList", false);
        startActivity(intent);
        finish();
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
