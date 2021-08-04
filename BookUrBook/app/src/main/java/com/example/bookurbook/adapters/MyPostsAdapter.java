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
import com.example.bookurbook.activities.EditPostActivity;
import com.example.bookurbook.activities.PostActivity;
import com.example.bookurbook.models.Admin;
import com.example.bookurbook.models.Post;
import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.RegularUser;
import com.example.bookurbook.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class provides connection between the view and the array list which will be used for the recycler view and update them according to actions
 */
public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ViewHolder>
{

    //variables
    private ArrayList<Post> myPosts;
    private Context context;
    private FirebaseFirestore db;


    //constructor
    public MyPostsAdapter(Context context, ArrayList<Post> posts)
    {
        myPosts = posts;
        this.context = context;
    }

    /**
     * inner class to hold the properties as views
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        //inner class properties
        private TextView title, seller, price;
        private ImageView photo;
        private ImageView soldPhoto, tickSold;
        private LinearLayout layout;
        private ImageView editButton;


        //inner class constructor
        public ViewHolder(View view)
        {
            super(view); //calling super

            //initialize variables
            this.title = view.findViewById(R.id.postText);
            this.seller = view.findViewById(R.id.postSeller);
            this.price = view.findViewById(R.id.priceText);
            this.photo = view.findViewById(R.id.postImageView);
            this.soldPhoto = view.findViewById(R.id.soldView);
            this.layout = (LinearLayout) view.findViewById(R.id.singlePostLayout);
            this.editButton = view.findViewById(R.id.edit_button);
            this.tickSold = view.findViewById(R.id.sold_tick);
        }
    }


    @NonNull
    @Override
    public MyPostsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.posts_layout, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method keeps the variables of the view layout and control them
     *
     * @param holder   holds the variables of the specific layout
     * @param position position of the array list
     */
    @Override
    public void onBindViewHolder(@NonNull MyPostsAdapter.ViewHolder holder, int position)
    {

        db = FirebaseFirestore.getInstance();

        if (myPosts.get(position).isSold()) //if the post is sold, the sold photo will be visible
        {
            holder.soldPhoto.setImageResource(R.drawable.sold);
            holder.tickSold.setImageResource(R.drawable.unmark);
        }

        //setting the variables of the layout according to the arraylist element
        holder.title.setText(myPosts.get(position).getTitle());
        holder.seller.setText(myPosts.get(position).getOwner().getUsername());
        Picasso.get().load(myPosts.get(position).getPicture()).into(holder.photo);
        holder.price.setText(Integer.toString(myPosts.get(position).getPrice()) + "₺");
        holder.layout.setOnClickListener(new View.OnClickListener()
        {
            /**
             * The method will send to the activity details when layout is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                User currentUser;
                PostList postlist;

                Intent intent = new Intent(context, PostActivity.class);
                intent.putExtra("post", myPosts.get(position));
                if (myPosts.get(position).getOwner() instanceof Admin) {
                    currentUser = (Admin) myPosts.get(position).getOwner();

                }
                else {
                    currentUser = (RegularUser) myPosts.get(position).getOwner();


                }
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("previousActivity", 2);

               postlist = new PostList();
                //store the post to the postlist
                for (int i = 0; i < myPosts.size(); i++)
                {
                    postlist.addPost(myPosts.get(i));
                }
                intent.putExtra("postlist", postlist);
                context.startActivity(intent); //starting activity

            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method sends the user to EditPostActivity when edit button is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                User currentUser;
                PostList pass;
                Intent intent2 = new Intent(context, EditPostActivity.class);
                intent2.putExtra("post", myPosts.get(position));

                if (myPosts.get(position).getOwner() instanceof Admin) {
                    currentUser = (Admin) myPosts.get(position).getOwner();
                } else {
                    currentUser = (RegularUser) myPosts.get(position).getOwner();
                }
                intent2.putExtra("currentUser", currentUser);

                pass = new PostList();
                for (int i = 0; myPosts.size() > i; i++) //adding my posts to the pass
                {
                    pass.addPost(myPosts.get(i));
                }
                intent2.putExtra("postlist", pass);
                context.startActivity(intent2);
            }
        });

        holder.tickSold.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method will set the post as sold when sold button is clicked
             * @param view layout
             */
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context); //initialize alert builder to interact with user
                builder.setTitle("Confirm");
                if (!myPosts.get(position).isSold()) //if it is not sold, change its current status
                {
                    builder.setMessage("Are you sure that you want to mark the Post as sold?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            HashMap<String, Object> newData = new HashMap<>();
                            newData.put("sold", true); //set the database variable to sold
                            db.collection("posts").document(myPosts.get(position).getId()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                /**
                                 * This method will set the post as sold and controlling necessary changes on the view
                                 * @param aVoid void
                                 */
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    PostList postList = new PostList();
                                    postList.setPostArray(myPosts);
                                    postList.postSold(myPosts.get(position)); //set the post in the arraylist
                                    Toast.makeText(context, "You have successfully sold your Post!", Toast.LENGTH_SHORT).show();
                                    holder.soldPhoto.setImageResource(R.drawable.sold); //set the image as visible
                                    holder.soldPhoto.setVisibility(View.VISIBLE);
                                    holder.tickSold.setImageResource(R.drawable.unmark);
                                    dialog.dismiss();
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
                else //if the post is already sold, change its current status
                    {
                    builder.setMessage("Are you sure that you want to mark the Post as unsold?");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {

                            HashMap<String, Object> newData = new HashMap<>();
                            newData.put("sold", false); //change the database variable
                            db.collection("posts").document(myPosts.get(position).getId()).set(newData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void aVoid)
                                {
                                    holder.soldPhoto.setVisibility(View.INVISIBLE); //set sold photo invisible
                                    holder.tickSold.setImageResource(R.drawable.tick);
                                    myPosts.get(position).setSold(false); //set the post as not sold
                                    Toast.makeText(context, "You have marked your Post as unsold!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
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
        return myPosts.size();
    }
}