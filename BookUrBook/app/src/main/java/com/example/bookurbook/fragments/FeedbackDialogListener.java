package com.example.bookurbook.fragments;

/**
 * This interface is created in order to get the data that is acquired in the feedback dialog.
 * Mainly created in order to be implemented in the activities that use the feedback dialog.(Settings Activity)
 */
public interface FeedbackDialogListener
{
    void applyTexts(String description);
}
