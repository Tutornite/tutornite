package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.Constants.currentUserModel;
import static com.example.tutornite.utils.Constants.remoteColleges;
import static com.example.tutornite.utils.Constants.remoteSkills;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;
import static com.example.tutornite.utils.FireStoreConstants.NAME;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.SKILLS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE_LEARNER;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE_TUTOR;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tutornite.R;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SessionCreateActivity extends BaseActivity {

    ImageView back_img;
    Button btn_submit;
    EditText edt_session_title, edt_session_location, edt_session_details;
    TextView txt_session_date, select_session_category;
    DatePickerDialog datePicker;

    String[] categoryList;
    final int[] selectedCategory = {-1};

    SessionDetailsModel sessionModel = new SessionDetailsModel();


    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_create);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setEvents();
        fetchSkills();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        btn_submit = findViewById(R.id.btn_submit);
        txt_session_date = findViewById(R.id.txt_session_date);
        select_session_category = findViewById(R.id.txt_selected_category);
        edt_session_title = findViewById(R.id.edt_session_title);
        edt_session_location = findViewById(R.id.edt_session_location);
        edt_session_details = findViewById(R.id.edt_session_details);
    }

    private void setEvents() {

        sessionModel.setPostedByUID(currentUser.getUid());

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_submit.setOnClickListener(view -> {
            validateAndProcessSubmit();
        });

        edt_session_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sessionModel.setSessionTitle(editable.toString());
            }
        });

        edt_session_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sessionModel.setSessionLocation(editable.toString());
            }
        });

        edt_session_details.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sessionModel.setSessionDetails(editable.toString());
            }
        });

        txt_session_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(SessionCreateActivity.this, (datePicker, year1, month1, day1) -> {
                calendar.set(year1, month1, day1);
                Timestamp timestamp = new Timestamp(new Date(calendar.getTimeInMillis()));
                sessionModel.setSessionDateTime(timestamp);
                txt_session_date.setText(convertTimestampToFormat(app_date_format, timestamp));
            }, year, month, day);
            datePicker.show();
        });

        select_session_category.setOnClickListener(view -> {
            showCategoryDialog();
        });

    }

    private void fetchSkills() {
        showProgressDialog();
        db.collection(SKILLS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> skillsList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            skillsList.add(documentSnapshot.getString(NAME));
                        }
                        remoteSkills.clear();
                        remoteSkills.addAll(skillsList);
                    }
                    hideProgressDialog();
                });
    }

    private void showCategoryDialog() {
        categoryList = new String[remoteSkills.size()];
        categoryList = remoteSkills.toArray(categoryList);

        AlertDialog.Builder builder = new AlertDialog.Builder(SessionCreateActivity.this);
        builder.setTitle("Select Category");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(categoryList, selectedCategory[0], (dialog, which) -> {
            selectedCategory[0] = which;
            select_session_category.setText(categoryList[which]);
            sessionModel.setCategoryID(select_session_category.getText().toString());
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.show();
    }

    private void validateAndProcessSubmit() {

        if (TextUtils.isEmpty(sessionModel.getSessionTitle())) {
            Toast.makeText(this, "Please Session Title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sessionModel.getSessionLocation())) {
            Toast.makeText(this, "Please enter session location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sessionModel.getSessionDetails())) {
            Toast.makeText(this, "Please enter session details", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sessionModel.getSessionDateTime() == null) {
            Toast.makeText(this, "Please select date of session", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sessionModel.getCategoryID())) {
            Toast.makeText(this, "Please select Category", Toast.LENGTH_SHORT).show();
            return;
        }
        sessionModel.setUserThumb(currentUserModel.getUserImage());

        showProgressDialog();
        db.collection(SESSIONS).add(sessionModel)
                .addOnSuccessListener(aVoid -> {
                    hideProgressDialog();
                    Intent i = new Intent(this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

}