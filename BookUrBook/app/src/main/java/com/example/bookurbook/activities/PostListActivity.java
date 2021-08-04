package com.example.bookurbook.activities;

import android.content.Intent;
import android.os.Bundle;


import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;


import com.example.bookurbook.fragments.FilterScreenView;
import com.example.bookurbook.adapters.PostListAdapter;
import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * a class to display the Post List screen, which inludes
 * all the posts in the database and some buttons for further operations
 */
public class PostListActivity extends AppCompatActivity implements FilterScreenView.FilterScreenListener
{

    // properties
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Button LtoHpriceButton;
    private Button HtoLpriceButton;
    private Button AtoZbutton;
    private Button ZtoAbutton;
    private Button resetButton;
    private ImageButton filterButton;
    private ImageButton createPostButton;
    private PostListAdapter postListAdapter;
    private PostList postList;
    private FirebaseFirestore db;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        toolbar = findViewById(R.id.postListToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Post List");


        // connection to database
        db = FirebaseFirestore.getInstance();
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");
        postList = (PostList) getIntent().getSerializableExtra("postlist");
        for (int i = 0; postList.getPostArray().size() > i; i++)
        {
            System.out.println(postList.getPostArray().get(i).getOwner().getEmail());
        }
        System.out.println("CURRENT USER: " + currentUser.getUsername());


        // initializing the variables
        createPostButton = findViewById(R.id.createPostButton);
        searchView = findViewById(R.id.search_id);
        recyclerView = findViewById(R.id.recycler_id);
        toolbar = findViewById(R.id.toolbar);
        LtoHpriceButton = findViewById(R.id.LtoH_price_button);
        HtoLpriceButton = findViewById(R.id.HtoL_price_button);
        AtoZbutton = findViewById(R.id.AtoZ_button);
        ZtoAbutton = findViewById(R.id.ZtoA_button);
        resetButton = findViewById(R.id.reset_button);
        filterButton = findViewById(R.id.filterButton);

        // an adapter for recycler view
        postListAdapter = new PostListAdapter(this, postList, currentUser);
        recyclerView.setAdapter(postListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // method for searching through the posts
        search(postListAdapter);

        /**
         * method for opening the Create Post screen upon tapping the button
         */
        createPostButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PostListActivity.this, CreatePostActivity.class);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("postlist", postList);
                intent.putExtra("fromPostList", true);
                startActivity(intent);
            }
        });

        /**
         * clicking this button will sort the posts in the order of lower price to higher price
         */
        LtoHpriceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postListAdapter.sort(view);
            }
        });

        /**
         * clicking this button will sort the posts in the order of higher price to lower price
         */
        HtoLpriceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postListAdapter.sort(view);
            }
        });

        /**
         * clicking this button will sort the posts in the alphabetical order
         */
        AtoZbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postListAdapter.sort(view);
            }
        });

        /**
         * clicking this button will sort the posts in the reverse alphabetical order
         */
        ZtoAbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postListAdapter.sort(view);
            }
        });

        /**
         * clicking this button will set the order to default version
         */
        resetButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postListAdapter.sort(view);
            }
        });

        /**
         * clicking this button will open a pop-up window where the user
         * can filter the posts according to their preferences
         */
        filterButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openFilterWindow();
            }
        });


    }


    /**
     * method for opening the Filter Screen pop-up window
     */
    public void openFilterWindow()
    {
        FilterScreenView filterScreen = new FilterScreenView();
        filterScreen.show(getSupportFragmentManager(), "example filter");
    }


    /**
     * method for filtering the post list according to the keywords
     * entered by the user to the search bar
     *
     * @param adp : the adapter which is used to display the post list
     */
    public void search(PostListAdapter adp)
    {
        final PostListAdapter adapter = adp;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    /**
     * method for going back to previous screen upon
     * clicking the arrow icon on top left of the screen
     */
    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(PostListActivity.this, MainMenuActivity.class);
        pass.putExtra("currentUser", currentUser);
        pass.putExtra("postlist", postList);
        startActivity(pass);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    /**
     * this method will filter the post list according to user preferencess
     *
     * @param uni       : the university user chose
     * @param course    : the course user chose
     * @param lowPrice  : the lower bound of the price range user chose
     * @param highPrice : the upper bound of the price range user chose
     */
    @Override
    public void filterThePosts(String uni, String course, int lowPrice, int highPrice)
    {
        postListAdapter.filterResults(uni, course, lowPrice, highPrice);
    }

}