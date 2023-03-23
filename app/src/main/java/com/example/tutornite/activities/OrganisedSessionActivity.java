package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.FROM_ORGANISED_SESSION;
import static com.example.tutornite.utils.FireStoreConstants.POSTED_BY_UID;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutornite.R;
import com.example.tutornite.adapters.SessionsAdapter;
import com.example.tutornite.interfaces.SessionEventsInterface;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrganisedSessionActivity extends BaseActivity implements SessionEventsInterface {

    ImageView back_img;
    RecyclerView recyclerSessions;
    TextView no_recipes_txt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    SessionsAdapter sessionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organised_session);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setEvents();
        fetchSessions();
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        no_recipes_txt = findViewById(R.id.no_recipes_txt);
        recyclerSessions = findViewById(R.id.recycler_events);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));
    }

    private void fetchSessions() {
        showProgressDialog();
        db.collection(SESSIONS)
                .whereEqualTo(POSTED_BY_UID, mAuth.getUid())
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<SessionDetailsModel> sessions = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            SessionDetailsModel sessionDetailsModel = documentSnapshot.toObject(SessionDetailsModel.class);
                            sessionDetailsModel.setDocumentID(documentSnapshot.getId());
                            sessions.add(sessionDetailsModel);
                        }
                        setUpSessionsList(sessions);
                    }
                    hideProgressDialog();
                });
    }

    private void setUpSessionsList(ArrayList<SessionDetailsModel> sessionsListDetailsModel) {
        if (sessionsListDetailsModel.isEmpty()) {
            no_recipes_txt.setVisibility(View.VISIBLE);
            recyclerSessions.setVisibility(View.GONE);
        } else {
            no_recipes_txt.setVisibility(View.GONE);
            recyclerSessions.setVisibility(View.VISIBLE);
        }

        sessionsAdapter = new SessionsAdapter(this, sessionsListDetailsModel, this, mAuth.getUid(), FROM_ORGANISED_SESSION);
        recyclerSessions.setAdapter(sessionsAdapter);
    }

    @Override
    public void joinSession(String documentID, String sessionTitle, int position, Timestamp sessionDateTime) {

    }

    @Override
    public void attendedSession(String sessionID, int position) {
        markSessionAttended(db, sessionID, mAuth.getUid(), () ->
                recyclerSessions.getAdapter().notifyItemChanged(position));
    }

    @Override
    public void cancelSession(String documentID, int position) {

    }

    @Override
    public void deleteSession(String documentID, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Delete Session");
        builder.setMessage("Are you sure you want to Delete Session?");

        builder.setPositiveButton("YES", (dialog, which) -> {
            dialog.dismiss();
            deleteSession(db, documentID, () -> {
                sessionsAdapter.removeItem(position);
            });
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}