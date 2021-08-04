package com.example.bookurbook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookurbook.MailAPISource.JavaMailAPI;
import com.example.bookurbook.adapters.MessageAdapter;
import com.example.bookurbook.R;
import com.example.bookurbook.fragments.ReportDialog;
import com.example.bookurbook.fragments.ReportPostDialogListener;
import com.example.bookurbook.SendNotificationPack.APIService;
import com.example.bookurbook.SendNotificationPack.Client;
import com.example.bookurbook.SendNotificationPack.Data;
import com.example.bookurbook.SendNotificationPack.MyResponse;
import com.example.bookurbook.SendNotificationPack.NotificationSender;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Chat;
import com.example.bookurbook.models.Message;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This is the ChatActivity where user can send messages to or get messages from a certain user.
 */
public class ChatActivity extends AppCompatActivity implements ReportPostDialogListener
{
    //variables
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private CollectionReference msgRef;
    private Chat currentChat;
    private ArrayList<Message> messages;
    private Date date;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ImageView sendButton;
    private EditText messageBox;
    private Toolbar toolbar;
    private ImageButton homeButton;
    private ImageButton reportButton;
    private ImageButton blockButton;
    private APIService apiService;
    private String otherUserID;
    private ArrayList<String> toBePassed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Initializing database related variables
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        //Receiving currentUser from previous activity
        if (getIntent().getSerializableExtra("currentUser") instanceof Admin)
        {
            currentUser = (Admin) getIntent().getSerializableExtra("currentUser");
        } else
        {
            currentUser = (RegularUser) getIntent().getSerializableExtra("currentUser");
        }

        //Initializing other variables
        currentChat = (Chat) getIntent().getSerializableExtra("clickedChat");
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        sendButton = findViewById(R.id.send_message_button);
        messageBox = findViewById(R.id.message_box);
        homeButton = findViewById(R.id.homeButton);
        reportButton = findViewById(R.id.reportButton);
        blockButton = findViewById(R.id.blockButton);

        //Initializing database
        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat with " + currentChat.getUser2().getUsername());

