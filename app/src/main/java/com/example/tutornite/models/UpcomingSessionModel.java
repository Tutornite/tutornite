package com.example.tutornite.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class UpcomingSessionModel {

    String sessionTitle, documentID;
    Timestamp sessionDateTime;
    boolean attended;

    public UpcomingSessionModel() {
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public Timestamp getSessionDateTime() {
        return sessionDateTime;
    }

    public void setSessionDateTime(Timestamp sessionDateTime) {
        this.sessionDateTime = sessionDateTime;
    }

    public boolean isAttended() {
        return attended;
    }

    public void setAttended(boolean attended) {
        this.attended = attended;
    }
}
