package com.example.bookurbook.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookurbook.R;

/**
 * This class is created in order to create a report dialog in the desired screen.
 */
public class ReportDialog extends AppCompatDialogFragment
{
    //instance variables
    private EditText descriptionEditText;
    private ReportPostDialogListener listener;//listener is created in order to send the data acquired in the dialog to the current activity (e.g. PostActivity, Chats activity)
    private Spinner spinner;

    /**
     * This method creates the dialog and does takes certain actions according to which button is pressed
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.post_report_dialog, null);
        builder.setView(view);
        builder.setTitle("Report the Post");
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
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
                String category = spinner.getSelectedItem().toString();
                listener.applyTexts(description, category);//applyTexts method is overrided in the activities that use report and when the
                //dialog gets created in the activity, the activity is then able
                //to acquire the data that is entered in the dialog.

                Toast.makeText(builder.getContext(), "Report sent!", Toast.LENGTH_LONG).show();
            }
        });
        descriptionEditText = (EditText) view.findViewById(R.id.postReportDescription);
        spinner = (Spinner) view.findViewById(R.id.reportCategorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(builder.getContext(), R.array.postReportCategories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return builder.create();
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try
        {
            listener = (ReportPostDialogListener) context;
        } catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString() + "must implement ReportDialogListener");
        }
    }

}
