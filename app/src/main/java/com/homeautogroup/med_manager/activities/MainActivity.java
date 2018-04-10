package com.homeautogroup.med_manager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.homeautogroup.med_manager.R;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        if (currentUser!=null){
            goToHomeActivity();
            Toast.makeText(this, currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }else {
            startUserSignIn();
        }

    }

    private void goToHomeActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivityMain.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private void startUserSignIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                goToHomeActivity();
                // ...
            } else {
                // Sign in failed, check response for error code
                // ...
                //todo handle errors here
                finishAffinity();
            }
        }
    }
}
