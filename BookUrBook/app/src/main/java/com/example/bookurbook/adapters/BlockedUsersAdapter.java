package com.example.bookurbook.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookurbook.R;
import com.example.bookurbook.activities.MyBlockListActivity;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
/**
 * This class provides connection between the view and the array list which will be used for the recycler view and update them according to actions
 */
public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ViewHolder>
{
    //variables
    private ArrayList<User> blockedUsers;
    private User currentUser;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    //constructor
    public BlockedUsersAdapter(Context context, ArrayList<User> users, User currentUser)
    {
        this.blockedUsers = users;
        this.context = context;
        this.currentUser = currentUser;
    }

    /**
     * inner class to hold the properties as views
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //inner class properties
        private TextView username;
        private ImageView photo;
        private ImageView unblockButton;

        //inner class constructor
        public ViewHolder(View view)
        {
            super(view);
            //setting variables to view elements
            this.username = view.findViewById(R.id.blocked_username);
            this.photo = view.findViewById(R.id.image_user);
            this.unblockButton = view.findViewById(R.id.btn_unblock);
        }
    }


    @NonNull
    @Override
    public BlockedUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_blocklist, parent, false);
        return new BlockedUsersAdapter.ViewHolder(view);
    }

    /**
     * This method keeps the variables of the view layout and control them
     *
     * @param holder   holds the variables of the specific layout
     * @param position position of the array list
     */
    @Override
    public void onBindViewHolder(@NonNull BlockedUsersAdapter.ViewHolder holder, int position)
    {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        holder.username.setText(blockedUsers.get(position).getUsername());
        Picasso.get().load(blockedUsers.get(position).getAvatar()).into(holder.photo); //accessing the user avatar from database
        holder.unblockButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will unblock the user when unblock button is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                //creating alert to unblock the user
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure that you want to unblock " + blockedUsers.get(position).getUsername() + "?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot)
                            {
                                List<String> blockedUsernames = Collections.emptyList();
                                blockedUsernames = (List<String>) documentSnapshot.get("blockedusers");
                                blockedUsernames.remove(blockedUsers.get(position).getUsername()); //remove the user from database
                                HashMap<String, Object> newData = new HashMap<>();
                                newData.put("blockedusers", blockedUsernames);
                                db.collection("users").document(auth.getCurrentUser().getUid()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Toast.makeText(context, blockedUsers.get(position).getUsername() + "'s block has been removed", Toast.LENGTH_SHORT).show();
                                        currentUser.getBlockedUsers().remove(blockedUsers.get(position)); //remove the user from blocked users list
                                        Intent pass = new Intent(context, MyBlockListActivity.class); //renew the activity
                                        pass.putExtra("currentUser", currentUser);
                                        context.startActivity(pass);
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });


                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

    }
    /**
     * method for determining the full size of the list
     * @return the size of the post list arraylist
     */
    @Override
    public int getItemCount() {
        return blockedUsers.size();
    }

}
