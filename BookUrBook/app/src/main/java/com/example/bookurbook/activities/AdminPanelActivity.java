package com.example.bookurbook.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bookurbook.R;
import com.example.bookurbook.adapters.ReportsAdapter;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.User;

import java.util.ArrayList;

/**
 * This class connects between Admin Panel view and its adapter class(Reports Adapter), accessing and using data from database also updating them according to actions
 */
public class AdminPanelActivity extends AppCompatActivity
{
    //variables
    private RecyclerView recyclerView;
    private ReportsAdapter adapter;
    private User currentUser;
    private ArrayList<User> reportedUsers;
    Toolbar toolbar;

    //method code

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        init();

    }

    /**
     * This method will construct the variables of the class
     */
    public void init()
    {
        //setting variables
        this.recyclerView = findViewById(R.id.reportList);

        //setting toolbar
        toolbar = findViewById(R.id.toolbarblocklist);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reported Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //accessing user from database
        currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        reportedUsers = (ArrayList<User>) getIntent().getSerializableExtra("userlist");
        adapter = new ReportsAdapter(getBaseContext(), reportedUsers);
        recyclerView.setAdapter(adapter); //setting adapter for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(AdminPanelActivity.this, MainMenuActivity.class);
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