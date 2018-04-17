package com.homeautogroup.med_manager.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.activities.AddMedicine;
import com.homeautogroup.med_manager.activities.HomeActivityMain;
import com.homeautogroup.med_manager.activities.MyMedicines;
import com.homeautogroup.med_manager.activities.SetupActivity;
import com.homeautogroup.med_manager.models.User;
import com.homeautogroup.med_manager.utils.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {


    private Context mContext;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private ImageView circleImageView;
    private TextView userName,userEmail,userAge,userSex,userBMI,userWeight;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();

        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        //find views
        circleImageView = view.findViewById(R.id.circleImageView);
        userName = view.findViewById(R.id.text_view_user_name);
        userEmail = view.findViewById(R.id.text_view_user_email);
        userAge = view.findViewById(R.id.text_view_user_age);
        userSex = view.findViewById(R.id.text_view_user_sex);
        userBMI = view.findViewById(R.id.text_view_user_bmi);
        userWeight = view.findViewById(R.id.text_view_user_weight);
        Button btnAddMedicines = view.findViewById(R.id.btn_add_medicines);
        Button btnViewMyMedicines = view.findViewById(R.id.btn_view_medicines);
        Button btnEditMyAccount = view.findViewById(R.id.btn_edit_my_account);


        btnAddMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(mContext, AddMedicine.class));
            }
        });


        btnViewMyMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, MyMedicines.class));
            }
        });

        btnEditMyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SetupActivity.class);
                intent.setAction(SetupActivity.START_ACTION_UPDATE);
                startActivity(intent);
            }
        });
        loadUserProfile();
        return view;
    }


    private void loadUserProfile(){
        DatabaseReference mUserRef = mDatabase.getReference().child("Users").child(mCurrentUser.getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    userName.setText("Name: " + user.getUserName());
                    userEmail.setText("Email: " + mCurrentUser.getEmail());
                    userAge.setText("Age: "+ String.valueOf(user.getUserAge())+" Years");
                    userSex.setText("Sex: "+user.getUserSex());
                    userBMI.setText("BMI: To CALC");
                    userWeight.setText("Weight: "+ String.valueOf(user.getUserWeight())+" KGs");



                    try {
                        GlideApp.with(mContext)
                                .load(user.getProfileImage())
                                .centerCrop()
                                .placeholder(R.drawable.default_avatar)
                                .skipMemoryCache(false)
                                .into(circleImageView);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
