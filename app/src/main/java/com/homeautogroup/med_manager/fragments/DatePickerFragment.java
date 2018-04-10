package com.homeautogroup.med_manager.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Jilinde Created by kelvin on 2/19/18.
 * com.homeautogroup.jilinde.fragments
 * flyboypac@gmail.com
 * +254705419309
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDateInteractionListener mListener;

    public static final String START_KEY_START_DATE = "get_start";
    public static final String START_KEY_END_DATE = "get_end";
    public static final String START_KEY = "start_key";
    private String startKey;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null){
            startKey = bundle.getString(START_KEY,null);
            if (startKey==null){
               getActivity().finish();
            }
        }
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String pattern = "yyyy-MM-dd";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(calendar.getTime());
        //Toast.makeText(getActivity(), date, Toast.LENGTH_SHORT).show();

        if (mListener != null) {
            switch (startKey){
                case START_KEY_START_DATE:
                    mListener.onStartDateSelected(date);
                    break;
                case START_KEY_END_DATE:
                    mListener.onEndDateSelected(date);
                    break;
                    default:
            }
            //mListener.onDatePickerDateSet(date);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDateInteractionListener) {
            mListener = (OnDateInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDateInteractionListener {
        // TODO: Update argument type and name
        void onStartDateSelected(String date);
        void onEndDateSelected(String date);
    }


}
