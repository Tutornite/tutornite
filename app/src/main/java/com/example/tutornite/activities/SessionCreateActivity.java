package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.Constants.currentUserModel;
import static com.example.tutornite.utils.Constants.remoteSessionsCategoryList;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.tutornite.R;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

public class SessionCreateActivity extends BaseActivity {

    ImageView back_img;
    Button btn_submit;
    EditText edt_session_title, edt_session_location, edt_session_details;
    TextView txt_session_date, select_session_category, txt_session_time;
    DatePickerDialog datePicker;

    boolean dateDone = false, timeDone = false;
    Calendar sessionDateTime = Calendar.getInstance();

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
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        btn_submit = findViewById(R.id.btn_submit);
        txt_session_date = findViewById(R.id.txt_session_date);
        select_session_category = findViewById(R.id.txt_selected_category);
        edt_session_title = findViewById(R.id.edt_session_title);
        edt_session_location = findViewById(R.id.edt_session_location);
        edt_session_details = findViewById(R.id.edt_session_details);
        txt_session_time = findViewById(R.id.txt_session_time);
    }

    private void setEvents() {

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
                sessionDateTime.set(year1, month1, day1);
                dateDone = true;
                Timestamp timestamp = new Timestamp(new Date(calendar.getTimeInMillis()));
                txt_session_date.setText(convertTimestampToFormat(app_date_format, timestamp));

                if (dateDone && timeDone) {
                    Timestamp timestampn = new Timestamp(new Date(sessionDateTime.getTimeInMillis()));
                    sessionModel.setSessionDateTime(timestampn);
                }
            }, year, month, day);
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        });

        txt_session_time.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
            int initialMinute = calendar.get(Calendar.MINUTE);

            TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
                sessionDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                sessionDateTime.set(Calendar.MINUTE, minute);
                timeDone = true;
                txt_session_time.setText(hourOfDay + ":" + minute);

                if (dateDone && timeDone) {
                    Timestamp timestamp = new Timestamp(new Date(sessionDateTime.getTimeInMillis()));
                    sessionModel.setSessionDateTime(timestamp);
                }
            };
            TimePickerDialog timePickerDialog =
                    new TimePickerDialog(this, timeSetListener, initialHour, initialMinute, true);
            timePickerDialog.show();
        });

        select_session_category.setOnClickListener(view -> {
            showCategoryDialog();
        });

    }

    private void showCategoryDialog() {
        categoryList = new String[remoteSessionsCategoryList.size()];
        for (int i = 0; i < remoteSessionsCategoryList.size(); i++) {
            categoryList[i] = remoteSessionsCategoryList.get(i).getName();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(SessionCreateActivity.this);
        builder.setTitle("Select Category");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(categoryList, selectedCategory[0], (dialog, which) -> {
            selectedCategory[0] = which;
            select_session_category.setText(categoryList[which]);
            sessionModel.setCategoryID(remoteSessionsCategoryList.get(which).getDocumentID());
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.show();
    }

    private void validateAndProcessSubmit() {
        if (TextUtils.isEmpty(sessionModel.getSessionTitle())) {
            Toast.makeText(this, "Please enter session title", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Please select date & time of session", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(sessionModel.getCategoryID())) {
            Toast.makeText(this, "Please select Category", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionModel.setUserThumb(currentUserModel.getUserImage());
        sessionModel.setPostedByUID(currentUser.getUid());
        sessionModel.setPostedBy(currentUserModel.getFirstName() + " " + currentUserModel.getLastName());

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