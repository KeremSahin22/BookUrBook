package com.example.bookurbook.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookurbook.R;

/**
 * This class is created in order to create a feedback dialog in the desired screen.
 */
public class FeedbackDialog extends AppCompatDialogFragment
{
    //instance variables
    private EditText descriptionEditText;
    private FeedbackDialogListener listener;//listener is created in order to send the data acquired in the dialog to the current activity (e.g. Settings)

    /**
     * This method creates the dialog and does takes certain actions according to which button is pressed
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_feedback, null);
        builder.setView(view);
        builder.setTitle("Send your feedback");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String description = descriptionEditText.getText().toString();
                listener.applyTexts(description);//applyTexts method is overrided in the settings activity and when the
                //dialog gets created in the settings activity, the activity is then able
                //to acquire the data that is entered in the dialog.


                Toast.makeText(builder.getContext(), "Feedback is sent!", Toast.LENGTH_LONG).show();
            }
        });
        descriptionEditText = (EditText) view.findViewById(R.id.feedbackDescription);
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            listener = (FeedbackDialogListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement FeedbackDialogListener");
        }
    }

}
