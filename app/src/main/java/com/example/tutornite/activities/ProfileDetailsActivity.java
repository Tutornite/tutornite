package com.example.tutornite.activities;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tutornite.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileDetailsActivity extends BaseActivity {
    ImageView back_img;
    TextView txt_username, txt_dateofbirth,txt_college,txt_skillset,txt_paymentlink;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiledetails);
        initViews();
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        txt_username = findViewById(R.id.txt_username);
        txt_dateofbirth = findViewById(R.id.txt_dateofbirth);
        txt_college = findViewById(R.id.txt_college);
        txt_skillset = findViewById(R.id.txt_skillset);
        txt_paymentlink = findViewById(R.id.txt_paymentlink);

        back_img.setOnClickListener(view -> {
            onBackPressed();
        });


    }


}




