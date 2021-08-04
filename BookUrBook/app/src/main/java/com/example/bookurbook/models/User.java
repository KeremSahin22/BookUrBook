package com.example.bookurbook.models;

import android.net.Uri;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.*;

/**
 * This class is an abstract class that contains the properties that every user(RegularUser,Admin) has.
 */
public abstract class User implements Reportable, Serializable
{
    //instance variables
    private String username;
    private String email;
    private boolean banned;
    private ArrayList<Report> reports;
    private int reportNum;
    private String avatar;
    private ArrayList<User> blockedUsers;
    private ArrayList<Post> wishList;

    //constructor
    public User(String username, String email, String avatar)
    {
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        reports = new ArrayList<Report>();
        wishList = new ArrayList<Post>();
        blockedUsers = new ArrayList<User>();
        reportNum = 0;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;

    }

    public ArrayList<Post> getWishList()
    {
        return wishList;
    }

    public int getReportNum()
    {
        return reportNum;
    }

    public void setReportNum(int reportNum)
    {
        this.reportNum = reportNum;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean isBanned()
    {
        return banned;
    }

    public void setBanned(boolean banned)
    {
        this.banned = banned;
    }

    public ArrayList<Report> getReports()
    {
        return reports;
    }

    public void setReports(ArrayList<Report> reports)
    {
        this.reports = reports;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public void setWishList(ArrayList<Post> wishList)
    {
        this.wishList = wishList;
    }

    public ArrayList<User> getBlockedUsers()
    {
        return blockedUsers;
    }

    public void setBlockedUsers(ArrayList<User> blockedUsers)
    {
        this.blockedUsers = blockedUsers;
    }

    /**
     * This method adds the wanted user to the blocklist
     *
     * @param user
     */
    public void blockUser(User user)
    {
        blockedUsers.add(user);
    }

    /**
     * This method creates a report and adds this report to the user
     *
     * @param description the description provided by the user
     * @param category    the category chosen by the user
     */
    @Override
    public void report(String description, String category)
    {
        Report report = new Report(description, category, this);
        reports.add(report);
    }

}



