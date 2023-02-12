package com.example.tutornite.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutornite.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_email, edt_password, edt_name;
    Button btn_sign_up;
    TextView txt_sign_in;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        edt_name = findViewById(R.id.edt_name);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        txt_sign_in = findViewById(R.id.txt_sign_in);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_sign_up.setOnClickListener(view -> {
            validateAndProcessSignUp(edt_email.getText().toString(), edt_password.getText().toString(), edt_name.getText().toString());
        });

        txt_sign_in.setOnClickListener(view -> {
            onBackPressed();
            finish();
        });
    }

    private void validateAndProcessSignUp(String email, String password, String name) {
        if (TextUtils.isEmpty(name)) {
            edt_name.setError("Please enter your name");
            return;
        }
        if (!isValidEmail(email)) {
            edt_email.setError("Please enter a valid email address");
            return;
        }
        if (!isValidPassword(password)) {
            edt_password.setError("Please enter a valid password");
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        Intent i = new Intent(this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}