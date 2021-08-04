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
import com.example.bookurbook.activities.PostActivity;
import com.example.bookurbook.models.Post;

import com.example.bookurbook.models.PostList;
import com.example.bookurbook.models.User;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import java.util.List;

/**
 * a class for building a recycler view for Post List screen
 */
public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder> implements Filterable {

    // variables
    private ArrayList<Post> postListHolder;
    private ArrayList<Post> postListHolderFull;
    private User currentUser;
    PostList list;
    Context context;

    // constructor
    public PostListAdapter(Context c, PostList list, User user)
    {
        currentUser = user;
        this.list = list;
        postListHolder = list.getPostArray();
        postListHolderFull = new ArrayList<>(list.getPostArray());
        context = c;
    }

    // used to represent a single item
    @NonNull
    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.post_row, viewGroup, false);
        return new PostListViewHolder(view);
    }

    // to display the data at the specified position
    @Override
    public void onBindViewHolder(@NonNull PostListViewHolder postListViewHolder, int i)
    {
        postListViewHolder.crown.setImageResource(R.drawable.crown_new);
        postListViewHolder.crown.setVisibility(View.VISIBLE);
        Post examplePost = postListHolder.get(i);
        postListViewHolder.title.setText(examplePost.getTitle());
        postListViewHolder.seller.setText(examplePost.getOwner().getUsername());
        postListViewHolder.price.setText(Integer.toString((int) examplePost.getPrice()) + "₺");
        if (examplePost.getPrice() != 0)
            postListViewHolder.crown.setVisibility(View.INVISIBLE);
        Picasso.get().load(examplePost.getPicture()).into(postListViewHolder.picture);
        postListViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pass = new Intent(context, PostActivity.class);
                pass.putExtra("currentUser", currentUser);
                pass.putExtra("postlist", list);
                pass.putExtra("post", examplePost);
                pass.putExtra("previousActivity", 1);
                context.startActivity(pass);
            }
        });
    }

    /**
     * method for determining the full size of the list
     * @return the size of the post list araylist
     */
    @Override
    public int getItemCount() {
        return postListHolder.size();
    }

    /**
     * for filtering by search bar purposes
     * @return
     */
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    /**
     * method for sorting the post list according to user peferences
     * @param v view parameter coming from the PostListActivity
     */
    public void sort(View v)
    {
        PostList filteredList = list;

        if (v.getId() != R.id.reset_button) {

            // sort by letter A to Z
            if (v.getId() == R.id.AtoZ_button)
                filteredList.sortByLetter(true);

                // sort by letter Z to A
            else if (v.getId() == R.id.ZtoA_button)
                filteredList.sortByLetter(false);

                // sort by price low to high
            else if (v.getId() == R.id.LtoH_price_button)
                filteredList.sortByPrice(true);

                // sort by price high to low
            else if (v.getId() == R.id.HtoL_price_button)
                filteredList.sortByPrice(false);

            // update post list
            postListHolder = new ArrayList<>(filteredList.getPostArray());
            notifyDataSetChanged();
        }
        else
        {
            // update post list by going back to default
            postListHolder = new ArrayList<>(postListHolderFull);
            notifyDataSetChanged();
        }

    }

    private Filter exampleFilter = new Filter() {
        /**
         * for filtering the list according to user input to the search bar
         * @param charSequence the string user enters to search bar
         * @return the updated list
         */
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Post> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0)
            {
                filteredList.addAll(postListHolderFull);
            }
            else
            {
                String filterInput = charSequence.toString().toLowerCase().trim();

                for (Post p : postListHolderFull)
                {
                    if (p.getTitle().toLowerCase().contains(filterInput))
                    {
                        filteredList.add(p);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        // update the list according to searching operations
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            postListHolder.clear();
            postListHolder.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


    /**
     * inner class to hold the properties as views
     */
    public class PostListViewHolder extends RecyclerView.ViewHolder {

        // variables
        TextView title;
        TextView seller;
        TextView price;
        ImageView picture;
        ImageView crown;
        LinearLayout layout;

        // constructor
        public PostListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.postText);
            picture = itemView.findViewById(R.id.postImageView);
            seller = itemView.findViewById(R.id.postSeller);
            price = itemView.findViewById(R.id.priceText);
            layout = (LinearLayout)  itemView.findViewById(R.id.row_post);
            crown = itemView.findViewById(R.id.sold_crown);
        }
    }

    /**
     * method for filtering the post list according to user preferences
     * @param uni : the university user chose
     * @param course : the course user chose
     * @param lowPrice : the lower bound of the price range user chose
     * @param highPrice : the upper bound of the price range user chose
     */
    public void filterResults(String uni, String course, int lowPrice, int highPrice)
    {
        PostList filteredList = list;

        if (!uni.equals("Other")) {
            filteredList = filteredList.filterByUniversity(uni);
        }
        if (!course.equals("Other")) {
            filteredList = filteredList.filterByCourse(course);
        }
        if (lowPrice != -1 || highPrice != -1) {
            filteredList = filteredList.filterByPrice(lowPrice, highPrice);
        }

        // update the list
        postListHolder = new ArrayList<>(filteredList.getPostArray());
        notifyDataSetChanged();
    }

}
