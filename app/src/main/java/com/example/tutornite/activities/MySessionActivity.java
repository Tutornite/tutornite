package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.FROM_HOME_SCREEN;
import static com.example.tutornite.utils.Constants.HOME_SCREEN;
import static com.example.tutornite.utils.Constants.remoteUpcomingSessions;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutornite.R;
import com.example.tutornite.adapters.SessionsAdapter;
import com.example.tutornite.interfaces.SessionEventsInterface;
import com.example.tutornite.models.SessionDetailsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class MySessionActivity extends BaseActivity implements SessionEventsInterface {

    ImageView back_img;
    RecyclerView recyclerSessions;
    TextView no_recipes_txt;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    SessionsAdapter sessionsAdapter;
    HashMap<String, SessionDetailsModel> sessionsHashmap = new HashMap<>();
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_session);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

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

        Gson gson = new Gson();
        ArrayList<SessionDetailsModel> sessionsList = gson.fromJson(getIntent().getStringExtra(HOME_SCREEN), new TypeToken<ArrayList<SessionDetailsModel>>() {
        }.getType());
        for (SessionDetailsModel sessionDetailsModel : sessionsList) {
            sessionsHashmap.put(sessionDetailsModel.getDocumentID(), sessionDetailsModel);
        }

        ArrayList<SessionDetailsModel> mySessionsList = new ArrayList<>();
        for (String sessionID : remoteUpcomingSessions) {
            if (sessionsHashmap.containsKey(sessionID)) {
                mySessionsList.add(sessionsHashmap.get(sessionID));
            }
        }

        setUpSessionsList(mySessionsList);

        hideProgressDialog();
    }

    private void setUpSessionsList(ArrayList<SessionDetailsModel> sessionsListDetailsModel) {
        if (sessionsListDetailsModel.isEmpty()) {
            no_recipes_txt.setVisibility(View.VISIBLE);
            recyclerSessions.setVisibility(View.GONE);
        } else {
            no_recipes_txt.setVisibility(View.GONE);
            recyclerSessions.setVisibility(View.VISIBLE);
        }

        sessionsAdapter = new SessionsAdapter(this, sessionsListDetailsModel, this, mAuth.getUid(), FROM_HOME_SCREEN);
        recyclerSessions.setAdapter(sessionsAdapter);
    }

    @Override
    public void joinSession(String sessionID, String sessionTitle, int position) {
        joinSessionBase(db, sessionID, currentUser.getUid(),
                currentUser.getEmail(), sessionTitle, () ->
                        recyclerSessions.getAdapter().notifyItemChanged(position));
    }

    @Override
    public void cancelSession(String sessionID, int position) {
        cancelJoinSessionBase(db, sessionID, currentUser.getUid(), () ->
                recyclerSessions.getAdapter().notifyItemChanged(position));
    }

    @Override
    public void deleteSession(String documentID, int position) {

    }
}