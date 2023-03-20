package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.USER_ID;
import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.Constants.sessionDetails;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class SessionDetailsActivity extends BaseActivity {

    ImageView back_img, user_img;
    TextView txt_session_title, txt_tutor_name, txt_event_short_desc, txt_event_address, txt_event_date, txt_event_time;
    LinearLayout lin_organised_by;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        db = FirebaseFirestore.getInstance();

        initViews();
        setEvents();
        populateSessionDetails();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        user_img = findViewById(R.id.img_user_image);
        txt_session_title = findViewById(R.id.txt_session_title);
        txt_tutor_name = findViewById(R.id.txt_tutor_name);
        txt_event_address = findViewById(R.id.txt_event_address);
        txt_event_date = findViewById(R.id.txt_event_date);
        txt_event_time = findViewById(R.id.txt_event_time);
        txt_event_short_desc = findViewById(R.id.txt_event_short_desc);
        lin_organised_by = findViewById(R.id.lin_organised_by);
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        lin_organised_by.setOnClickListener(view -> {
            Intent intent = new Intent(this, TutorDetailsActivity.class);
            intent.putExtra(USER_ID, sessionDetails.getPostedByUID());
            startActivity(intent);
        });
    }

    private void populateSessionDetails() {
        if (sessionDetails != null) {
            SessionDetailsModel sessionDetailsModel = sessionDetails;

            txt_session_title.setText(sessionDetailsModel.getSessionTitle());
            txt_tutor_name.setText(sessionDetailsModel.getPostedBy());
            if (!TextUtils.isEmpty(sessionDetailsModel.getUserThumb())) {
                Glide.with(this)
                        .load(Base64.decode(sessionDetailsModel.getUserThumb(), Base64.DEFAULT))
                        .into(user_img);
            }

            txt_event_date.setText(convertTimestampToFormat(app_date_format, sessionDetailsModel.getSessionDateTime()));
            txt_event_time.setText(convertTimestampToFormat("hh:mm a", sessionDetailsModel.getSessionDateTime()).toUpperCase());
            txt_event_short_desc.setText(sessionDetailsModel.getSessionDetails());
            txt_event_address.setText(sessionDetailsModel.getSessionLocation());
        }
    }
}