package com.homeautogroup.med_manager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.fragments.AddMedFragment;
import com.homeautogroup.med_manager.fragments.MyMedsFragment;
import com.homeautogroup.med_manager.fragments.MyProfileFragment;
import com.homeautogroup.med_manager.fragments.RemindersFragment;
import com.homeautogroup.med_manager.models.User;
import com.homeautogroup.med_manager.utils.GlideApp;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivityMain extends AppCompatActivity {

    private TextView mTextMessage;
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private Toolbar toolbar;
    //private View mNavigationView;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);

        //toolbar = getSupportActionBar();
        toolbar = (Toolbar)findViewById(R.id.toolbar_main);
       // toolbar.setLogo(imageDrawable);
       // toolbar.setTitle(title);
        toolbar.setTitle("Med Manager");
        setSupportActionBar(toolbar);

        //LayoutInflater mInflater = LayoutInflater.from(this);
        //View mCustomView = mInflater.inflate(R.layout.actionbar_custom_layout, null);
        //toolbar.setCustomView(mCustomView);
        //toolbar.setDisplayShowCustomEnabled(true);
        View mCustomView =  toolbar.getRootView();

        profileImage = mCustomView.findViewById(R.id.profile_image);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
//        mDatabase.setPersistenceEnabled(true);

        if(mCurrentUser==null){
            goToSignInActivity();
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        checkUserAccount();

        // load the store fragment by default
        toolbar.setTitle("Med Manager");
        loadFragment(new MyProfileFragment());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_my_profile:
                    fragment = new MyProfileFragment();
                    loadFragment(fragment);
                    toolbar.setTitle("My Profile");
                    return true;
                case R.id.navigation_add_med:
                    fragment = new AddMedFragment();
                    loadFragment(fragment);
                    toolbar.setTitle("Monthly Intake");
                    return true;
                case R.id.navigation_reminders:
                   fragment = new RemindersFragment();
                   loadFragment(fragment);
                   toolbar.setTitle("Reminders");
                    return true;
                case R.id.navigation_my_meds:
                    fragment = new MyMedsFragment();
                    loadFragment(fragment);
                    toolbar.setTitle("My Medicines");
                    return true;
            }
            return false;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            signOutUser();
        }else if (id==R.id.action_my_account){
            Intent intent = new Intent(HomeActivityMain.this,SetupActivity.class);
            intent.setAction(SetupActivity.START_ACTION_UPDATE);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutUser() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        //finishAffinity();
                        goToSignInActivity();
                    }
                });
    }

    private void goToSignInActivity() {
        Intent intent = new Intent(HomeActivityMain.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void goToUserSetupAccount() {
        Intent intent = new Intent(HomeActivityMain.this, SetupActivity.class);
        intent.setAction(SetupActivity.START_ACTION_CREATE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void checkUserAccount(){
        DatabaseReference mUserRef = mDatabase.getReference().child("Users").child(mCurrentUser.getUid());
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    User user = dataSnapshot.getValue(User.class);
                    if (user==null){
                        goToUserSetupAccount();
                    }

                    /*//inflate views
                    TextView userName = mNavigationView.findViewById(R.id.nav_header_text_name);
                    TextView userEmail = mNavigationView.findViewById(R.id.nav_header_text_email);
                    CircleImageView userProfileImage = mNavigationView.findViewById(R.id.image_view_user_profile);


                    assert user != null;
                    userName.setText(user.getUserName());
                    userEmail.setText(mCurrentUser.getEmail());

                    GlideApp.with(HomeActivityMain.this)
                            .load(user.getProfileImage())
                            .centerCrop()
                            .placeholder(R.drawable.default_avatar)
                            .skipMemoryCache(false)
                            .into(userProfileImage);*/
                    try {
                        GlideApp.with(HomeActivityMain.this)
                                .load(user.getProfileImage())
                                .centerCrop()
                                .placeholder(R.drawable.default_avatar)
                                .skipMemoryCache(false)
                                .into(profileImage);
                    }catch (IllegalArgumentException e){
                        e.printStackTrace();
                    }

                }else {
                    goToUserSetupAccount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * loading fragment into FrameLayout
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
