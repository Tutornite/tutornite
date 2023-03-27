package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.FROM_HOME_SCREEN;
import static com.example.tutornite.utils.Constants.FROM_ORGANISED_SESSION;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE_TUTOR;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tutornite.R;
import com.example.tutornite.adapters.SessionsAdapter;
import com.example.tutornite.adapters.TutorsAdapter;
import com.example.tutornite.models.SessionDetailsModel;
import com.example.tutornite.models.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TutorListActivity extends BaseActivity {

    ImageView back_img;
    TextView no_tutors_txt;
    RecyclerView recyclerUsers;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    TutorsAdapter tutorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setEvents();
        fetchTutors();
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        no_tutors_txt = findViewById(R.id.no_tutors_txt);
        recyclerUsers = findViewById(R.id.recycler_tutors);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchTutors() {
        showProgressDialog();
        db.collection(USERS)
                .whereEqualTo(USER_TYPE, USER_TYPE_TUTOR)
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<UserModel> usersList = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            UserModel userDetailsModel = documentSnapshot.toObject(UserModel.class);
                            userDetailsModel.setDocumentID(documentSnapshot.getId());
                            usersList.add(userDetailsModel);
                        }
                        setUpTutorList(usersList);
                    }
                    hideProgressDialog();
                });
    }

    private void setUpTutorList(ArrayList<UserModel> userDetailsModel) {
        if (userDetailsModel.isEmpty()) {
            no_tutors_txt.setVisibility(View.VISIBLE);
            recyclerUsers.setVisibility(View.GONE);
        } else {
            no_tutors_txt.setVisibility(View.GONE);
            recyclerUsers.setVisibility(View.VISIBLE);
        }

        tutorsAdapter = new TutorsAdapter(this, userDetailsModel, mAuth.getUid(), FROM_HOME_SCREEN);
        recyclerUsers.setAdapter(tutorsAdapter);
    }

}