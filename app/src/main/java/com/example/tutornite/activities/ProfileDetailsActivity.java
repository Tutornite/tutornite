package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.app_date_format;
import static com.example.tutornite.utils.Constants.currentUserModel;
import static com.example.tutornite.utils.Constants.remoteColleges;
import static com.example.tutornite.utils.Constants.remoteSkills;
import static com.example.tutornite.utils.DateTimeFormatter.convertTimestampToFormat;
import static com.example.tutornite.utils.FireStoreConstants.COLLEGES;
import static com.example.tutornite.utils.FireStoreConstants.NAME;
import static com.example.tutornite.utils.FireStoreConstants.SKILLS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE_TUTOR;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.models.UserModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileDetailsActivity extends BaseActivity {

    ImageView back_img;
    CircleImageView user_image;
    TextView txt_payment_link, txt_selected_skills, txt_selected_college, txt_date;
    EditText edt_payment_link, edt_first_name, edt_last_name, edt_email;
    Button btn_submit;
    DatePickerDialog datePicker;

    boolean[] selectedSkills;
    ArrayList<Integer> skillsLst = new ArrayList<>();
    String[] skillsArray;

    final int[] selectedCollege = {-1};
    String[] collegesList;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    Bitmap selectedProfileImage = null;

    UserModel userModel = new UserModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetails);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        userModel = currentUserModel;

        initViews();
        setEvents();
        fetchColleges();
        fetchSkills();
        setProfileData();
    }

    private void setProfileData() {
        if (!TextUtils.isEmpty(userModel.getUserImage())) {
            Glide.with(this)
                    .load(Base64.decode(userModel.getUserImage(), Base64.DEFAULT))
                    .into(user_image);
        }
        edt_first_name.setText(userModel.getFirstName());
        edt_last_name.setText(userModel.getLastName());
        txt_date.setText(convertTimestampToFormat(app_date_format, userModel.getBirthOfDate()));
        txt_selected_college.setText(userModel.getCollege());
        txt_selected_skills.setText(userModel.getSkills());
        if (userModel.getType().equalsIgnoreCase(USER_TYPE_TUTOR)) {
            txt_payment_link.setVisibility(View.VISIBLE);
            edt_payment_link.setVisibility(View.VISIBLE);
            edt_payment_link.setText(currentUserModel.getPaymentLink());
        } else {
            txt_payment_link.setVisibility(View.GONE);
            edt_payment_link.setVisibility(View.GONE);
        }
    }

    private void setEvents() {
        edt_email.setText(currentUser.getEmail());

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_submit.setOnClickListener(view -> {
            validateAndProcessSubmit();
        });

        user_image.setOnClickListener(view -> {
            if (checkAndRequestPermissions(this)) {
                chooseImage(this);
            }
        });

        edt_first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userModel.setFirstName(editable.toString());
            }
        });

        edt_last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userModel.setLastName(editable.toString());
            }
        });

        edt_payment_link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userModel.setPaymentLink(editable.toString());
            }
        });

        txt_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(ProfileDetailsActivity.this, (datePicker, year1, month1, day1) -> {
                calendar.set(year1, month1, day1);
                Timestamp timestamp = new Timestamp(new Date(calendar.getTimeInMillis()));
                userModel.setBirthOfDate(timestamp);
                txt_date.setText(convertTimestampToFormat(app_date_format, timestamp));
            }, year, month, day);
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePicker.show();
        });

        txt_selected_skills.setOnClickListener(view -> {
            showSkillsDialog();
        });

        txt_selected_college.setOnClickListener(view -> {
            showCollegesDialog();
        });
    }

    private void fetchColleges() {
        showProgressDialog();
        db.collection(COLLEGES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> collegesList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            collegesList.add(documentSnapshot.getString(NAME));
                        }
                        remoteColleges.clear();
                        remoteColleges.addAll(collegesList);
                    }
                    hideProgressDialog();
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

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        txt_payment_link = findViewById(R.id.txt_payment_link);
        edt_payment_link = findViewById(R.id.edt_payment_link);
        btn_submit = findViewById(R.id.btn_submit);
        txt_date = findViewById(R.id.txt_date);
        txt_selected_skills = findViewById(R.id.txt_selected_skills);
        edt_first_name = findViewById(R.id.edt_first_name);
        edt_last_name = findViewById(R.id.edt_last_name);
        edt_email = findViewById(R.id.edt_email);
        txt_selected_college = findViewById(R.id.txt_selected_college);
        user_image = findViewById(R.id.user_image);
    }

    private void showSkillsDialog() {
        skillsArray = new String[remoteSkills.size()];
        skillsArray = remoteSkills.toArray(skillsArray);
        if (selectedSkills == null) {
            selectedSkills = new boolean[skillsArray.length];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
        builder.setTitle("Select Skills");
        builder.setCancelable(false);

        builder.setMultiChoiceItems(skillsArray, selectedSkills, (dialogInterface, i, b) -> {
            if (b) {
                skillsLst.add(i);
                Collections.sort(skillsLst);
            } else {
                skillsLst.remove(Integer.valueOf(i));
            }
        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int j = 0; j < skillsLst.size(); j++) {
                stringBuilder.append(skillsArray[skillsLst.get(j)]);
                if (j != skillsLst.size() - 1) {
                    stringBuilder.append(", ");
                }
            }
            txt_selected_skills.setText(stringBuilder.toString());
            userModel.setSkills(txt_selected_skills.getText().toString());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
            for (int j = 0; j < selectedSkills.length; j++) {
                selectedSkills[j] = false;
                skillsLst.clear();
                txt_selected_skills.setText("");
                userModel.setSkills("");
            }
        });
        builder.show();
    }

    private void showCollegesDialog() {
        collegesList = new String[remoteColleges.size()];
        collegesList = remoteColleges.toArray(collegesList);

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileDetailsActivity.this);
        builder.setTitle("Select College");
        builder.setCancelable(false);

        builder.setSingleChoiceItems(collegesList, selectedCollege[0], (dialog, which) -> {
            selectedCollege[0] = which;
            txt_selected_college.setText(collegesList[which]);
            userModel.setCollege(txt_selected_college.getText().toString());
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        builder.show();
    }

    private void validateAndProcessSubmit() {
        if (selectedProfileImage != null) {
            String userImage = generateBitmapToBase64(selectedProfileImage);
            userModel.setUserImage(userImage);
        }
        if (TextUtils.isEmpty(userModel.getUserImage())) {
            Toast.makeText(this, "Please select profile image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userModel.getFirstName())) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userModel.getLastName())) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userModel.getBirthOfDate() == null) {
            Toast.makeText(this, "Please select birth of date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userModel.getCollege())) {
            Toast.makeText(this, "Please select college", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userModel.getSkills())) {
            Toast.makeText(this, "Please select skills", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userModel.getType().equalsIgnoreCase(USER_TYPE_TUTOR)) {
            if (!isValidURL(userModel.getPaymentLink())) {
                Toast.makeText(this, "Please enter valid payment link", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        showProgressDialog();
        db.collection(USERS).document(currentUser.getUid())
                .set(userModel)
                .addOnSuccessListener(aVoid -> {
                    currentUserModel = userModel;
                    hideProgressDialog();
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    hideProgressDialog();
                    Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        setImage(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            Bitmap selectedImageBitmap = null;
                            try {
                                selectedImageBitmap
                                        = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(),
                                        selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (selectedImageBitmap != null) {
                                setImage(selectedImageBitmap);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private void setImage(Bitmap selectedImage) {
        user_image.setImageBitmap(selectedImage);
        selectedProfileImage = selectedImage;
    }
}