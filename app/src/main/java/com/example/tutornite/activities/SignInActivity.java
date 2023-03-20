package com.example.tutornite.activities;

import static com.example.tutornite.utils.FireStoreConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tutornite.R;
import com.example.tutornite.models.UserModel;
import com.example.tutornite.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends BaseActivity {

    ImageView back_img;
    EditText edt_email, edt_password;
    Button btn_sign_in;
    TextView txt_forgot_password, txt_signup;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        btn_sign_in = findViewById(R.id.btn_sign_in);
        txt_forgot_password = findViewById(R.id.txt_forgot_password);
        txt_signup = findViewById(R.id.txt_signup);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });

        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edt_email.setError(null);
            }
        });

        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edt_email.setError(null);
            }
        });

        btn_sign_in.setOnClickListener(view -> {
            validateAndProcessSignIn(edt_email.getText().toString(), edt_password.getText().toString());
        });

        txt_forgot_password.setOnClickListener(view -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        txt_signup.setOnClickListener(view -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void validateAndProcessSignIn(String email, String password) {
        if (!isValidEmail(email)) {
            edt_email.setError("Please enter a valid email address");
            return;
        }
        if (!isValidPassword(password)) {
            edt_password.setError("Please enter a valid password");
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    hideProgressDialog();
                    if (task.isSuccessful()) {
                        db.collection(USERS).document(mAuth.getUid())
                                .get().addOnCompleteListener(taskUser -> {
                                    Intent i = new Intent(this, HomeActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    if (taskUser.isSuccessful()) {
                                        DocumentSnapshot document = taskUser.getResult();
                                        if (document.exists()) {
                                            Constants.currentUserModel = document.toObject(UserModel.class);
                                        }
                                    }
                                    startActivity(i);
                                });
                    } else {
                        Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }
}