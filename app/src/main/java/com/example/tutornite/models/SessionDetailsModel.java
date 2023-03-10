package com.example.tutornite.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class SessionDetailsModel {

    String documentID, postedByUID, sessionLocation, sessionDetails,
            sessionTitle, userThumb, categoryID, postedBy;
    Timestamp sessionDateTime;

    public SessionDetailsModel() {
    }

    public String getPostedByUID() {
        return postedByUID;
    }

    public void setPostedByUID(String postedByUID) {
        this.postedByUID = postedByUID;
    }

    public String getSessionLocation() {
        return sessionLocation;
    }

    public void setSessionLocation(String sessionLocation) {
        this.sessionLocation = sessionLocation;
    }

    public String getSessionDetails() {
        return sessionDetails;
    }

    public void setSessionDetails(String sessionDetails) {
        this.sessionDetails = sessionDetails;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getUserThumb() {
        return userThumb;
    }

    public void setUserThumb(String userThumb) {
        this.userThumb = userThumb;
    }

    public Timestamp getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(Timestamp sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
