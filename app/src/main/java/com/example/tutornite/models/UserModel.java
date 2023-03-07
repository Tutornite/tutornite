package com.example.tutornite.models;

import com.google.firebase.Timestamp;

public class UserModel {

    Timestamp birthOfDate;
    String email, firstName, lastName, userImage;

    public UserModel() {
    }

    public Timestamp getBirthOfDate() {
        return birthOfDate;
    }

    public void setBirthOfDate(Timestamp birthOfDate) {
        this.birthOfDate = birthOfDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
