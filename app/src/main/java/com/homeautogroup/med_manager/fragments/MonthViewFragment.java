package com.homeautogroup.med_manager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.homeautogroup.med_manager.adapters.RecyclerViewAdapter;
import com.homeautogroup.med_manager.models.Medicine;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MonthViewFragment extends Fragment {


    private static final String TAG = MonthViewFragment.class.getSimpleName();
    private Context mContext;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mCurrentUser;
    private ArrayList<Medicine> modelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter mAdapter;

    public MonthViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_month_view, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view);
        mContext = view.getContext();
        Button mBtnSelectMonth = view.findViewById(R.id.btn_select_month_year);
        initRecyclerView();
        mBtnSelectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMonthYearPicker();
            }
        });
        return view;
    }

    private void openMonthYearPicker() {

      //  final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(mContext, new MonthPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int selectedMonth, int selectedYear) {
                // Log.d(TAG, "selectedMonth : " + selectedMonth + " selectedYear : " + selectedYear);
                //Toast.makeText(mContext, "Date set with month " + selectedMonth + " year " + selectedYear, Toast.LENGTH_SHORT).show();
                queryMedicationDates(selectedMonth,selectedYear);
            }
        }, 2018, 6);

        builder.setActivatedMonth(5)
                .setMinYear(2018)
                .setActivatedYear(2018)
                .setActivatedMonth(3)
                .setMaxYear(2025)
                //.setMinMonth(1)
                .setTitle("Select Medication Month!")
                //.setMonthRange(1, 11)
                // .setMaxMonth(Calendar.OCTOBER)
                // .setYearRange(1890, 1890)
                // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                .showMonthOnly()
                // .showYearOnly()
                .build()
                .show();
    }

    private void queryMedicationDates(int selectedMonth, int selectedYear) {
        modelList.clear();
        mAdapter.notifyDataSetChanged();
        DatabaseReference myMedsRef = mDatabase.getReference().child("Users")
                .child(mCurrentUser.getUid()).child("My Medicines");

        Query myMeds = myMedsRef.orderByChild("intakeMonth").equalTo(selectedMonth + 1);
        myMeds.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // Toast.makeText(mContext, postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    Medicine medicine = postSnapshot.getValue(Medicine.class).withUniqueId(postSnapshot.getKey());
                    modelList.add(medicine);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initRecyclerView() {
        mAdapter = new RecyclerViewAdapter(mContext, modelList);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        /*mAdapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Medicine model) {
                Toast.makeText(mContext, "Hey " + model.getMedicineName(), Toast.LENGTH_SHORT).show();

            }
        });*/
    }
}

