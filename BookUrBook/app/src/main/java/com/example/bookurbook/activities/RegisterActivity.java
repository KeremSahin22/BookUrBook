package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bookurbook.MailAPISource.JavaMailAPI;
import com.example.bookurbook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

/**
 * A class for the Register Screen
 *
 * @author Veni Vidi Code
 * @version 2020 Fall
 */
public class RegisterActivity extends AppCompatActivity
{
    //properties
    private EditText txtUsername;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtRePassword;
    private Button txtRegister;
    private ArrayList<String> usernames;
    private ArrayList<String> emails;
    private final String VERIFICATION_SUBJECT = " Your BookURBook verification code";
    private final String VERIFICATION_CODE = String.format("%06d", new Random().nextInt(999999));
    private final String VERIFICATION_MAIL = "Your BookURBook verification code is " + VERIFICATION_CODE + ".";
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
        setContentView(R.layout.activity_register);
        txtUsername = findViewById(R.id.editemail);
        txtEmail = findViewById(R.id.editemail2);
        txtPassword = findViewById(R.id.editpassword2);
        txtRePassword = findViewById(R.id.editrepassword);
        txtRegister = findViewById(R.id.registerbutton2);
        db = FirebaseFirestore.getInstance();
        txtRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createNewAccount();
            }
        });
    }

    /**
     * This method validates all of the input in order to create a new account and sends a verification code eventually
     */
    private void createNewAccount()
    {
        //setting some String shortcuts in order to ease the checks
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String rePassword = txtRePassword.getText().toString();
        usernames = new ArrayList<String>();
        emails = new ArrayList<String>();

        //gets all of the users' data in order the check if the email and username already exists
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful()) // if the data pull is successful, usernames and emails will be added into ArrayLists
                {
                    for (DocumentSnapshot document : task.getResult())
                    {
                        usernames.add(document.getString("username"));
                        emails.add(document.getString("email"));
                    }
                    if (TextUtils.isEmpty(username)) //if the username field is empty, show an error message.
                    {
                        Toast noUsername = Toast.makeText(RegisterActivity.this, "Username field is empty", Toast.LENGTH_LONG);
                        noUsername.show();
                    } else if (TextUtils.isEmpty(email))  //if the email field is empty, show an error message.
                    {
                        Toast noEmail = Toast.makeText(RegisterActivity.this, "Email field is empty", Toast.LENGTH_LONG);
                        noEmail.show();
                    } else if (TextUtils.isEmpty(password))  //if the password field is empty, show an error message.
                    {
                        Toast noPassword = Toast.makeText(RegisterActivity.this, "Password field is empty", Toast.LENGTH_LONG);
                        noPassword.show();
                    } else if (password.length() < 6) //if the password is less than 6 chars, show an error message
                    {
                        Toast insufficientPasswordLength = Toast.makeText(RegisterActivity.this, "Password length cannot be less than 6!", Toast.LENGTH_LONG);
                        insufficientPasswordLength.show();
                    } else if (TextUtils.isEmpty(rePassword))  //if the re-password field is empty, show an error message.
                    {
                        Toast noRePassword = Toast.makeText(RegisterActivity.this, "Retype Password field is empty", Toast.LENGTH_LONG);
                        noRePassword.show();
                    } else if (!password.equals(rePassword)) //if password mismatch, show an error message and clear the fields.
                    {
                        Toast passwordMismatch = Toast.makeText(RegisterActivity.this, "Passwords are different", Toast.LENGTH_LONG);
                        passwordMismatch.show();
                        txtPassword.setText("");
                        txtRePassword.setText("");
                    } else if (!email.contains("@")) //if e mail is not valid, show an error.
                    {
                        Toast notEmail = Toast.makeText(RegisterActivity.this, "Wrong email", Toast.LENGTH_LONG);
                        notEmail.show();
                    } else if (!email.substring(email.indexOf("@"), email.length()).contains(".edu.tr")) //if it is not edu.tr mail, show an error
                    {
                        Toast notEdu = Toast.makeText(RegisterActivity.this, "This is not a edu.tr mail", Toast.LENGTH_LONG);
                        notEdu.show();
                    } else if(!username.matches("[a-zA-Z0-9]+")) //checks the username is alpha numerical or not
                    {
                        Toast usernameNonAlpha = Toast.makeText(RegisterActivity.this, "The username " + username + " contains non-AlphaNumerical chars! (Only Aa-zZ 0-9)", Toast.LENGTH_LONG);
                        usernameNonAlpha.show();
                        txtUsername.setText("");
                    } else if (usernames.contains(username)) //if the username exists, show an error and clear the field.
                    {
                        Toast userNameExist = Toast.makeText(RegisterActivity.this, "The username " + username + " already exists!", Toast.LENGTH_LONG);
                        userNameExist.show();
                        txtUsername.setText("");
                    } else if (emails.contains(email)) ///if the email exists, show an error and clear the field.
                    {
                        Toast emailExist = Toast.makeText(RegisterActivity.this, "The email " + email + " already exists!", Toast.LENGTH_LONG);
                        emailExist.show();
                        txtEmail.setText("");
                    }
                    else //otherwise, send verification email and transfer the data to the verification screen.
                    {
                        Toast.makeText(RegisterActivity.this, "Your verification code has been sent to your email.", Toast.LENGTH_LONG);
                        JavaMailAPI javaMailAPI = new JavaMailAPI(RegisterActivity.this, email, VERIFICATION_SUBJECT, VERIFICATION_MAIL);
                        javaMailAPI.execute();
                        Intent pass = new Intent(RegisterActivity.this, VerificationActivity.class);
                        pass.putExtra("username", username);
                        pass.putExtra("email", email);
                        pass.putExtra("password", password);
                        pass.putExtra("code", VERIFICATION_CODE);
                        startActivity(pass);
                    }
                }
            }
        });
    }

}

