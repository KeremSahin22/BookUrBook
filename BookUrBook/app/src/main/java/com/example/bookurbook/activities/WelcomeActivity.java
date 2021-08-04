package com.example.bookurbook.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A class for the Welcome Screen
 *
 * @author Veni Vidi Code
 * @version 2020 Fall
 */
public class WelcomeActivity extends AppCompatActivity
{

    //properties
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User currentUser;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() != null) //if user is already logged in
        { //find the current user's properties in database
            db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override //if finding is success
                public void onSuccess(DocumentSnapshot documentSnapshot)
                { //if the user is banned, they're logged out and gets a warning Toast.
                    if (documentSnapshot.getBoolean("banned"))
                    {
                        Toast.makeText(WelcomeActivity.this, "This user has been banned from BookUrBook!", Toast.LENGTH_LONG).show();
                        Intent banned = new Intent(WelcomeActivity.this, WelcomeActivity.class);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(banned);
                        finish();
                    } else
                    { //if user is not banned. It sets the currentUser object according to it's properties. And Sends them to the main menu.
                        if (documentSnapshot.getBoolean("admin"))
                            currentUser = new Admin(documentSnapshot.getString("username"), documentSnapshot.getString("email"), null);
                        else
                            currentUser = new RegularUser(documentSnapshot.getString("username"), documentSnapshot.getString("email"), null);
                        if (documentSnapshot.getString("avatar") != null)
                        {
                            currentUser.setAvatar(documentSnapshot.getString("avatar"));
                        } else
                            currentUser.setAvatar("https://firebasestorage.googleapis.com/v0/b/bookurbook-a02e4.appspot.com/o/images%2Fprofile_pictures%2Fdefault.jpg?alt=media&token=a54505f6-0d24-40cd-a626-e39a655254c6");

                        Intent pass = new Intent(WelcomeActivity.this, MainMenuActivity.class);
                        pass.putExtra("currentUser", currentUser);
                        startActivity(pass);
                    }
                }
            });
        } else //if the user is not logged in, they're transferred to the login screen after 3 secs of waiting.
        {
            new CountDownTimer(3000, 1000)
            {
                public void onTick(long millisUntilFinished)
                {
                    //DO NOTHING
                }

                public void onFinish()
                {
                    Intent pass = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(pass);
                }
            }.start();
        }
    }
}
