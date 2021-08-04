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

import com.example.bookurbook.adapters.BlockedUsersAdapter;
import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;

/**
 * This class connects between MyBlocklist view, its adapter class and model classes, accessing and using data from database also updating them according to actions
 */
public class MyBlockListActivity extends AppCompatActivity {

    //variables
    private User currentUser;
    private RecyclerView blockList;
    private ImageButton homeButton;
    private BlockedUsersAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_blocklist);

       init();
    }

    /**
     * This method will construct the variables of the class and setting actions to them
     */
    public void init()
    {
        //setting variables
        toolbar = findViewById(R.id.toolbarblocklist);
        homeButton = findViewById(R.id.homeButton);
        this.blockList = findViewById(R.id.blockList);

        if(getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin)getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser)getIntent().getSerializableExtra("currentUser");

        //setting toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My Blocklist");

        homeButton.setOnClickListener(new View.OnClickListener()  //direct user to the main screen
        {
            @Override
            public void onClick(View v)
            {
                Intent startIntent = new Intent(MyBlockListActivity.this, MainMenuActivity.class);
                startIntent.putExtra("currentUser" , currentUser);
                startActivity(startIntent);
            }
        });
        adapter = new BlockedUsersAdapter(MyBlockListActivity.this, currentUser.getBlockedUsers(), currentUser);
        blockList.setAdapter(adapter); //setting adapter to the recycler view
        blockList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(MyBlockListActivity.this, SettingsActivity.class);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass); //pass the back screen
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}