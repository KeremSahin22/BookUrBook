package com.example.bookurbook.models;

import android.net.Uri;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * A model admin class that has certain features in order to keep order in app
 */
public class Admin extends User implements Serializable
{
    //instance variables
    public Admin(String username, String email, String avatar)
    {
        super(username, email, avatar);
    }

    /**
     * This method sets the ban status of other users
     *
     * @param other
     */
    public void ban(User other)
    {
        other.setBanned(true);
    }

    /**
     * Is created to delete a post from other users
     *
     * @param post     item
     * @param usedList postList that the item is going to be removed
     */
    public void deletePost(Post post, PostList usedList)
    {
        usedList.deletePost(post);
    }

}

