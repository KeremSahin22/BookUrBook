package com.example.bookurbook.models;

/**
 * This interface is created in order to indicate classes which are sortable according to the given parameters.
 */
public interface Sortable
{

    void sortByPrice(boolean isLowToHigh);

    void sortByLetter(boolean isAToZ);

}
