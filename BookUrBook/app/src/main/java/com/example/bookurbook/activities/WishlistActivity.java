package com.example.bookurbook.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bookurbook.R;
import com.example.bookurbook.adapters.WishlistAdapter;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;

/**
 * This class connects between Wishlist view and model classes, updating them according to actions
 */
public class WishlistActivity extends AppCompatActivity {

    //variables
    Toolbar toolbar;
    RecyclerView recyclerView;
    WishlistAdapter adapter;
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        init(); //calling the method to initialize variables
    }

    /**
     * This method will set the properties according to the currentUser
     */
    public void init(){
        //initializing variables
        toolbar = findViewById(R.id.wishlistToolbar);
        recyclerView = findViewById(R.id.wishList);

        //setting toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Wishlist");

        //accessing current user from database
        if(getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin)getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser)getIntent().getSerializableExtra("currentUser");

        //setting adapter to recycler view
        adapter = new WishlistAdapter(WishlistActivity.this, currentUser.getWishList(), currentUser);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void onBackPressed()
    {
        Intent pass = new Intent(WishlistActivity.this, MainMenuActivity.class);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}