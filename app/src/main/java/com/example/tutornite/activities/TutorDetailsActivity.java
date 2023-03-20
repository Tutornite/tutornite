package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.USER_ID;
import static com.example.tutornite.utils.FireStoreConstants.USERS;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.models.UserModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorDetailsActivity extends BaseActivity {

    ImageView back_img;
    CircleImageView user_image;
    TextView txt_tutor_skills, txt_tutor_college, txt_full_name;
    Button btn_donate;

    String paymentLink = "";

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_details);

        db = FirebaseFirestore.getInstance();

        initViews();
        setEvents();
        getTutorDetails(getIntent().getStringExtra(USER_ID));
    }

    private void getTutorDetails(String userID) {
        db.collection(USERS).document(userID)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UserModel tutorModel = document.toObject(UserModel.class);
                            setProfileData(tutorModel);
                        } else {
                            Toast.makeText(this, "Sorry, tutor details not available", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    } else {
                        Toast.makeText(this, "Sorry, tutor details not available", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
    }

    private void setProfileData(UserModel userModel) {
        if (!TextUtils.isEmpty(userModel.getUserImage())) {
            Glide.with(this)
                    .load(Base64.decode(userModel.getUserImage(), Base64.DEFAULT))
                    .into(user_image);
        }
        txt_full_name.setText(userModel.getFirstName() + " " + userModel.getLastName());
        txt_tutor_college.setText(userModel.getCollege());
        txt_tutor_skills.setText(userModel.getSkills());
        paymentLink = userModel.getPaymentLink();
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_donate.setOnClickListener(view -> {
            openWebPage(paymentLink);
        });
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        btn_donate = findViewById(R.id.btn_donate);
        txt_tutor_skills = findViewById(R.id.txt_tutor_skills);
        txt_full_name = findViewById(R.id.txt_full_name);
        txt_tutor_college = findViewById(R.id.txt_tutor_college);
        user_image = findViewById(R.id.user_image);
    }
}