package com.example.tutornite.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tutornite.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_email;
    Button btn_reset_pass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        edt_email = findViewById(R.id.edt_email);
        btn_reset_pass = findViewById(R.id.btn_reset_pass);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        btn_reset_pass.setOnClickListener(view -> {
            validateAndProcessResetPass(edt_email.getText().toString());
        });
    }

    private void validateAndProcessResetPass(String email) {
        if (!isValidEmail(email)) {
            edt_email.setError("Please enter a valid email address");
            return;
        }

        showProgressDialog();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            hideProgressDialog();
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this, "Password reset request was sent successfully. Please check your email to reset your password.",
                        Toast.LENGTH_LONG).show();
                onBackPressed();
                finish();
            } else {
                Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}