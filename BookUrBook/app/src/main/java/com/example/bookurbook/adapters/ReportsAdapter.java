package com.example.bookurbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookurbook.R;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder>
{
    //variables
    private ArrayList<User> reportedUsers;
    private Admin user;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    //constructor
    public ReportsAdapter(Context context, ArrayList<User> users)
    {
        this.reportedUsers = users;
        this.context = context;
    }

    /**
     * inner class to hold the properties as views
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //inner class properties
        private TextView username, reportNumber;
        private ImageView photo, bannedView;
        private ImageButton bannedButton;

        //inner class constructor
        public ViewHolder(View view)
        {
            super(view);
            this.username = view.findViewById(R.id.blocked_username);
            this.photo = view.findViewById(R.id.image_user);
            this.bannedButton = view.findViewById(R.id.btn_ban);
            this.bannedView = view.findViewById(R.id.bannedPhoto);
            this.reportNumber = view.findViewById(R.id.report_number);
        }
    }


    @NonNull
    @Override
    public ReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_report, parent, false);
        return new ReportsAdapter.ViewHolder(view);
    }

    /**
     * This method keeps the variables of the view layout and control them
     *
     * @param holder   holds the variables of the specific layout
     * @param position position of the array list
     */
    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ViewHolder holder, int position)
    {
        //setting variables according to the element of the array list
        holder.username.setText(reportedUsers.get(position).getUsername());
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Picasso.get().load(reportedUsers.get(position).getAvatar()).into(holder.photo);
        holder.bannedView.setImageResource(R.drawable.banned);

        holder.reportNumber.setText((reportedUsers.get(position).getReportNum() + ""));

        if (reportedUsers.get(position).isBanned())  //if the user has been already banned, bannedView appears
            holder.bannedView.setVisibility(View.VISIBLE);
        else
            holder.bannedView.setVisibility(View.INVISIBLE);

        holder.reportNumber.setText("Report Number: " + (reportedUsers.get(position).getReportNum()));

        holder.bannedButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will set the user as banned or not when bannedButton is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                if (reportedUsers.get(position).isBanned())  //if the user is already banned
                {
                    reportedUsers.get(position).setBanned(false); //setting user's ban variable
                    holder.bannedView.setImageResource(R.drawable.banned);
                    holder.bannedView.setVisibility(View.INVISIBLE); //setting bannedView as inivisible
                    Toast.makeText(context, reportedUsers.get(position).getUsername() + "'s ban has been removed", Toast.LENGTH_SHORT).show();
                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            String id;
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                if (doc.getString("username").equals(reportedUsers.get(position).getUsername())) {
                                    id = doc.getId();
                                    HashMap<String, Object> newData = new HashMap<>();
                                    newData.put("banned", false); //setting database variable as banned
                                    db.collection("users").document(id).set(newData, SetOptions.merge());
                                }
                            }
                        }
                    });
                }
                else
                    {
                    reportedUsers.get(position).setBanned(true); //if the user is not banned
                    holder.bannedView.setImageResource(R.drawable.banned);
                    holder.bannedView.setVisibility(View.VISIBLE); //setting bannedView as visible
                    Toast.makeText(context, reportedUsers.get(position).getUsername() + " has been banned", Toast.LENGTH_SHORT).show();
                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for(QueryDocumentSnapshot doc : task.getResult())

                            {
                                if (doc.getString("username").equals(reportedUsers.get(position).getUsername()))
                                {
                                    db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                    {
                                        String id;
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot)
                                        {
                                            id = doc.getId();
                                            HashMap<String, Object> newData = new HashMap<>();
                                            user = new Admin(documentSnapshot.getString("username"), documentSnapshot.getString("email"), documentSnapshot.getString("avatar"));
                                            user.ban(reportedUsers.get(position));
                                            newData.put("banned", true);
                                            db.collection("users").document(id).set(newData, SetOptions.merge());
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * method for determining the full size of the list
     *
     * @return the size of the post list arraylist
     */
    @Override
    public int getItemCount()
    {
        return reportedUsers.size();
    }

}
