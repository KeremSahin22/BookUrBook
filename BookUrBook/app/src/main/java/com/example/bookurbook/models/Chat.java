package com.example.bookurbook.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * This is a single chat object for the chat system. It contains some information about chat like users, messages
 * or last message in database
 */
public class Chat implements Serializable, Comparable<Chat>
{
    //variables
    private User user1;
    private User user2;
    private String lastMessageContentInDB;
    private Date date;
    private String chatID;
    private boolean readByUser1;
    private boolean readByUser2;

    //constructor
    public Chat(User user1, User user2, String chatID)
    {
        this.user1 = user1;
        this.user2 = user2;
        this.chatID = chatID;
        readByUser1 = true;
        readByUser2 = true;
    }

    //methods

    /**
     * To check if a user is a part of this chat or not
     *
     * @param user wanted to be check
     * @return 1 for user1, 2 for user2, 0 for user is not a part of this chat.
     */
    public int isConsist(User user)
    {
        if (user1.getUsername().equals(user.getUsername()))
        {
            return 1;
        } else if (user2.getUsername().equals(user.getUsername()))
        {
            return 2;
        } else
        {
            return 0;
        }
    }

    /**
     * To get user1
     *
     * @return User user1
     */
    public User getUser1()
    {
        return user1;
    }

    /**
     * To get user2
     *
     * @return User user2
     */
    public User getUser2()
    {
        return user2;
    }


    /**
     * This is for database usage. It is used to set the last message of content retrieved from database.
     *
     * @param lastMessageContentInDB content of last message
     */
    public void setLastMessageContentInDB(String lastMessageContentInDB)
    {
        this.lastMessageContentInDB = lastMessageContentInDB;
    }

    /**
     * To get the date of last message in this chat. This info is retrieved from database.
     *
     * @return Date date of last message
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * This is for database usage. It is used to set the date of last message in this chat. This info is retrieved from database.
     *
     * @param date date of last message
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * To get the content of last message retrieved from database.
     *
     * @return content of last message
     */
    public String getLastMessageInDB()
    {
        return lastMessageContentInDB;
    }

    /**
     * This is for comparing the dates of chat. It works reverse for sorting Chat ArrayLists with Collection.sort()
     * from new to old.
     *
     * @param o other Chat
     * @return reverse version of normal compareTo method for dates.
     */
    @Override
    public int compareTo(Chat o)
    {
        return -(getDate().compareTo(o.getDate()));
    }

    /**
     * To get the chatID retrieved from database.
     *
     * @return String chatID
     */
    public String getChatID()
    {
        return chatID;
    }

    /**
     * To set chatID in database to Chat object.
     *
     * @param chatID String chatID
     */
    public void setChatID(String chatID)
    {
        this.chatID = chatID;
    }

    /**
     * To check if this chat is read by user1
     *
     * @return boolean
     */
    public boolean isReadByUser1()
    {
        return readByUser1;
    }

    /**
     * To check if this chat is read by user2
     *
     * @return boolean
     */
    public boolean isReadByUser2()
    {
        return readByUser2;
    }

    /**
     * To set if this chat is read by user1. Generally used with database.
     *
     * @return boolean
     */
    public void setReadByUser1(boolean readByUser1)
    {
        this.readByUser1 = readByUser1;
    }

    /**
     * To set if this chat is read by user2. Generally used with database.
     *
     * @return boolean
     */
    public void setReadByUser2(boolean readByUser2)
    {
        this.readByUser2 = readByUser2;
    }
}
