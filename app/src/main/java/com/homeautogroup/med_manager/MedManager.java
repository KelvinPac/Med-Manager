package com.homeautogroup.med_manager;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;


/**
 * MedManager Created by kelvin on 09/04/18.
 * com.homeautogroup.med_manager
 * flyboypac@gmail.com
 * +254705419309
 */
public class MedManager extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        if (!FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
