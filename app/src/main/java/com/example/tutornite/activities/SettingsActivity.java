package com.example.tutornite.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tutornite.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity {

    ImageView back_img;
    CheckBox notification_chkbox;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        notification_chkbox = findViewById(R.id.notification_chkbox);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });


    }
}