        //Receiving all messages of this chat.
        msgRef = db.collection("messages").document(currentChat.getChatID()).collection("messagetree");
        msgRef.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error)
            {
                if (error != null) //If an error occurs related to database
                {
                    Toast chatError = Toast.makeText(ChatActivity.this, "Something is wrong. Please check your Internet connection.", Toast.LENGTH_LONG);
                    chatError.show();
                    return;
                }
                //Starts to add all messages one by one
                messages = new ArrayList<Message>();
                for (QueryDocumentSnapshot doc : value)
                {
                    Message msgData = new Message(doc.getString("sendBy"), doc.getString("content"),
                            dateFormat.format(doc.getDate("date")), timeFormat.format(doc.getDate("date")));
                    msgData.setDate(doc.getDate("date"));
                    messages.add(msgData);
                }
                //Create gui by using messages
                Collections.sort(messages); //Before creating gui, sorts messages from old to new
                buildRecyclerView();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1); //To show latest messages
            }
        });

        /**
         * Send a message to database when this button is clicked. Also, sends notification to other user.
         */
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //First access the list of blocked users who are blocked by other user
                db.collection("users").whereEqualTo("username", currentChat.getUser2().getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (DocumentSnapshot doc : task.getResult()) //create the list of blocked users by other user
                            {
                                List<String> usersBlockedByOther = (List<String>) doc.get("blockedusers");
                                if (usersBlockedByOther != null && usersBlockedByOther.contains(currentUser.getUsername())) //if other user blocked current user
                                {
                                    Toast chatError = Toast.makeText(ChatActivity.this, "You are blocked by this user.", Toast.LENGTH_LONG);
                                    chatError.show();
                                } else // If currentUser is not blocked by other user
                                {
                                    date = new Date();
                                    Message message = new Message(currentUser.getUsername(), messageBox.getText().toString(), dateFormat.format(date), timeFormat.format(date));
                                    if (!messageBox.getText().toString().equals("")) //Don't send empty messages to database
                                    {
                                        sendMessageToDatabase(message, date); //Updates database
                                        messageBox.setText("");

                                        //After sending message to database, send notification to other user
                                        db.collection("users").whereEqualTo("username", currentChat.getUser2().getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task)
                                            {
                                                if (task != null)
                                                {
                                                    for (DocumentSnapshot document : task.getResult()) //This should be for loop but it
                                                    {                                                  //only contains other user
                                                        otherUserID = document.getId();
                                                    }

                                                    //after getting ID of other user, get it's device token and send notification.
                                                    db.collection("tokens").document(otherUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                                    {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot)
                                                        {
                                                            sendNotifications(documentSnapshot.get("token").toString(), "You have a new message", currentUser.getUsername() + " has sent you a message.");
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });

        /**
         * To scroll down all messages to last message
         */
        messageBox.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
        });

        /**
         * To go back to main menu
         */
        homeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent startIntent = new Intent(ChatActivity.this, MainMenuActivity.class);
                //Updates database before going back. Updates database in here.
                if (currentChat.getChatID().indexOf(currentUser.getUsername()) == 0)
                {
                    db.collection("chats").document(currentChat.getChatID()).update("readbyuser1", true);
                } else
                {
                    db.collection("chats").document(currentChat.getChatID()).update("readbyuser2", true);
                }
                startIntent.putExtra("currentUser", currentUser);
                startActivity(startIntent);
            }
        });

        /**
         * To report the other user.
         */
        reportButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openPostReportDialog();
            }
        });

        /**
         * To block the other user.
         */
        blockButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Update the blocked users list of this user in database
                db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot)
                    {
                        List<String> blockedUsernames = Collections.emptyList();
                        blockedUsernames = (List<String>) documentSnapshot.get("blockedusers");
                        blockedUsernames.add(currentChat.getUser2().getUsername());
                        if (!((boolean) getIntent().getExtras().get("fromPostActivity")))
                        {   //if previous activity is PostActivity, do not update the model class for now.
                            toBePassed = getIntent().getStringArrayListExtra("blockedUsernames");
                            toBePassed.add(currentChat.getUser2().getUsername());
                        }
                        HashMap<String, Object> newData = new HashMap<>();
                        newData.put("blockedusers", blockedUsernames);
                        //Update database
                        db.collection("users").document(auth.getCurrentUser().getUid()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(ChatActivity.this, currentChat.getUser2().getUsername() + " has been blocked.", Toast.LENGTH_SHORT).show();
                                //Send user to different activities depending on his previous activity
                                if ((boolean) getIntent().getExtras().get("fromPostActivity"))
                                {
                                    Intent pass = new Intent(ChatActivity.this, MainMenuActivity.class);
                                    pass.putExtra("currentUser", currentUser);
                                    startActivity(pass);
                                } else
                                {
                                    Intent pass = new Intent(ChatActivity.this, MyChatsActivity.class);
                                    pass.putExtra("currentUser", currentUser);
                                    pass.putExtra("blockedUsernames", toBePassed);
                                    startActivity(pass);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Updates recyclerView with adapter
     */
    private void buildRecyclerView()
    {
        recyclerView = findViewById(R.id.my_chats_recycler_id);
        messageAdapter = new MessageAdapter(ChatActivity.this, messages, currentChat);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        messageAdapter.notifyDataSetChanged();
    }

    /**
     * Updates chats part and messages part in database
     * @param msg message will be send
     * @param msgdate date of the message
     */
    public void sendMessageToDatabase(Message msg, Date msgdate)
    {
        Map<String, Object> data = new HashMap<>(); //for messages collection in database
        Map<String, Object> chatData = new HashMap<>(); //for chats collection in database
        data.put("sendBy", msg.getSentBy());
        data.put("content", msg.getContent());
        data.put("date", msgdate);
        chatData.put("lastmessage", msg.getContent());
        chatData.put("lastmessagedate", msgdate);
        if (currentChat.getChatID().indexOf(currentUser.getUsername()) == 0) //Checks if current user is user1 in database
        {
            chatData.put("readbyuser1", true);
            chatData.put("readbyuser2", false);
        }
        else //if current user is user2 in database
        {
            chatData.put("readbyuser1", false);
            chatData.put("readbyuser2", true);
        }
        //updates database with HashMaps
        msgRef.add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() //messages collection
        {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task)
            {
                db.collection("chats").document(currentChat.getChatID()) //chat collection
                        .update(chatData).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        System.out.println("Database updated.");
                    }
                });
            }
        });
    }

    public void openPostReportDialog()
    {
        ReportDialog dialog = new ReportDialog();
        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void applyTexts(String description, String category)
    {
        currentChat.getUser2().report(description, category);
        currentChat.getUser2().setReportNum(currentUser.getReportNum() + 1);
        String reportDetails = "";
        for (int i = 0; messages.size() > i; i++)
        {
            if (messages.get(i).getSentBy().equals(currentChat.getUser1().getUsername()))
                reportDetails = reportDetails + currentChat.getUser1().getUsername() + ": " + messages.get(i).getContent() + " \uD83D\uDD52 sent in " + messages.get(i).getMessageDate() + "\n\n";
            else
                reportDetails = reportDetails + currentChat.getUser2().getUsername() + ": " + messages.get(i).getContent() + " \uD83D\uDD52 sent in " + messages.get(i).getMessageDate() + "\n\n";
        }
        reportDetails = reportDetails + " This report is categorized as " + category + " and described as " + description + ".";
        JavaMailAPI reportPost = new JavaMailAPI(ChatActivity.this, "vvcbookurbook@gmail.com", currentChat.getUser2().getUsername() + " CHAT REPORT", reportDetails);
        reportPost.execute();
        db.collection("users").whereEqualTo("username", currentChat.getUser2().getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                for (DocumentSnapshot doc : task.getResult())
                {
                    String reportedUserID = doc.getId();
                    List<String> reporters = (List<String>) doc.get("reporters");
                    if(!reporters.contains(currentUser.getUsername()))
                    {
                        reporters.add(currentUser.getUsername());
                    }
                    HashMap<String, Object> newData = new HashMap<>();
                    newData.put("reporters", reporters);
                    db.collection("users").document(reportedUserID).set(newData, SetOptions.merge());
                }

            }
        });
    }

    /**
     * To send notification to other user by using database and SendNotificationPack
     * @param usertoken device token of  other user
     * @param title notification title
     * @param message notification message
     */
    public void sendNotifications(String usertoken, String title, String message)
    {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>()
        {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response)
            {
                if (response.code() == 200) // This means successful response
                {
                    if (response.body().success != 1)
                    {
                        Toast.makeText(ChatActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t)
            {

            }
        });
    }

    /**
     * To go back to previous activity
     */
    @Override
    public void onBackPressed()
    {
        Intent pass;
        if ((boolean) getIntent().getExtras().get("fromPostActivity")) // if the previous activity is PostActivity
        {
            pass = new Intent(ChatActivity.this, PostActivity.class);
            pass.putExtra("postlist", (PostList) getIntent().getSerializableExtra("postlist"));
            pass.putExtra("post", (Post) getIntent().getSerializableExtra("post"));
            pass.putExtra("fromPostList", true);
            pass.putExtra("previousActivity", (Integer) getIntent().getExtras().get("previousActivity"));
        }
        else // if the previous activity is MyChatsActivity
        {
            pass = new Intent(ChatActivity.this, MyChatsActivity.class);
        }
        pass.putExtra("currentUser", currentUser);
        pass.putExtra("blockedUsernames", getIntent().getStringArrayListExtra("blockedUsernames"));

        //update read info of messages in database
        if (currentChat.getChatID().indexOf(currentUser.getUsername()) == 0)
        {
            db.collection("chats").document(currentChat.getChatID()).update("readbyuser1", true);
        } else
        {
            db.collection("chats").document(currentChat.getChatID()).update("readbyuser2", true);
        }
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