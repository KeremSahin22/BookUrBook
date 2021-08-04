package com.example.bookurbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookurbook.R;
import com.example.bookurbook.activities.ChatActivity;
import com.example.bookurbook.models.Chat;
import com.example.bookurbook.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for the recycler view in MyChatsActivity
 */
public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.MyChatsViewHolder> implements Filterable {

    // variables
    private ArrayList<Chat> chatsListFull;
    private ArrayList<Chat> chatsList;
    private Context context;
    private User currentUser;
    private ArrayList<String> blockedUsernames;
    private FirebaseFirestore db;

    //constructor
    public MyChatsAdapter(Context c, ArrayList<Chat> list,  User currentUser, ArrayList<String> blockedUsernames)
    {
        chatsList = list;
        chatsListFull = new ArrayList<>(chatsList);
        context = c;
        this.currentUser = currentUser;
        this.blockedUsernames = blockedUsernames;
    }


    @NonNull
    @Override
    public MyChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.chat_view, parent, false);
        return new MyChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyChatsViewHolder holder, int position) {

        Chat exampleChat = chatsList.get(position);
        holder.userName.setText(exampleChat.getUser2().getUsername());
        holder.latestChat.setText(exampleChat.getLastMessageInDB());
        Picasso.get().load(exampleChat.getUser2().getAvatar()).into(holder.userAvatar);
        if ( !exampleChat.isReadByUser1() )
        {
            holder.newMessageIcon.setVisibility(View.VISIBLE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent pass = new Intent(context, ChatActivity.class);
                if ( chatsList.size() != 0)
                {
                    pass.putExtra("currentUser", currentUser);
                    pass.putExtra("fromPostActivity",false);
                    pass.putExtra("clickedChat", exampleChat);
                    pass.putExtra("blockedUsernames", blockedUsernames);
                    context.startActivity(pass);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    // this part is new
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    // this part is new
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Chat> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0)
            {
                filteredList.addAll(chatsListFull);
            }
            else
            {
                String filterInput = charSequence.toString().toLowerCase().trim();

                for (Chat p : chatsListFull)
                {
                    if (p.getUser2().getUsername().toLowerCase().contains(filterInput))
                    {
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            chatsList.clear();
            chatsList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class MyChatsViewHolder extends RecyclerView.ViewHolder {

        // variables
        ImageView userAvatar;
        ImageView newMessageIcon;
        TextView userName;
        TextView latestChat;
        private LinearLayout layout;

        public MyChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.my_chats_avatar);
            userName = itemView.findViewById(R.id.my_chats_username);
            latestChat = itemView.findViewById(R.id.my_chats_latest_message);
            layout = (LinearLayout) itemView.findViewById(R.id.chat_layout_id);
            newMessageIcon = itemView.findViewById(R.id.new_message_icon);
        }
    }

}
