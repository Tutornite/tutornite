package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.attendedSessions;
import static com.example.tutornite.utils.Constants.currentUserModel;
import static com.example.tutornite.utils.FireStoreConstants.ATTENDED;
import static com.example.tutornite.utils.FireStoreConstants.IS_NOTIFICATION_ENABLED;
import static com.example.tutornite.utils.FireStoreConstants.UPCOMING_SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tutornite.R;
import com.example.tutornite.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends BaseActivity {

    ImageView back_img;
    CheckBox notification_chkbox;
    LinearLayout enable_notification_nav, about_layout, support_layout;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setNotificationCheckbox();
    }

    private void setNotificationCheckbox() {
        notification_chkbox.setChecked(currentUserModel.getNotificationEnabled());
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        notification_chkbox = findViewById(R.id.notification_chkbox);

        enable_notification_nav = findViewById(R.id.enable_notification_nav);
        about_layout = findViewById(R.id.about_layout);
        support_layout = findViewById(R.id.support_layout);

        notification_chkbox.setOnCheckedChangeListener((compoundButton, isChecked) ->
        {
            showProgressDialog();

            Map<String, Object> map = new HashMap<>();
            map.put(IS_NOTIFICATION_ENABLED, isChecked);
            db.collection(USERS)
                    .document(Objects.requireNonNull(currentUser.getUid()))
                    .update(map)
                    .addOnSuccessListener(aVoid -> {
                        hideProgressDialog();
                        currentUserModel.setNotificationEnabled(isChecked);
                        if(isChecked){
                            Toast.makeText(this, getString(R.string.notif_enabled_confirmation), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.notif_disabled_confirmation), Toast.LENGTH_SHORT).show();
                        }

                    }).addOnFailureListener(e -> {
                        hideProgressDialog();
                        Toast.makeText(this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    });
        });

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        about_layout.setOnClickListener(view -> {
            openWebPage(Constants.rating_app_url);
        });

        support_layout.setOnClickListener(view -> {
            openWebPage(Constants.rating_app_url);
        });

    }



}