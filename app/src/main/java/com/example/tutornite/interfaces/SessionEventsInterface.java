package com.example.tutornite.interfaces;

public interface SessionEventsInterface {
    void joinSession(String documentID, String sessionTitle, int position);

    void cancelSession(String documentID, int position);

    void deleteSession(String documentID, int position);
}
