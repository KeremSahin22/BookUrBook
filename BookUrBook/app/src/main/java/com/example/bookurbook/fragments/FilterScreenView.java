package com.example.bookurbook.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.bookurbook.R;

/**
 * this class represents the pop-up window which is
 * displyaed when the user wants to filter the post list
 */
public class FilterScreenView extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {

    // properties
    String filteredUni = "";
    String filteredCourse = "";
    private FilterScreenListener filterScreenListener;
    TextView filterByUni;
    Spinner uniSpinner;
    TextView filterByCourse;
    Spinner courseSpinner;
    EditText lowPrice;
    EditText highPrice;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // this is the pop-up window that will open
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_filter, null);

        // a spinner with several university options in it where the user can choose one
        filterByUni = view.findViewById(R.id.uni_filter);
        uniSpinner = view.findViewById(R.id.univeristy_spinner);
        ArrayAdapter<CharSequence> uniAdapter = ArrayAdapter.createFromResource(builder.getContext(), R.array.Universities, android.R.layout.simple_spinner_item);
        uniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uniSpinner.setAdapter(uniAdapter);
        uniSpinner.setOnItemSelectedListener(this);

        // a spinner with several course options in it where the user can choose one
        filterByCourse = view.findViewById(R.id.course_filter);
        courseSpinner = view.findViewById(R.id.course_spinner);
        ArrayAdapter<CharSequence> courseAdapter = ArrayAdapter.createFromResource(builder.getContext(), R.array.Courses, android.R.layout.simple_spinner_item);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);
        courseSpinner.setOnItemSelectedListener(this);

        // edit text fields for user to set a price range
        lowPrice = view.findViewById(R.id.low_price);
        highPrice = view.findViewById(R.id.high_price);

        // for applying the filter options
        builder.setView(view)
                .setTitle("Filter")

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })

                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    int low;
                    int high;
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (lowPrice.getText().toString().equals(""))
                            low = -1;
                        else
                            low = Integer.parseInt(lowPrice.getText().toString());
                        if (highPrice.getText().toString().equals(""))
                            high = -1;
                        else
                            high = Integer.parseInt(highPrice.getText().toString());

                        filterScreenListener.filterThePosts(filteredUni, filteredCourse, low , high);
                    }
                });
        return builder.create();
    }

    /**
     * method for getting the user preference from the spinners
     * @param adapterView which displays options in spinner
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == uniSpinner.getId()) {
            filteredUni = uniSpinner.getItemAtPosition(i).toString();

        }
        else if (adapterView.getId() == courseSpinner.getId()) {
            filteredCourse = courseSpinner.getItemAtPosition(i).toString();
        }
    }

    /**
     * nothing will happen if no option is selected
     * @param adapterView which displays options in spinner
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            filterScreenListener = (FilterScreenListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement FilterScreenListener");
        }
    }

    /**
     * an interface which is to be implemented by Post List Activity
     */
    public interface FilterScreenListener
    {
        void filterThePosts(String uni, String course, int lowPrice, int highPrice);
    }
}
