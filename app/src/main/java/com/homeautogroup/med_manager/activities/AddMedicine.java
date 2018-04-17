package com.homeautogroup.med_manager.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.fragments.DatePickerFragment;
import com.homeautogroup.med_manager.models.Medicine;

public class AddMedicine extends AppCompatActivity implements DatePickerFragment.OnDateInteractionListener{

    private EditText mInputMedName,mInputMedDescription;
    private String startDate,endDate,medicine_name,medicine_description;
    private TextView mStartDateTextView,mEndDateTextView;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    String userUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mInputMedName = findViewById(R.id.input_medicine_name);
        mInputMedDescription = findViewById(R.id.input_medicine_description);
        mStartDateTextView = findViewById(R.id.start_date_textview);
        mEndDateTextView = findViewById(R.id.end_date_textview);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        if (mCurrentUser == null){
            goToSignInActivity();
        }else {
           userUID = mCurrentUser.getUid();
        }

    }

    private void showDatePicker(String startKey) {
        DialogFragment newFragment = new DatePickerFragment();
        Bundle bundle = new  Bundle();
        bundle.putString(DatePickerFragment.START_KEY,startKey);
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void saveStartDate(View view){
        showDatePicker(DatePickerFragment.START_KEY_START_DATE);
    }

    public void saveEndDate(View view){
        showDatePicker(DatePickerFragment.START_KEY_END_DATE);
    }

    public void saveMedicine(View view){
        medicine_name = mInputMedName.getText().toString().trim();
        medicine_description = mInputMedDescription.getText().toString().trim();

        if (startDate==null || endDate ==null){
            Toast.makeText(this, "Please select start and end date", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(medicine_name) || TextUtils.isEmpty(medicine_description)){
            Toast.makeText(this, "Please add medicine name and medicine description", Toast.LENGTH_SHORT).show();
        }else {
            saveMedicineToDb();
        }

    }

    private void saveMedicineToDb() {
        Toast.makeText(this, "Saving medicine to db", Toast.LENGTH_SHORT).show();
        DatabaseReference myMedsRef = mDatabase.getReference().child("Users")
                .child(userUID).child("My Medicines");

        DatabaseReference newMedicineRef = myMedsRef.push();

        Medicine medicine = new Medicine(medicine_name,medicine_description,
                startDate,endDate,getMonth(),getYear());
        newMedicineRef.setValue(medicine).addOnSuccessListener(this,new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddMedicine.this, "Medicine Added Successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(this,new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddMedicine.this, "Medicine could not be added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStartDateSelected(String date) {
        startDate = date;
        mStartDateTextView.setText("Start Date Is: "+date);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onEndDateSelected(String date) {
        endDate = date;
        mEndDateTextView.setText("End Date Is: "+date);
    }

    private void goToSignInActivity() {
        Intent intent = new Intent(AddMedicine.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private int getYear(){
        String yearArray[] = startDate.split("-");
       return Integer.valueOf(yearArray[0]);
    }

    private int getMonth(){
        String monthArray[] = startDate.split("-");
        return Integer.valueOf(monthArray[1]);
    }


}
