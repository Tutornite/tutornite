package com.example.tutornite.activities;

import static com.example.tutornite.utils.FireStoreConstants.USERS;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.tutornite.R;
import com.example.tutornite.models.UserModel;
import com.example.tutornite.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(() -> {
            if (currentUser != null) {
                db.collection(USERS).document(currentUser.getUid())
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Constants.currentUserModel = document.toObject(UserModel.class);
                                    startActivity(new Intent(this, HomeActivity.class));
                                } else {
                                    startActivity(new Intent(this, ProfileCreateActivity.class));
                                }
                            } else {
                                startActivity(new Intent(this, ProfileCreateActivity.class));
                            }
                            finish();
                        });
            } else {
                startActivity(new Intent(this, SignInActivity.class));
                finish();
            }
        }, 2000);
    }
}