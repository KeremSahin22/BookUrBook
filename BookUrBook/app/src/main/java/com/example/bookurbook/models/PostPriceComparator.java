package com.example.bookurbook.models;

import java.util.Comparator;

/**
 * This comparator is created in order to compare the price of the post
 */
public class PostPriceComparator implements Comparator<Post>
{
    @Override
    public int compare(Post o1, Post o2)
    {
        if (o1.getPrice() > o2.getPrice())
            return 1;
        else if (o1.getPrice() == o2.getPrice())
            return 0;
        else
            return -1;
    }
}

