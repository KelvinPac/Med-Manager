package com.homeautogroup.med_manager.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.homeautogroup.med_manager.R;
import com.homeautogroup.med_manager.models.User;
import com.homeautogroup.med_manager.utils.GlideApp;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE =55;
    private CircleImageView mSetupImageButton;
    private  Uri mImageUri =null;
    private boolean isProfilePicChanged;

    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageImage;
    private ProgressDialog mProgress;

    public static String START_ACTION_CREATE = "create_account";
    public static String START_ACTION_UPDATE = "update_account";

    //EditTexts
    //private TextInputLayout mInputLayoutAge,mInputLayoutWeight;
    private TextInputEditText mInputAge,mInputWeight,mSetupNameField,mInputHeight;

    private RadioGroup radioGroup;
    private RadioButton maleRadioButton,femaleRadioButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Intent intent = getIntent();
        String startAction = intent.getAction();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Button mSetupButton = findViewById(R.id.setupFinishButton);
        mSetupImageButton = findViewById(R.id.setupImageButton);
        mSetupNameField = findViewById(R.id.setupNameField);

        // mInputLayoutAge = findViewById(R.id.input_layout_age);
        mInputHeight = findViewById(R.id.input_editText_height);
        mInputAge = findViewById(R.id.input_editText_age);
        mInputWeight = findViewById(R.id.input_editText_weight);
        radioGroup = findViewById(R.id.radio_group_sex);
        maleRadioButton = findViewById(R.id.radioButtonMale);
        femaleRadioButton = findViewById(R.id.radioButtonFemale);
        mProgress= new ProgressDialog(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageImage = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser==null){
            //user is not signed in
            goToSignInActivity();
        }

        mSetupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestMultiplePermission();
            }
        });
        mSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAccountSetup();
            }
        });

        if (startAction.equals(START_ACTION_UPDATE)){
            checkUserAccount();
        }else {
            mSetupButton.setText("Create Account");
            mSetupNameField.setText(mCurrentUser.getDisplayName());
        }
    }

    private void checkUserAccount(){
        mProgress.setMessage("Checking account");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        DatabaseReference mUserRef = mDatabaseUsers.child(mCurrentUser.getUid());
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgress.cancel();
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    //set data
                    mSetupNameField.setText(user.getUserName());
                    mInputAge.setText(String.valueOf(user.getUserAge()));
                    mInputWeight.setText(String.valueOf(user.getUserWeight()));
                    mImageUri = Uri.parse(user.getProfileImage());
                    mInputHeight.setText(String.valueOf(user.getUserHeight()));
                    radioGroup.clearCheck();
                    if (user.getUserSex().equals("Male")){
                        maleRadioButton.setChecked(true);
                    }else {
                        femaleRadioButton.setChecked(true);
                    }
                    GlideApp.with(SetupActivity.this)
                            .load(user.getProfileImage())
                            .centerCrop()
                            .placeholder(R.drawable.default_avatar)
                            .skipMemoryCache(false)
                            .into(mSetupImageButton);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgress.cancel();
            }
        });

    }
    private void startAccountSetup() {

        final String name = mSetupNameField.getText().toString().trim();
        final String user_id = mCurrentUser.getUid();
        final int userAge = Integer.valueOf(mInputAge.getText().toString().trim());
        final int userWeight = Integer.valueOf(mInputWeight.getText().toString().trim());
        final int userHeight = Integer.valueOf(mInputHeight.getText().toString().trim());

        if (userAge <=0 || userWeight <=0 || userHeight <=0){
            Toast.makeText(this, "Age, weight or height is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri==null){
            Toast.makeText(this, "Profile image not set", Toast.LENGTH_SHORT).show();
        }


        if (!TextUtils.isEmpty(name) && mImageUri !=null){

            if (isProfilePicChanged){

                mProgress.setMessage("Finishing Setup...");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                StorageReference filepath = mStorageImage.child(user_id+".jpg");
                filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                        String userSex;
                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                        if (checkedRadioButtonId == R.id.radioButtonMale){
                            userSex = "Male";
                        }else {
                            userSex = "Female";
                        }

                        User user = new User(name,downloadUrl,userAge,userWeight,userSex,userHeight);
                        // mDatabaseUsers.child(user_id).child("name").setValue(name);
                        //mDatabaseUsers.child(user_id).child("image").setValue(downloadUrl);
                        mDatabaseUsers.child(user_id).setValue(user);

                        mProgress.dismiss();
                        Intent main = new Intent(SetupActivity.this, SignInActivity.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgress.cancel();
                        Toast.makeText(SetupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {

                String userSex;
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.radioButtonMale){
                    userSex = "Male";
                }else {
                    userSex = "Female";
                }

                User user = new User(name,mImageUri.toString(),userAge,userWeight,userSex,userHeight);
                mDatabaseUsers.child(user_id).setValue(user);
                finish();
            }



        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== GALLERY_REQUEST_CODE && resultCode==RESULT_OK){

            Uri imageUri = data.getData();

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                   // .setCropShape(CropImageView.CropShape.OVAL)
                    .setRequestedSize(300, 300)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
               // Uri resultUri = result.getUri();
                mImageUri=result.getUri();
                mSetupImageButton.setImageURI(mImageUri);
                isProfilePicChanged = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToSignInActivity() {
        Intent intent = new Intent(SetupActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestMultiplePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            openGallery();
                            Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void openGallery(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }
}
