package com.example.tutornite.activities;

import static com.example.tutornite.utils.FireStoreConstants.UPCOMING_SESSIONS;
import static com.example.tutornite.utils.FireStoreConstants.USERS;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tutornite.R;
import com.example.tutornite.models.UpcomingSessionModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProgressActivity extends BaseActivity {

    ImageView back_img;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    PieChart pie_chart_view;
    TextView txt_attended_sessions, txt_upcoming_sessions, txt_total_sessions, txt_unattended_sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setEvents();
        getAnalytics();
    }

    private void getAnalytics() {
        showProgressDialog();
        db.collection(USERS)
                .document(Objects.requireNonNull(mAuth.getUid()))
                .collection(UPCOMING_SESSIONS)
                .get()
                .addOnCompleteListener(task -> {
                    hideProgressDialog();

                    if (task.isSuccessful() && task.getResult() != null) {
                        ArrayList<UpcomingSessionModel> upcomingSes = new ArrayList<>();
                        int upcomingSessions = 0, attendedSessions = 0, unAttendedSessions = 0;

                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            UpcomingSessionModel upcomingSessionModel = documentSnapshot.toObject(UpcomingSessionModel.class);
                            upcomingSessionModel.setDocumentID(documentSnapshot.getId());
                            upcomingSes.add(upcomingSessionModel);

                            if (isSessionPassed(upcomingSessionModel.getSessionDateTime())) {
                                if (upcomingSessionModel.isAttended()) {
                                    attendedSessions++;
                                } else {
                                    unAttendedSessions++;
                                }
                            } else {
                                upcomingSessions++;
                            }
                        }

                        showPieChart(attendedSessions, upcomingSessions, unAttendedSessions);
                    }
                });
    }

    private void showPieChart(int attended, int upcoming, int unAttended) {
        txt_total_sessions.setText(String.valueOf(attended + unAttended + upcoming));
        txt_attended_sessions.setText(String.valueOf(attended));
        txt_upcoming_sessions.setText(String.valueOf(upcoming));
        txt_unattended_sessions.setText(String.valueOf(unAttended));

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        String label = "Sessions";

        Map<String, Integer> typeAmountMap = new HashMap<>();
        if (attended > 0) {
            typeAmountMap.put("Attended", attended);
        }
        if (unAttended > 0) {
            typeAmountMap.put("Non-attended", unAttended);
        }
        if (upcoming > 0) {
            typeAmountMap.put("Upcoming", upcoming);
        }

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#304567"));
        colors.add(Color.parseColor("#309967"));
        colors.add(Color.parseColor("#476567"));
        colors.add(Color.parseColor("#890567"));
        colors.add(Color.parseColor("#a35567"));
        colors.add(Color.parseColor("#ff5f67"));
        colors.add(Color.parseColor("#3ca567"));

        for (String type : typeAmountMap.keySet()) {
            pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);

        pie_chart_view.getDescription().setEnabled(false);
        pie_chart_view.setData(pieData);
        pie_chart_view.invalidate();
    }

    private void setEvents() {
        back_img.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private boolean isSessionPassed(Timestamp sessionDateTime) {
        Date currentDate = new Date();
        Date timestampDate = sessionDateTime.toDate();
        return timestampDate.before(currentDate);
    }

    private void initViews() {
        back_img = findViewById(R.id.back_img);
        pie_chart_view = findViewById(R.id.pie_chart_view);
        txt_attended_sessions = findViewById(R.id.txt_attended_sessions);
        txt_upcoming_sessions = findViewById(R.id.txt_upcoming_sessions);
        txt_total_sessions = findViewById(R.id.txt_total_sessions);
        txt_unattended_sessions = findViewById(R.id.txt_unattended_sessions);
    }
}