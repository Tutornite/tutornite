package com.example.tutornite.activities;

import static com.example.tutornite.utils.Constants.FROM_HOME_SCREEN;
import static com.example.tutornite.utils.Constants.HOME_SCREEN;
import static com.example.tutornite.utils.Constants.remoteSessionsCategoryList;
import static com.example.tutornite.utils.Constants.remoteUpcomingSessions;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.SESSIONS_CATEGORIES;
import static com.example.tutornite.utils.FireStoreConstants.UPCOMING_SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;
import static com.example.tutornite.utils.FireStoreConstants.USER_TYPE_LEARNER;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tutornite.R;
import com.example.tutornite.adapters.FilterAdapter;
import com.example.tutornite.adapters.SessionsAdapter;
import com.example.tutornite.interfaces.SessionEventsInterface;
import com.example.tutornite.models.SessionCategoryModel;
import com.example.tutornite.models.SessionDetailsModel;
import com.example.tutornite.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends BaseActivity implements View.OnClickListener, SessionEventsInterface {

    ImageView drawer_btn;
    DrawerLayout drawer_layout;
    LinearLayout share_nav, rating_nav, logout_nav, profile_nav, my_sessions_nav, organised_sessions_nav;
    RelativeLayout rel_filter;
    CircleImageView user_image;
    FloatingActionButton create_session_fab;
    EditText edt_search;
    TextView no_recipes_txt, user_name, user_email;
    TextWatcher textWatcher;
    ArrayList<String> selectedCategoriesFilter = new ArrayList<>();
    FirebaseUser currentUser;
    Dialog filterDialog;

    RecyclerView recyclerSessions;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    ArrayList<SessionDetailsModel> sessionsListDetailsModel = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setClicks();
        fetchSessionCategories();
        setUpUserDetails();
    }

    private void fetchSessionCategories() {
        db.collection(SESSIONS_CATEGORIES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<SessionCategoryModel> recipesCategories = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            SessionCategoryModel sessionCategoryModel = documentSnapshot.toObject(SessionCategoryModel.class);
                            sessionCategoryModel.setDocumentID(documentSnapshot.getId());
                            recipesCategories.add(sessionCategoryModel);
                        }

                        remoteSessionsCategoryList.clear();
                        remoteSessionsCategoryList.addAll(recipesCategories);
                    }
                });
    }

    private void fetchUpcomingSessions() {
        showProgressDialog();
        db.collection(USERS)
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection(UPCOMING_SESSIONS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<String> upcomingSes = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            upcomingSes.add(documentSnapshot.getId());
                        }

                        remoteUpcomingSessions.clear();
                        remoteUpcomingSessions.addAll(upcomingSes);
                    }
                    fetchSessions();
                });
    }

    private void fetchSessions() {
        db.collection(SESSIONS)
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<SessionDetailsModel> sessions = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            SessionDetailsModel sessionDetailsModel = documentSnapshot.toObject(SessionDetailsModel.class);
                            sessionDetailsModel.setDocumentID(documentSnapshot.getId());
                            sessions.add(sessionDetailsModel);
                        }
                        sessionsListDetailsModel.clear();
                        sessionsListDetailsModel.addAll(sessions);
                        setUpSessionsList(sessionsListDetailsModel);
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

        recyclerSessions.setAdapter(new SessionsAdapter(this, sessionsListDetailsModel, this, currentUser.getUid(), FROM_HOME_SCREEN));
    }

    private void setUpUserDetails() {
        String fullName = "";

        if (!TextUtils.isEmpty(Constants.currentUserModel.getFirstName())) {
            fullName += Constants.currentUserModel.getFirstName();
        }
        if (!TextUtils.isEmpty(Constants.currentUserModel.getLastName())) {
            fullName += " " + Constants.currentUserModel.getLastName();
        }

        if (!TextUtils.isEmpty(fullName)) {
            user_name.setText(fullName);
        }

        if (!TextUtils.isEmpty(Constants.currentUserModel.getEmail())) {
            user_email.setText(Constants.currentUserModel.getEmail());
        }

        if (!TextUtils.isEmpty(Constants.currentUserModel.getUserImage())) {
            Glide.with(this)
                    .load(Base64.decode(Constants.currentUserModel.getUserImage(), Base64.DEFAULT))
                    .into(user_image);
        }

        if (Constants.currentUserModel.getType().equalsIgnoreCase(USER_TYPE_LEARNER)) {
            create_session_fab.setVisibility(View.GONE);
            organised_sessions_nav.setVisibility(View.GONE);
        }
    }

    private void setClicks() {
        drawer_btn.setOnClickListener(this);
        share_nav.setOnClickListener(this);
        rating_nav.setOnClickListener(this);
        logout_nav.setOnClickListener(this);
        profile_nav.setOnClickListener(this);
        my_sessions_nav.setOnClickListener(this);
        organised_sessions_nav.setOnClickListener(this);
        rel_filter.setOnClickListener(this);
        create_session_fab.setOnClickListener(this);

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                hideKeyboard(HomeActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                selectedCategoriesFilter.clear();
                searchSessions(editable.toString().toLowerCase());
            }
        };

        edt_search.addTextChangedListener(textWatcher);
    }

    private void searchSessions(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            setUpSessionsList(sessionsListDetailsModel);
            return;
        }

        ArrayList<SessionDetailsModel> searchList = new ArrayList<>();

        for (SessionDetailsModel sessionDetailsModel : sessionsListDetailsModel) {
            if (sessionDetailsModel.getSessionTitle().toLowerCase().contains(searchText) ||
                    sessionDetailsModel.getSessionDetails().toLowerCase().contains(searchText)) {
                searchList.add(sessionDetailsModel);
            }
        }

        setUpSessionsList(searchList);
    }

    private void initViews() {
        drawer_btn = findViewById(R.id.drawer_btn);
        drawer_layout = findViewById(R.id.drawer_layout);
        share_nav = findViewById(R.id.share_nav);
        rating_nav = findViewById(R.id.rating_nav);
        logout_nav = findViewById(R.id.logout_nav);
        profile_nav = findViewById(R.id.profile_nav);
        my_sessions_nav = findViewById(R.id.my_sessions_nav);
        organised_sessions_nav = findViewById(R.id.organised_sessions_nav);
        rel_filter = findViewById(R.id.rel_filter);
        no_recipes_txt = findViewById(R.id.no_recipes_txt);
        create_session_fab = findViewById(R.id.create_session_fab);
        edt_search = findViewById(R.id.edt_search);
        user_image = findViewById(R.id.user_image);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        recyclerSessions = findViewById(R.id.recycler_events);
        recyclerSessions.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_btn:
                if (drawer_layout.isDrawerOpen(Gravity.LEFT)) {
                    drawer_layout.closeDrawer(Gravity.LEFT);
                } else {
                    drawer_layout.openDrawer(Gravity.LEFT);
                }
                break;
            case R.id.share_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                shareApp();
                break;
            case R.id.rating_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                openWebPage(Constants.rating_app_url);
                break;
            case R.id.logout_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to Logout?");

                builder.setPositiveButton("YES", (dialog, which) -> {
                    dialog.dismiss();
                    mAuth.signOut();
                    Intent i = new Intent(HomeActivity.this, SplashActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                });

                builder.setNegativeButton("NO", (dialog, which) -> {
                    dialog.dismiss();
                });

                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.profile_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, ProfileDetailsActivity.class));
                break;
            case R.id.my_sessions_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(this, MySessionActivity.class);
                Gson gson = new Gson();
                intent.putExtra(HOME_SCREEN, gson.toJson(sessionsListDetailsModel));
                startActivity(intent);
                break;
            case R.id.organised_sessions_nav:
                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, OrganisedSessionActivity.class));
                break;
            case R.id.rel_filter:
                selectFilterDialog();
                break;
            case R.id.create_session_fab:
                drawer_layout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(this, SessionCreateActivity.class));
                break;
        }
    }

    public void selectFilterDialog() {
        if (filterDialog != null) {
            if (filterDialog.isShowing()) {
                return;
            }
        }

        View view = LayoutInflater.from(this).inflate(R.layout.apply_filter_lay, null);
        filterDialog = new Dialog(this);
        filterDialog.setCancelable(false);
        filterDialog.setContentView(view);

        TextView txt_apply;
        ImageView close_btn;
        txt_apply = filterDialog.findViewById(R.id.txt_apply);
        close_btn = filterDialog.findViewById(R.id.close_btn);

        FilterAdapter filterAdapter = new FilterAdapter(this, remoteSessionsCategoryList, selectedCategoriesFilter);

        txt_apply.setOnClickListener(view1 -> {
            edt_search.removeTextChangedListener(textWatcher);
            edt_search.setText("");
            edt_search.addTextChangedListener(textWatcher);

            filterDialog.dismiss();
            selectedCategoriesFilter = filterAdapter.selectedCategories;
            if (!selectedCategoriesFilter.isEmpty()) {
                filterRecipes(selectedCategoriesFilter);
            } else {
                setUpSessionsList(sessionsListDetailsModel);
            }
        });
        close_btn.setOnClickListener(view12 -> filterDialog.dismiss());

        RecyclerView filter_items_recycler;
        filter_items_recycler = filterDialog.findViewById(R.id.filter_items_recycler);
        filter_items_recycler.setLayoutManager(new LinearLayoutManager(this));
        filter_items_recycler.setAdapter(filterAdapter);

        filterDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        filterDialog.show();
    }

    private void filterRecipes(ArrayList<String> selectedCategoriesFilter) {
        ArrayList<SessionDetailsModel> searchList = new ArrayList<>();
        for (SessionDetailsModel sessionDetailsModel : sessionsListDetailsModel) {
            if (selectedCategoriesFilter.contains(sessionDetailsModel.getCategoryID())) {
                searchList.add(sessionDetailsModel);
            }
        }
        setUpSessionsList(searchList);
    }

    public void shareApp() {
        String shareBody = "Download our app using this link " + Constants.share_app_url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Vie"));
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchUpcomingSessions();
    }
}