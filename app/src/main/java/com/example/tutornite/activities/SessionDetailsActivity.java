package com.example.tutornite.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.tutornite.R;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class SessionDetailsActivity extends AppCompatActivity {

    SessionDetailsModel sessionDetailsModel = new SessionDetailsModel();

    ImageView back_img, user_img;
    Button btn_session_join;
    TextView txt_session_title, txt_tutor_name, txt_event_short_desc, txt_event_address, txt_event_date, txt_event_time;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setEvents();
        populateSessionDetails();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        user_img = findViewById(R.id.img_user_image);
        btn_session_join = findViewById(R.id.btn_session_join);
        txt_session_title = findViewById(R.id.txt_session_title);
        txt_tutor_name = findViewById(R.id.txt_tutor_name);
        txt_event_address = findViewById(R.id.txt_event_address);
        txt_event_date = findViewById(R.id.txt_event_date);
        txt_event_time = findViewById(R.id.txt_event_time);
        txt_event_short_desc = findViewById(R.id.txt_event_short_desc);
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void populateSessionDetails() {
        txt_session_title.setText(sessionDetailsModel.getSessionTitle());
        txt_tutor_name.setText(sessionDetailsModel.getPostedByUID());
        //user_img.setImageBitmap(sessionDetailsModel.getUserThumb());
        //txt_event_time.setText(sessionDetailsModel.getSessionDateTime());
        txt_event_short_desc.setText(sessionDetailsModel.getSessionDetails());
        txt_event_address.setText(sessionDetailsModel.getSessionLocation());

    }
}