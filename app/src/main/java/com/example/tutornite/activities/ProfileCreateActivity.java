package com.example.tutornite.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.tutornite.R;
import com.example.tutornite.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ProfileCreateActivity extends BaseActivity {

    ImageView back_img;
    RadioGroup profile_radio_group;
    RadioButton rb_tutor, rb_learner;
    Spinner college_spinner;
    TextView txt_skills, txt_payment_link, txt_selected_skills;
    EditText edt_payment_link, edt_date,edt_name;
    Button btn_submit;
    // Date picker declaration for calendar.
    DatePickerDialog datePicker;

    boolean[] selectedLanguage;
    ArrayList<Integer> langList = new ArrayList<>();
    String[] langArray = {"Java", "C++", "Kotlin", "C", "Python", "Javascript"};

    private FirebaseAuth mAuth;

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);
        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        profile_radio_group = findViewById(R.id.profile_radio_group);
        rb_tutor = findViewById(R.id.rb_tutor);
        rb_learner = findViewById(R.id.rb_learner);
        college_spinner = findViewById(R.id.college_spinner);
        txt_skills = findViewById(R.id.txt_skills);
        txt_payment_link = findViewById(R.id.txt_payment_link);
        edt_payment_link = findViewById(R.id.edt_payment_link);
        btn_submit = findViewById(R.id.btn_submit);
        edt_date = findViewById(R.id.edt_date);
        txt_selected_skills = findViewById(R.id.txt_selected_skills);
        edt_name = findViewById(R.id.edt_name);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_submit.setOnClickListener(view -> {
            validateAndProcessSubmit();
        });

        ArrayAdapter<CharSequence> collegeAdapter = ArrayAdapter.createFromResource(this, R.array.college_options, android.R.layout.simple_spinner_item);
        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        college_spinner.setAdapter(collegeAdapter);

        rb_tutor.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                txt_skills.setVisibility(View.VISIBLE);
                txt_skills.setText(R.string.select_skills_tutor);

                txt_payment_link.setVisibility(View.VISIBLE);
                edt_payment_link.setVisibility(View.VISIBLE);

                user.setProfileType(1);

            }
        });

        rb_learner.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                txt_skills.setVisibility(View.VISIBLE);
                txt_selected_skills.setVisibility(View.VISIBLE);
                txt_skills.setText(R.string.select_skills_learner);

                txt_payment_link.setVisibility(View.GONE);
                edt_payment_link.setVisibility(View.GONE);

                user.setProfileType(2);

            }
        });

        edt_date.setInputType(InputType.TYPE_NULL);
        // Listener function invoked when date text field is clicked.
        edt_date.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(ProfileCreateActivity.this, new DatePickerDialog.OnDateSetListener() {
                // Listener function called when the date is selected on the calendar.
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    edt_date.setText(day + "/" + (month + 1) + "/" + year);
                    user.setDateOfBirth(day + "/" + (month + 1) + "/" + year);
                }
            }, year, month, day);
            datePicker.show();
        });

        // initialize selected language array
        selectedLanguage = new boolean[langArray.length];

        txt_selected_skills.setOnClickListener(view -> {
            showSkillsDialog();
        });

    }

    private void showSkillsDialog() {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileCreateActivity.this);

        // set title
        builder.setTitle("Select Skills");

        // set dialog non cancelable
        builder.setCancelable(false);

        builder.setMultiChoiceItems(langArray, selectedLanguage, (dialogInterface, i, b) -> {
            // check condition
            if (b) {
                // when checkbox selected
                // Add position  in lang list
                langList.add(i);
                // Sort array list
                Collections.sort(langList);
            } else {
                // when checkbox unselected
                // Remove position from langList
                langList.remove(Integer.valueOf(i));
            }
        });

        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            // Initialize string builder
            StringBuilder stringBuilder = new StringBuilder();
            // use for loop
            for (int j = 0; j < langList.size(); j++) {
                // concat array value
                stringBuilder.append(langArray[langList.get(j)]);
                // check condition
                if (j != langList.size() - 1) {
                    // When j value  not equal
                    // to lang list size - 1
                    // add comma
                    stringBuilder.append(", ");
                }
            }
            // set text on textView
            txt_selected_skills.setText(stringBuilder.toString());
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            // dismiss dialog
            dialogInterface.dismiss();
        });

        builder.setNeutralButton("Clear All", (dialogInterface, i) -> {
            // use for loop
            for (int j = 0; j < selectedLanguage.length; j++) {
                // remove all selection
                selectedLanguage[j] = false;
                // clear language list
                langList.clear();
                // clear text view value
                txt_selected_skills.setText("");
            }
        });
        // show dialog
        builder.show();
    }

    private void validateAndProcessSubmit() {

    }

}