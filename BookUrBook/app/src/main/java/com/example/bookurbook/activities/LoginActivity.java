package com.example.bookurbook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookurbook.R;
import com.example.bookurbook.SendNotificationPack.Token;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A class for the Login screen
 *
 * @author Veni Vidi Code
 * @version 2020 Fall
 */
public class LoginActivity extends AppCompatActivity
{

    //properties
    private Button login;
    private Button register;
    private EditText email;
    private EditText password;
    private TextView passwordForget;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private User currentUser;
    private FirebaseFirestore db;

    /**
     * This method sets the activity on create by overriding AppCompatActivity's onCreate method.
     *
     * @param savedInstanceState - Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.loginbutton);
        register = findViewById(R.id.registerbutton);
        email = findViewById(R.id.editemail);
        password = findViewById(R.id.editpassword);
        passwordForget = findViewById(R.id.forgotmypass);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        //what happens on click on login button
        login.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loginUser(); //logins the user by checking
            }
        });

        //It transfers the user to the Register Screen
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pass = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(pass);
            }
        });

        //If the user forgets their password, they will be sent to the Forgotten Password Screen
        passwordForget.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, ForgottenPasswordActivity.class));
            }
        });
    }

    /**
     * This method validates entered input in order for the user to login.
     */
    private void loginUser()
    {
        if (TextUtils.isEmpty(email.getText().toString())) //checks whether the email field is empty
            Toast.makeText(this, "Email field is empty", Toast.LENGTH_LONG).show();
        else if (TextUtils.isEmpty(password.getText().toString())) //check whether the password field is empty
            Toast.makeText(this, "Password field is empty", Toast.LENGTH_LONG).show();
        else if (!email.getText().toString().contains("@")) //checks whether the email is valid or not
            Toast.makeText(this, "Wrong email", Toast.LENGTH_LONG).show();
        else if (!email.getText().toString().substring(email.getText().toString().indexOf("@"), email.getText().toString().length()).contains(".edu.tr")) //checks whether the mail is edu.tr mail or not
            Toast.makeText(this, "This is not a edu.tr mail", Toast.LENGTH_LONG).show();
        else
        {
            //if everything seems right, Firebase authentication takes it over in order to check the password and internet connection
            auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) //if user logs in, necessary database calls are made here.
                    {
                        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) //if database can reach the data it checks whether the user banned or not
                            {
                                if (documentSnapshot.getBoolean("banned")) //if the user is banned, they will be kicked out.
                                {
                                    Toast.makeText(LoginActivity.this, "This user has been banned from BookUrBook!", Toast.LENGTH_LONG).show();
                                    Intent banned = new Intent(LoginActivity.this, WelcomeActivity.class);
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(banned);
                                    finish();
                                } else //if everything is fine sets the currentUser object with necessary data from database.
                                {
                                    db.collection("tokens").document(auth.getUid()).set(new Token(FirebaseInstanceId.getInstance().getToken()));
                                    if (documentSnapshot.getBoolean("admin"))
                                        currentUser = new Admin(documentSnapshot.getString("username"), documentSnapshot.getString("email"), null);
                                    else
                                        currentUser = new RegularUser(documentSnapshot.getString("username"), documentSnapshot.getString("email"), null);
                                    storage.getReference().child("images/profile_pictures/" + auth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri uri) //if the user has already had an avatar, it will be set.
                                        {
                                            currentUser.setAvatar(uri.toString());
                                            Intent pass = new Intent(LoginActivity.this, MainMenuActivity.class);
                                            pass.putExtra("currentUser", currentUser);
                                            startActivity(pass);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e) //if they do not have an avatar, they will be given a default avatar.
                                        {
                                            storage.getReference().child("images/profile_pictures/default.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                            {
                                                @Override
                                                public void onSuccess(Uri uri)
                                                {
                                                    currentUser.setAvatar(uri.toString());
                                                    Intent pass = new Intent(LoginActivity.this, MainMenuActivity.class);
                                                    pass.putExtra("currentUser", currentUser);
                                                    startActivity(pass);
                                                    finish();
                                                }
                                            });
                                        }
                                    });

                                }
                            }
                        });
                        Toast.makeText(LoginActivity.this, "Login is successful!", Toast.LENGTH_LONG).show();
                    } else
                    {
                        Toast.makeText(LoginActivity.this, "OOPS! Login is not successful!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    /** This method literally does nothing on the BackButton pressed. If we did not override it,
     * it's super call would make the application buggy!
     *
     */
    @Override
    public void onBackPressed()
    {
        //DO NOTHING!
    }
}