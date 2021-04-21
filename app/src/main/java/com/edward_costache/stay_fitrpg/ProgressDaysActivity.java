package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ProgressDaysActivity extends AppCompatActivity {

    private RecyclerView daysRecView;
    private ArrayList<ProgressDay> days;
    private ArrayList<String> names;
    private DayRecViewAdapter adapter;
    private String week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_days);
        TextView weekName = findViewById(R.id.progressDaysWeekName);
        daysRecView = findViewById(R.id.progressDaysRecView);

        days = new ArrayList<>();
        names = new ArrayList<>();
        daysRecView.setLayoutManager(new LinearLayoutManager(ProgressDaysActivity.this));
        adapter = new DayRecViewAdapter();
        Calendar calendar = new GregorianCalendar();
        week = Integer.toString(calendar.get(Calendar.WEEK_OF_YEAR));
        displayDays();
        weekName.setText(getIntent().getStringExtra("weekName"));

    }

    /**
     * A function for displaying all of the recorded weeks since the user has started exercising using the application.
     * The function uses a ValueEventListener to listen to the Firebase Database and fetch all of the weeks for that user, and store
     * that information in a ProgressWeek object, which is then added to a list.
     *
     * NOTE: When a ValueEventListener is constructed and added to a FirebaseDatabase reference, the code within onDataChanged() will be triggered when called once.
     * With a ValueEventListener, the code will also trigger reference the data in the reference changes.
     * A listenerForSingleValueEvent is only triggered when added to a reference.
     */
    private void displayDays()
    {
        FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress").child(week).child("days").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                days.clear();
                if (snapshot.getChildrenCount() != 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ProgressDay day = ds.getValue(ProgressDay.class);
                        days.add(day);
                        //since we have added i to the name of the day so that it would be numerically sorted in the DB,
                        //now the removal of i is done using a substring of the key.
                        String name = ds.getKey().substring(1);
                        names.add(name);
                    }
                }
                refreshRooms();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter.setDays(names, days);
        daysRecView.setAdapter(adapter);
        daysRecView.setLayoutManager(new LinearLayoutManager(ProgressDaysActivity.this));
    }

    private void refreshRooms() {
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}