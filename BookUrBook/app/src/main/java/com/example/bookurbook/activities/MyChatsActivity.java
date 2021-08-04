package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.bookurbook.adapters.MyChatsAdapter;
import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Chat;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This is MyChatsActivity where users can see all of their chats.
 */
public class MyChatsActivity extends AppCompatActivity {
    //variables
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference docRef;
    private ArrayList<Chat> chatList;
    private String otherUsername;
    private RecyclerView recyclerView;
    private MyChatsAdapter myChatsAdapter;
    private ArrayList<String> blockedUsernames;
    private Toolbar toolbar;
    private SearchView searchView;
    private boolean repetitive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);

        //Initializing toolbar
        toolbar = findViewById(R.id.toolbar_my_chats);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initializing database related variables
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Initializing other variables
        searchView = findViewById(R.id.search_id_for_my_chats);
        chatList = new ArrayList<Chat>();
        buildRecyclerView();
        searchMyChats(myChatsAdapter);
        repetitive = false;
        searchView = findViewById(R.id.search_id_for_my_chats);

        //Receiving current user and blocked usernames of this user from previous activity
        if(getIntent().getSerializableExtra("currentUser") instanceof Admin)
            currentUser = (Admin)getIntent().getSerializableExtra("currentUser");
        else
            currentUser = (RegularUser)getIntent().getSerializableExtra("currentUser");
        blockedUsernames = getIntent().getStringArrayListExtra("blockedUsernames");

        //Receiving chats of current user from database
        db.collection("chats").addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                if (error != null) //Is there any error related to database
                {
                    Toast chatError = Toast.makeText(MyChatsActivity.this,"Something is wrong. Please check your Internet connection.", Toast.LENGTH_LONG);
                    chatError.show();
                    return;
                }
                //Clear chatList
                chatList = new ArrayList<Chat>();
                for (QueryDocumentSnapshot doc : value) //Find all chat documents which belong to current user
                {
                    if (doc != null && (doc.getString("username1").equals(currentUser.getUsername()) || doc.getString("username2").equals(currentUser.getUsername())))
                        {
                            //find where this user is saved and get the name of other user
                            if ( doc.getString("username1").equals(currentUser.getUsername()))
                            {
                                otherUsername = doc.getString("username2");
                            }
                            else
                            {
                                otherUsername = doc.getString("username1");
                            }
                            //find the information of other user to create object
                            db.collection("users").whereEqualTo("username", otherUsername)
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        for (DocumentSnapshot document : task.getResult())
                                        {
                                           if( !blockedUsernames.contains(document.getString("username")))
                                           { //If current user blocked other user, do not add it.
                                                // To fix a minor bug about repetition of same chats.
                                                for ( int i = 0; i < chatList.size(); i++ )
                                                {
                                                    if( chatList.get(i).getUser2().getUsername().equals(document.getString("username")))
                                                    {
                                                        repetitive = true;
                                                        break;
                                                    }
                                                    else
                                                    {
                                                        repetitive = false;
                                                    }
                                                }
                                                if( !repetitive ) // If the chat is not added before, add it
                                                {
                                                    Chat chat = new Chat(currentUser, new RegularUser(document.getString("username"),
                                                            document.getString("email"), document.getString("avatar")), doc.getId());
                                                    chat.setLastMessageContentInDB(doc.getString("lastmessage"));
                                                    chat.setDate(doc.getDate("lastmessagedate"));
                                                    if (doc.getId().indexOf(currentUser.getUsername()) == 0) //If current user is user1 in database
                                                    {
                                                        chat.setReadByUser1(doc.getBoolean("readbyuser1"));
                                                        chat.setReadByUser2(doc.getBoolean("readbyuser2"));
                                                    } else //If current user is user2 in database
                                                    {
                                                        chat.setReadByUser1(doc.getBoolean("readbyuser2"));
                                                        chat.setReadByUser2(doc.getBoolean("readbyuser1"));
                                                    }
                                                    chatList.add(chat);
                                                }
                                           }
                                        }
                                    }
                                    else //For any issue related to database
                                    {
                                        Toast chatError = Toast.makeText(MyChatsActivity.this,"Something is wrong. Please check your Internet connection.", Toast.LENGTH_LONG);
                                        chatError.show();
                                    }
                                    //Update GUI
                                    Collections.sort(chatList); //Sort chats from new to old
                                    buildRecyclerView();
                                    searchMyChats(myChatsAdapter);
                                }
                            });
                    }
                }
            }
        });
    }

    /**
     * To go back previous activity
     */
    @Override
    public void onBackPressed()
    {
        Intent pass = new Intent(MyChatsActivity.this, MainMenuActivity.class);
        pass.putExtra("currentUser", currentUser);
        startActivity(pass);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    /**
     * To update recycle view with adapter
     */
    private void buildRecyclerView()
    {
        recyclerView = findViewById(R.id.my_chats_recycler_id);
        myChatsAdapter = new MyChatsAdapter(MyChatsActivity.this, chatList, currentUser, blockedUsernames);
        recyclerView.setAdapter(myChatsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyChatsActivity.this));
        myChatsAdapter.notifyDataSetChanged();
    }

    /**
     * To search among chats by username
     * @param adp
     */
    public void searchMyChats(MyChatsAdapter adp)
    {
        final MyChatsAdapter adapter = adp;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }
}
