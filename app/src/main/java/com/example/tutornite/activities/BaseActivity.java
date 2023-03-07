package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.upcomingSessions;
import static com.example.tutornite.utils.FireStoreConstants.PARTICIPANTS;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.SESSION_TITLE;
import static com.example.tutornite.utils.FireStoreConstants.UPCOMING_SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_EMAIL;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tutornite.R;
import com.example.tutornite.interfaces.SessionCancelInterface;
import com.example.tutornite.interfaces.SessionJoinInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseActivity extends AppCompatActivity {

    public Dialog progressDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this);
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        /*1. Upper Letter 2. Lower Letter 3. Number 4. maximum 8 digit*/
        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return !TextUtils.isEmpty(password) && matcher.matches();
    }

    public void joinSessionBase(FirebaseFirestore db, String sessionID, String userID, String userEmail, String sessionTitle, SessionJoinInterface sessionActionsInterface) {
        showProgressDialog();
        Map<String, Object> user = new HashMap<>();
        user.put(USER_EMAIL, userEmail);

        db.collection(SESSIONS)
                .document(sessionID)
                .collection(PARTICIPANTS)
                .document(userID)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    addSessionToPersonalProfile(db, userID, sessionID, sessionTitle, sessionActionsInterface);
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void addSessionToPersonalProfile(FirebaseFirestore db, String userID, String sessionID, String sessionTitle, SessionJoinInterface sessionActionsInterface) {
        Map<String, Object> session = new HashMap<>();
        session.put(SESSION_TITLE, sessionTitle);

        db.collection(USERS)
                .document(Objects.requireNonNull(userID))
                .collection(UPCOMING_SESSIONS)
                .document(sessionID)
                .set(session)
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Joined successfully", Toast.LENGTH_SHORT).show();
                    upcomingSessions.add(sessionID);
                    sessionActionsInterface.joinSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    public void cancelJoinSessionBase(FirebaseFirestore db, String sessionID, String userID, SessionCancelInterface sessionCancelInterface) {
        showProgressDialog();

        db.collection(SESSIONS)
                .document(sessionID)
                .collection(PARTICIPANTS)
                .document(userID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    removeSessionFromPersonalProfile(db, userID, sessionID, sessionCancelInterface);
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void removeSessionFromPersonalProfile(FirebaseFirestore db, String userID, String sessionID, SessionCancelInterface sessionCancelInterface) {
        db.collection(USERS)
                .document(Objects.requireNonNull(userID))
                .collection(UPCOMING_SESSIONS)
                .document(sessionID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    upcomingSessions.remove(sessionID);
                    Toast.makeText(this, "Cancelled successfully", Toast.LENGTH_SHORT).show();
                    sessionCancelInterface.cancelledSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
