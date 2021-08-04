package com.example.bookurbook.fragments;

/**
 * This interface is created in order to get the data that is acquired in the report dialog.
 * Mainly created in order to be implemented in the activities that use the report dialog.(Post Activity, Chat Activity)
 */
public interface ReportPostDialogListener
{
    void applyTexts(String description, String category);
}
