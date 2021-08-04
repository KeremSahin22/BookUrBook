package com.example.bookurbook.models;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is the messages for chat system. It contains several information like date, content or sent by whom.
 */
public class Message implements Serializable, Comparable<Message>
{
    //variables
    private String messageDate;
    private String messageTime;
    private String sentBy;
    private String content;
    private Date date;

    //constructor
    public Message(String sentBy, String contentString, String messageDate, String messageTime)
    {
        this.messageDate = messageDate;
        this.messageTime = messageTime;
        this.content = contentString;
        this.sentBy = sentBy;
    }

    //methods

    /**
     * To access String version of date
     *
     * @return String messageDate
     */
    public String getMessageDate()
    {
        return messageDate;
    }

    /**
     * To access String version of time
     *
     * @return String messageTime
     */
    public String getMessageTime()
    {
        return messageTime;
    }

    /**
     * To access content of message
     *
     * @return String content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * To access the name of user who sent this message
     *
     * @return String sentBy
     */
    public String getSentBy()
    {
        return sentBy;
    }

    /**
     * To change the String version of date
     *
     * @param messageDate String date
     */
    public void setMessageDate(String messageDate)
    {
        this.messageDate = messageDate;
    }

    /**
     * To change the String version of time
     *
     * @param messageTime String time
     */
    public void setMessageTime(String messageTime)
    {
        this.messageTime = messageTime;
    }

    /**
     * To change the message content
     *
     * @param content String message content
     */
    public void setContent(String content)
    {
        this.content = content;
    }

    /**
     * To change the sender of the message
     *
     * @param sentBy String username of sender
     */
    public void setSentBy(String sentBy)
    {
        this.sentBy = sentBy;
    }

    /**
     * This is for database usage and comparision. To get Date version of date and time.
     *
     * @return Date date and time together
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * This is for database usage and comparision. To set Date version of time.
     *
     * @param date Date date and time together
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * This methods is written for Collection.sort() method. It compares messages depending on their Date date.
     *
     * @param o other message
     * @return integer result
     */
    @Override
    public int compareTo(Message o)
    {
        return this.getDate().compareTo(o.getDate());
    }
}
