package com.homeautogroup.med_manager.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * MedManager Created by kelvin on 08/04/18.
 * com.homeautogroup.med_manager
 * flyboypac@gmail.com
 * +254705419309
 */

@SuppressWarnings("unused")
@IgnoreExtraProperties
public class User {

    private String userName;
    private String profileImage;
    private int userAge;
    private int userWeight;
    private String userSex;
    private int userHeight;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String profileImage, int userAge, int userWeight, String userSex, int userHeight) {
        this.userName = userName;
        this.profileImage = profileImage;
        this.userAge = userAge;
        this.userWeight = userWeight;
        this.userSex = userSex;
        this.userHeight = userHeight;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public int getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(int userWeight) {
        this.userWeight = userWeight;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public int getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(int userHeight) {
        this.userHeight = userHeight;
    }
}
