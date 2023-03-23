package com.example.tutornite.interfaces;

import com.google.firebase.Timestamp;

public interface SessionEventsInterface {
    void joinSession(String documentID, String sessionTitle, int position, Timestamp sessionDateTime);

    void attendedSession(String documentID, int position);

    void cancelSession(String documentID, int position);

    void deleteSession(String documentID, int position);
}
