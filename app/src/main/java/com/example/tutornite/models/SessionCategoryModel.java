package com.example.tutornite.models;

import com.google.firebase.firestore.Exclude;

public class SessionCategoryModel {
    String name, documentID;

    public SessionCategoryModel() {
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
