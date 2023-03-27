package com.example.tutornite.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tutornite.R;
import com.example.tutornite.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        notification_chkbox = findViewById(R.id.notification_chkbox);

        enable_notification_nav = findViewById(R.id.enable_notification_nav);
        about_layout = findViewById(R.id.about_layout);
        support_layout = findViewById(R.id.support_layout);

        notification_chkbox.setOnCheckedChangeListener((compoundButton, isChecked) ->
        {
            if(isChecked) {

            }
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