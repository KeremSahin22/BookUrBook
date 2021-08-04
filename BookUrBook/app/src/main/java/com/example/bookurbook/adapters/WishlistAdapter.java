package com.example.bookurbook.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookurbook.R;
import com.example.bookurbook.activities.PostActivity;
import com.example.bookurbook.activities.WishlistActivity;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
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
public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder>
{

    //variables
    private ArrayList<Post> posts;
    private User currentUser;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    //constructor
    public WishlistAdapter(Context context, ArrayList<Post> posts, User currentUser)
    {
        this.posts = posts;
        this.context = context;
        this.currentUser = currentUser;
    }

    /**
     * inner class to hold the properties as views
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //inner class properties
        private TextView postName, seller, price;
        private ImageView photo, likeButton;
        private LinearLayout layout;

        //inner class constructor
        public ViewHolder(View view)
        {
            super(view);
            this.postName = view.findViewById(R.id.postText);
            this.seller = view.findViewById(R.id.postSeller);
            this.price = view.findViewById(R.id.priceText);
            this.photo = view.findViewById(R.id.postImageView);
            this.likeButton = view.findViewById(R.id.like_btn);
            this.layout = view.findViewById(R.id.wishlist_layout);
        }
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.post_wishlist, parent, false);
        return new WishlistAdapter.ViewHolder(view);
    }


    /**
     * This method keeps the variables of the view layout and control them
     *
     * @param holder   holds the variables of the specific layout
     * @param position position of the array list
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //setting variables according to the element of the array list
        holder.postName.setText(posts.get(position).getTitle());
        holder.seller.setText(posts.get(position).getOwner().getUsername());
        Picasso.get().load(posts.get(position).getPicture()).into(holder.photo);
        holder.price.setText(Integer.toString(posts.get(position).getPrice()) + "₺");
        holder.likeButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will be invoked when like button is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context); //alert dialog to interact with user
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure that you want to remove " + posts.get(position).getTitle() + " from your wishlist?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        db.collection("users").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                        {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot)
                            {
                                List<String> wishlist = Collections.emptyList();
                                wishlist = (List<String>) documentSnapshot.get("wishlist"); //getting the wishlist variable from database
                                wishlist.remove(posts.get(position).getId()); //removing from the list
                                HashMap<String, Object> newData = new HashMap<>();
                                newData.put("wishlist", wishlist);
                                db.collection("users").document(auth.getCurrentUser().getUid()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Toast.makeText(context, posts.get(position).getTitle() + " has been removed from your wishlist", Toast.LENGTH_SHORT).show();
                                        currentUser.getWishList().remove(posts.get(position)); //remove the post from wishlist
                                        Intent pass = new Intent(context, WishlistActivity.class); //renewing the activity
                                        pass.putExtra("currentUser", currentUser);
                                        context.startActivity(pass);
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

        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will direct users to specific post details view when the post layout is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("post", posts.get(position));   //send the specific post from postlist
                intent.putExtra("currentUser", currentUser);   //send the currents user
                intent.putExtra("previousActivity", 3);
                PostList postlist = new PostList();

                for(int i = 0; i < posts.size(); i++)  //storing the current postlist to send it to the next screen
                {
                    postlist.addPost(posts.get(i));
                }
                intent.putExtra("postlist", postlist);
                context.startActivity(intent);
            }
        });

    }

    /**
     * method for determining the full size of the list
     * @return the size of the post list arraylist
     */
    @Override
    public int getItemCount()
    {
        return posts.size();
    }
}