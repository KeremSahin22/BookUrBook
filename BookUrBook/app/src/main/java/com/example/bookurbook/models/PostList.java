package com.example.bookurbook.models;

import android.widget.ImageView;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class PostList implements Filterable, Sortable, Serializable
{
    //instance variables
    private ArrayList<Post> postArray;

    //constructor
    public PostList()
    {
        postArray = new ArrayList<>();
    }

    //get methods
    public ArrayList<Post> getPostArray()
    {
        return postArray;
    }

    //set methods
    public void setPostArray(ArrayList<Post> postList)
    {
        postArray = postList;
    }

    //other methods

    /**
     * Adds post to the postArray
     *
     * @param post post to be added
     */
    public void addPost(Post post)
    {
        postArray.add(post);
    }

    /**
     * Deletes the post from the postArray
     *
     * @param post post to be deleted
     */
    public void deletePost(Post post)
    {
        this.getPostArray().remove(post);
    }

    /**
     * This method creates a post, adds it to the Postlist and also adds it to the userSpecPostList of the owner
     *
     * @param description .
     * @param title       .
     * @param university  .
     * @param course      .
     * @param price       .
     * @param picture     .
     * @param owner       .
     * @param id          .
     */
    public void createPost(String description, String title, String university, String course, int price, String picture, User owner, String id)
    {
        if (!owner.isBanned())
        {
            Post post = new Post(description, title, university, course, price, picture, owner, id);
            this.addPost(post);
        }
    }

    /**
     * When a post is sold, it deletes it from the post list and marks it as sold.
     *
     * @param post post that is sold
     */
    public void postSold(Post post)
    {
        post.setSold(true);
    }

    /**
     * This method sorts the posts in a collection according to their prices
     *
     * @param isLowToHigh if true sorts it from low to high, if false sorts it from high to low
     */
    @Override
    public void sortByPrice(boolean isLowToHigh)
    {
        if (isLowToHigh)
        {
            Collections.sort(postArray, new PostPriceComparator());
        } else
        {
            Collections.sort(postArray, new PostPriceComparator());
            Collections.reverse(postArray);
        }
    }

    /**
     * This method sorts the posts in a collection according to their prices
     *
     * @param isAToZ if true sorts it according alphabetic order, if false sorts it in reverse alphabetic order
     */
    @Override
    public void sortByLetter(boolean isAToZ)
    {
        if (isAToZ)
        {
            Collections.sort(postArray, new PostTitleComparator());
        } else
        {
            Collections.sort(postArray, new PostTitleComparator());
            Collections.reverse(postArray);
        }
    }

    /**
     * Filters the postlist that contains posts that is from a certain university and returns the newly created list.
     *
     * @param University desired university
     * @return filtered postlist
     */
    @Override
    public PostList filterByUniversity(String University)
    {
        PostList filtered = new PostList();
        for (Post post : postArray)
        {
            if (post.getUniversity().equalsIgnoreCase(University))
                filtered.addPost(post);
        }
        return filtered;
    }

    /**
     * Filters the postlist that contains posts that is from a course and returns the newly created list.
     *
     * @param course desired course
     * @return filtered postlist
     */
    @Override
    public PostList filterByCourse(String course)
    {
        PostList filtered = new PostList();
        for (Post post : postArray)
        {
            if (post.getCourse().equalsIgnoreCase(course))
                filtered.addPost(post);
        }
        return filtered;
    }

    /**
     * Filters the postlist that contains posts that is in the price range and returns the newly created list.
     *
     * @param min min boundary of price
     * @param max max boundary of price
     * @return filtered postlist
     */
    @Override
    public PostList filterByPrice(int min, int max)
    {
        PostList filtered = new PostList();
        for (Post post : postArray)
        {
            if (post.getPrice() <= max && min <= post.getPrice())
                filtered.addPost(post);
        }
        return filtered;
    }
}
