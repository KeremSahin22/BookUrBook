package com.example.bookurbook.models;

import java.io.*;
import java.util.*;

import android.widget.ImageView;

/**
 * This class stores the necessary information about a post.
 */
public class Post implements Reportable, Serializable
{
    //instance variables
    private String description;
    private String title;
    private String university;
    private String course;
    private int price;
    private String picture;
    private String id;
    private User owner;
    private boolean sold;
    private ArrayList<Report> reports;
    private int reportNum;

    //constructors
    public Post(String description, String title, String university, String course, int price, String picture, User owner, String id)
    {
        this.description = description;
        this.title = title;
        this.university = university;
        this.course = course;
        this.price = price;
        this.picture = picture;
        this.id = id;
        this.owner = owner;
        sold = false;
        reports = new ArrayList<>();
        reportNum = 0;
    }

    //getters
    public String getDescription()
    {
        return description;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUniversity()
    {
        return university;
    }

    public String getCourse()
    {
        return course;
    }

    public int getPrice()
    {
        return price;
    }

    public String getPicture()
    {
        return picture;
    }

    public User getOwner()
    {
        return owner;
    }

    public int getReportNum()
    {
        return reportNum;
    }

    public void setReportNum(int reportNum)
    {
        this.reportNum = reportNum;
    }

    public boolean isSold()
    {
        return sold;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public ArrayList<Report> getReports()
    {
        return reports;
    }

    //setters
    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setUniversity(String university)
    {
        this.university = university;
    }

    public void setCourse(String course)
    {
        this.course = course;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public void setPicture(String picture)
    {
        this.picture = picture;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }

    public void setSold(boolean sold)
    {
        this.sold = sold;
    }

    public void setReports(ArrayList<Report> reports)
    {
        this.reports = reports;
    }

    //other methods

    /**
     * This method is put in order to edit wanted post variables
     *
     * @param description description
     * @param title       title
     * @param university  Univeristy
     * @param course      course
     * @param price       price
     * @param picture     picture
     */
    public void editPost(String description, String title, String university, String course, int price, String picture)
    {
        this.description = description;
        this.title = title;
        this.university = university;
        this.course = course;
        this.price = price;
        this.picture = picture;
    }

    /**
     * This method creates a report and adds this report to the post
     *
     * @param description the description provided by the user
     * @param category    the category chosen by the user
     */
    @Override
    public void report(String description, String category)
    {
        Report report = new Report(description, category, owner);
        reports.add(report);
        owner.getReports().add(report);
    }
}
