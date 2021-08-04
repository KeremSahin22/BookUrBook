package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookurbook.fragments.FeedbackDialog;
import com.example.bookurbook.fragments.FeedbackDialogListener;
import com.example.bookurbook.MailAPISource.JavaMailAPI;
import com.example.bookurbook.R;
import com.example.bookurbook.SendNotificationPack.Token;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This class connects between Settings view and model classes, accessing and using data from database also updating them according to actions
 */
public class SettingsActivity extends AppCompatActivity implements FeedbackDialogListener
{
    //variables
    private Button logout;
    private Button select;
    private Button blocklist;
    private Button resetPass;
    private TextView userDetails;
    private ImageView profilePic;
    private Uri imageUri;
    private User currentUser;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private Intent pass;
    private Toolbar toolbar;
    private Button sendFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }
    public void init()
    {
        //setting variables
        logout = findViewById(R.id.logout);
        select = findViewById(R.id.selectImage);
        blocklist = findViewById(R.id.blocked_users);
        resetPass = findViewById(R.id.resetPassword);
        profilePic = findViewById(R.id.profilepic);
        userDetails = findViewById(R.id.userdetails);
        sendFeedback = findViewById(R.id.sendFeedback);
        toolbar = findViewById(R.id.settingsToolbar);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

       //setting toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //accessing current user from database
        if(getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin)getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser)getIntent().getSerializableExtra("currentUser");

        Picasso.get().load(currentUser.getAvatar()).into(profilePic);

        //set the text according to user type
        if(currentUser instanceof Admin)
            userDetails.setText(currentUser.getUsername()+ "\n" + "Admin User");
        else
            userDetails.setText(currentUser.getUsername());

        logout.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                db.collection("tokens").document(auth.getUid()).set(new Token(""));
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent (SettingsActivity.this, LoginActivity.class));         //direct user to login screen
                finish();
            }
        });

        select.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                choosePicture();     //calling the method
            }
        });
        blocklist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
             * This method will store the blocked users list and show them on My Blocklist view
             */
            public void onClick(View v)
            {
                currentUser.setBlockedUsers(new ArrayList<User>());
            db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                List<String> blockedUsernames = Collections.emptyList();
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        blockedUsernames = (List<String> )documentSnapshot.get("blockedusers");      //get the bloclist from database
                        if(blockedUsernames.size() == 0)
                        {
                            Intent pass = new Intent(SettingsActivity.this, MyBlockListActivity.class);
                            pass.putExtra("currentUser", currentUser);
                            startActivity(pass);      //passing My Blocklist view
                            finish();
                        }
                        for(int i = 0; blockedUsernames.size() > i; i++)
                        {
                            db.collection("users").whereEqualTo("username", blockedUsernames.get(i)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    for(DocumentSnapshot doc : task.getResult())
                                    {
                                        User toBeAdded;
                                        //creating the blocked user according to type
                                        if(doc.getBoolean("admin"))
                                            toBeAdded = new Admin(doc.getString("username"), doc.getString("email"), doc.getString("avatar"));
                                        else
                                            toBeAdded = new RegularUser(doc.getString("username"), doc.getString("email"), doc.getString("avatar"));
                                        if(!currentUser.getBlockedUsers().contains(toBeAdded))
                                           currentUser.blockUser(toBeAdded);
                                    }
                                    Intent pass = new Intent(SettingsActivity.this, MyBlockListActivity.class);    //direct user to My Blocklist view
                                    pass.putExtra("currentUser", currentUser);
                                    startActivity(pass);
                                    finish();

                                }
                            });
                        }
                    }
                });
            }
        });

        resetPass.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will reset the password when reset password button is clicked
             * @param v
             */
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Are you sure?");
                builder.setMessage("If you click on YES, you will be logged out and a link will " +
                        "be sent to your e-mail in order for you to reset your password.");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        auth.sendPasswordResetEmail(currentUser.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                        if(task.isSuccessful())
                                        {
                                            auth.signOut();
                                            Toast.makeText(SettingsActivity.this,
                                                    "Your link for resetting your password has been sent to "
                                                            + currentUser.getEmail(), Toast.LENGTH_LONG).show();
                                            Intent pass = new Intent(SettingsActivity.this, WelcomeActivity.class);
                                            startActivity(pass);       //after sending link to the user's mail, direct user to welcome screen
                                        }
                                        else
                                        {
                                            Toast.makeText(SettingsActivity.this,
                                                    "There has been an error! Check your internet connection."
                                                            + currentUser.getEmail(), Toast.LENGTH_LONG).show();   //error message if the task is not successful
                                        }
                                }
                            });
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        sendFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                openFeedbackDialog();
            }
        });
    }

    /**
     * This method will enable users to choose their avatars
     */
    private void choosePicture()
    {
        Intent galleryOpen = new Intent();
        galleryOpen.setType("image/*");
        galleryOpen.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryOpen, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            uploadPic();  //calling the method when picture is chosen from user's gallery
        }


    }

    /**
     * This method enables users to upload their new avatars to the app
     */
    private void uploadPic()
    {
        StorageReference picRef = storageReference.child("images/profile_pictures/" + auth.getCurrentUser().getUid());
        picRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        // Get a URL to the uploaded content
                        Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
                        picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                        {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                Picasso.get().load(uri).into(profilePic);    //loading new picture to database variable
                                HashMap<String, Object> newData = new HashMap();
                                newData.put("avatar", uri.toString());
                                currentUser.setAvatar(uri.toString());    //setting new picture as users avatar
                                db.collection("users").document(auth.getCurrentUser().getUid()).set(newData, SetOptions.merge());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG).show(); //information of the current status
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        pass = new Intent(SettingsActivity.this, MainMenuActivity.class);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is created in order to create a pop up dialog using FeedbackDialog class.
     */
    public void openFeedbackDialog()
    {
        FeedbackDialog dialog = new FeedbackDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    /**
     * When the feedback dialog is created, the listener inside the FeedbackDialog class will call this method and therefore
     * we will be able to access the description and the category from the post activity that was provided in the dialog.
     * This method gets the necessary info and sends the feedback mail to our (Veni Vidi Code) mail.
     * @param description the description of the feedback provided by the user
     */
    @Override
    public void applyTexts(String description)
    {
        JavaMailAPI mail = new JavaMailAPI(SettingsActivity.this
                , "vvcbookurbook@gmail.com", currentUser.getUsername()
                + "'s Feedback", description );
        mail.execute();
    }
}
