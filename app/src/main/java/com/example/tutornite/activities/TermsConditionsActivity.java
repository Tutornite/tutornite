package com.example.tutornite.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tutornite.R;

public class TermsConditionsActivity extends AppCompatActivity {

    ImageView back_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        back_img = findViewById(R.id.back_img);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}