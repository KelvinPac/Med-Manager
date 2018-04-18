package com.homeautogroup.med_manager.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.activities.AddEditAlarmActivity;
import com.homeautogroup.med_manager.data.DatabaseHelper;
import com.homeautogroup.med_manager.models.Alarm;
import com.homeautogroup.med_manager.models.Medicine;
import com.homeautogroup.med_manager.service.AlarmReceiver;
import com.homeautogroup.med_manager.service.LoadAlarmsService;
import com.homeautogroup.med_manager.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public final class AddEditAlarmFragment extends Fragment {

    private TimePicker mTimePicker;
    List<String> spinnerArray = new ArrayList<>();
    // private EditText mLabel;
    private CheckBox mMon, mTues, mWed, mThurs, mFri, mSat, mSun;
    private Context mContext;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mCurrentUser;
    private Spinner myMedicinesSpinner;

    public static AddEditAlarmFragment newInstance(Alarm alarm) {

        Bundle args = new Bundle();
        args.putParcelable(AddEditAlarmActivity.ALARM_EXTRA, alarm);

        AddEditAlarmFragment fragment = new AddEditAlarmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_add_edit_alarm, container, false);
        mContext = v.getContext();
        setHasOptionsMenu(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        final Alarm alarm = getAlarm();

        mTimePicker = v.findViewById(R.id.edit_alarm_time_picker);
        myMedicinesSpinner = v.findViewById(R.id.my_medicines_spinner);
        ViewUtils.setTimePickerTime(mTimePicker, alarm.getTime());

        // mLabel = (EditText) v.findViewById(R.id.edit_alarm_label);
        //mLabel.setText(alarm.getLabel());

        mMon = v.findViewById(R.id.edit_alarm_mon);
        mTues = v.findViewById(R.id.edit_alarm_tues);
        mWed = v.findViewById(R.id.edit_alarm_wed);
        mThurs = v.findViewById(R.id.edit_alarm_thurs);
        mFri = v.findViewById(R.id.edit_alarm_fri);
        mSat = v.findViewById(R.id.edit_alarm_sat);
        mSun = v.findViewById(R.id.edit_alarm_sun);

        setDayCheckboxes(alarm);

        setDefaultDayOfWeek();
        fetchMyMedicinesFromDb();
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_alarm_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                save();
                break;
            case R.id.action_delete:
                delete();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void fetchMyMedicinesFromDb() {
        //add default value to spinner array. this helps us in knowing if a medicine was selected
        spinnerArray.add("Select Medicine");
        DatabaseReference myMedsRef = mDatabase.getReference().child("Users")
                .child(mCurrentUser.getUid()).child("My Medicines");
        Query myMeds = myMedsRef.orderByKey();
        myMeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Medicine medicine = postSnapshot.getValue(Medicine.class).withUniqueId(postSnapshot.getKey());
                    spinnerArray.add(medicine.getMedicineName());
                }

                populateSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDefaultDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                mSun.setChecked(true);
                break;
            case Calendar.MONDAY:
                mMon.setChecked(true);
                break;
            case Calendar.TUESDAY:
                mTues.setChecked(true);
                break;
            case Calendar.WEDNESDAY:
                mWed.setChecked(true);
                break;
            case Calendar.THURSDAY:
                mThurs.setChecked(true);
                break;
            case Calendar.FRIDAY:
                mFri.setChecked(true);
                break;
            case Calendar.SATURDAY:
                mSat.setChecked(true);
                break;
            default:
        }

    }

    private void populateSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                mContext, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myMedicinesSpinner.setAdapter(adapter);

    }

    private Alarm getAlarm() {
        return getArguments().getParcelable(AddEditAlarmActivity.ALARM_EXTRA);
    }

    private void setDayCheckboxes(Alarm alarm) {
        mMon.setChecked(alarm.getDay(Alarm.MON));
        mTues.setChecked(alarm.getDay(Alarm.TUES));
        mWed.setChecked(alarm.getDay(Alarm.WED));
        mThurs.setChecked(alarm.getDay(Alarm.THURS));
        mFri.setChecked(alarm.getDay(Alarm.FRI));
        mSat.setChecked(alarm.getDay(Alarm.SAT));
        mSun.setChecked(alarm.getDay(Alarm.SUN));
    }

    private void save() {

        //String label = mLabel.getText().toString();
        String selected = myMedicinesSpinner.getSelectedItem().toString();
        if (selected.equals("Select Medicine") || TextUtils.isEmpty(selected)) {
            Toast.makeText(mContext, "Medicine is not selected. If you have no medicines please add some first", Toast.LENGTH_SHORT).show();
            return;
        }

        final Alarm alarm = getAlarm();

        final Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, ViewUtils.getTimePickerMinute(mTimePicker));
        time.set(Calendar.HOUR_OF_DAY, ViewUtils.getTimePickerHour(mTimePicker));
        alarm.setTime(time.getTimeInMillis());

        alarm.setLabel(selected);

        alarm.setDay(Alarm.MON, mMon.isChecked());
        alarm.setDay(Alarm.TUES, mTues.isChecked());
        alarm.setDay(Alarm.WED, mWed.isChecked());
        alarm.setDay(Alarm.THURS, mThurs.isChecked());
        alarm.setDay(Alarm.FRI, mFri.isChecked());
        alarm.setDay(Alarm.SAT, mSat.isChecked());
        alarm.setDay(Alarm.SUN, mSun.isChecked());

        final int rowsUpdated = DatabaseHelper.getInstance(getContext()).updateAlarm(alarm);

        final int messageId = (rowsUpdated == 1) ? R.string.update_complete : R.string.update_failed;

        Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();

        AlarmReceiver.setReminderAlarm(getContext(), alarm);

        getActivity().finish();

    }

    private void delete() {

        final Alarm alarm = getAlarm();

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(getContext(), R.style.DeleteAlarmDialogTheme);
        builder.setTitle(R.string.delete_dialog_title);
        builder.setMessage(R.string.delete_dialog_content);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finalizeDeleteAlarm(alarm);
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();

    }

    @Override
    public void onPause() {
        super.onPause();
        final Alarm alarm = getAlarm();
        if (TextUtils.isEmpty(alarm.getLabel())) {
            //delete this alarm
            finalizeDeleteAlarm(alarm);
        }
    }


    private void finalizeDeleteAlarm(Alarm alarm) {
        //Cancel any pending notifications for this alarm
        AlarmReceiver.cancelReminderAlarm(getContext(), alarm);

        final int rowsDeleted = DatabaseHelper.getInstance(getContext()).deleteAlarm(alarm);
        int messageId;
        if (rowsDeleted == 1) {
            messageId = R.string.delete_complete;
            Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
            LoadAlarmsService.launchLoadAlarmsService(getContext());
            getActivity().finish();
        } else {
            messageId = R.string.delete_failed;
            Toast.makeText(getContext(), messageId, Toast.LENGTH_SHORT).show();
        }
    }
}
