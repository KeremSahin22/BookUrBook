package com.example.bookurbook.models;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * This class is created for the customers.
 */
public class RegularUser extends User implements Serializable
{
    //constructor
    public RegularUser(String username, String email, String avatar)
    {
        super(username, email, avatar);
    }
}


