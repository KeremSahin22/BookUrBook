package com.example.bookurbook.models;

/**
 * This interface is created in order to create a filterable model class
 */
public interface Filterable
{
    /**
     * Filtered according to the given university
     *
     * @param university chosen university
     * @return postlist only with the given university
     */
    PostList filterByUniversity(String university);

    /**
     * Filtered according to the given course
     *
     * @param course chosen course
     * @return postlist only with the given course
     */
    PostList filterByCourse(String course);

    /**
     * Filtered according to the given university
     *
     * @param min min boundary
     * @param max max boundary
     * @return postlist only with the given price interval
     */
    PostList filterByPrice(int min, int max);

}
