package com.homeautogroup.med_manager.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

/**
 * MedManager Created by kelvin on 10/04/18.
 * com.homeautogroup.med_manager.models
 * flyboypac@gmail.com
 * +254705419309
 */
public class MedicineExtra {

    @Exclude
    public String uniqueFirebaseId;

    @SuppressWarnings("unchecked")
    public <T extends MedicineExtra> T withUniqueId(@NonNull final String uniqueId){
        this.uniqueFirebaseId = uniqueId;
        return (T) this;

    }
}
