package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.attendedSessions;
import static com.example.tutornite.utils.Constants.remoteUpcomingSessions;
import static com.example.tutornite.utils.FireStoreConstants.ATTENDED;
import static com.example.tutornite.utils.FireStoreConstants.PARTICIPANTS;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.UPCOMING_SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_EMAIL;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tutornite.R;
import com.example.tutornite.interfaces.SessionCancelInterface;
import com.example.tutornite.interfaces.SessionJoinInterface;
import com.example.tutornite.models.UpcomingSessionModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseActivity extends AppCompatActivity {

    public Dialog progressDialog = null;
    public final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

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

    public boolean isValidURL(String url) {
        return !TextUtils.isEmpty(url) && URLUtil.isValidUrl(url);
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

    public void joinSessionBase(FirebaseFirestore db, String sessionID, String userID, String userEmail, String sessionTitle, Timestamp sessionDateTime, SessionJoinInterface sessionActionsInterface) {
        showProgressDialog();
        Map<String, Object> user = new HashMap<>();
        user.put(USER_EMAIL, userEmail);

        db.collection(SESSIONS)
                .document(sessionID)
                .collection(PARTICIPANTS)
                .document(userID)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    addSessionToPersonalProfile(db, userID, sessionID, sessionTitle, sessionActionsInterface, sessionDateTime);
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSessionToPersonalProfile(FirebaseFirestore db, String userID,
                                             String sessionID, String sessionTitle,
                                             SessionJoinInterface sessionActionsInterface,
                                             Timestamp sessionDateTime) {
        UpcomingSessionModel upcomingSessionModel = new UpcomingSessionModel();
        upcomingSessionModel.setSessionTitle(sessionTitle);
        upcomingSessionModel.setAttended(false);
        upcomingSessionModel.setSessionDateTime(sessionDateTime);

        db.collection(USERS)
                .document(Objects.requireNonNull(userID))
                .collection(UPCOMING_SESSIONS)
                .document(sessionID)
                .set(upcomingSessionModel)
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.joined_success), Toast.LENGTH_SHORT).show();
                    remoteUpcomingSessions.add(sessionID);
                    sessionActionsInterface.joinSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                });
    }

    public void markSessionAttended(FirebaseFirestore db, String sessionID, String userID, SessionJoinInterface sessionActionsInterface) {
        showProgressDialog();
        Map<String, Object> map = new HashMap<>();
        map.put(ATTENDED, true);

        db.collection(USERS)
                .document(Objects.requireNonNull(userID))
                .collection(UPCOMING_SESSIONS)
                .document(sessionID)
                .update(map)
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.thankyou_confirmation), Toast.LENGTH_SHORT).show();
                    attendedSessions.add(sessionID);
                    sessionActionsInterface.joinSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                });
    }

    public void deleteSession(FirebaseFirestore db, String sessionID, SessionCancelInterface sessionCancelInterface) {
        showProgressDialog();

        db.collection(SESSIONS)
                .document(sessionID)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.session_delete), Toast.LENGTH_SHORT).show();
                    sessionCancelInterface.cancelledSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
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
                    remoteUpcomingSessions.remove(sessionID);
                    Toast.makeText(this, getString(R.string.cancelled_success), Toast.LENGTH_SHORT).show();
                    sessionCancelInterface.cancelledSuccessfully();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                });
    }

    public static String generateBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public boolean checkAndRequestPermissions(final Activity context) {
        int W_Ext_storagePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (W_Ext_storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, (dialogInterface, i) -> {
            if (optionsMenu[i].equals("Take Photo")) {
                // Open the camera and get the photo
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            } else if (optionsMenu[i].equals("Choose from Gallery")) {
                // choose from  external storage
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            } else if (optionsMenu[i].equals("Exit")) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                                    "Tutornite Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(),
                            "Tutornite Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(this);
                }
                break;
        }
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_apps_can_manage), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
