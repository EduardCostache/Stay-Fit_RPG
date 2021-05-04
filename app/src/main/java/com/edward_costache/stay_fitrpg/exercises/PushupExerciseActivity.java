//the following code is generated by Android Studio
package com.edward_costache.stay_fitrpg.exercises;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;
import com.edward_costache.stay_fitrpg.User;
import com.edward_costache.stay_fitrpg.util.Proximiter;
import com.edward_costache.stay_fitrpg.util.SoundLibrary;
import com.edward_costache.stay_fitrpg.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
//the code above was Generated by android Studio

/**
 * Created by Edward Costache
 */
public class PushupExerciseActivity extends AppCompatActivity {

    public static final String TAG = "PushupExercise";
    private LinearLayout layoutRound, layoutBreak;
    private TextView txtTitle;
    private boolean isRound = true;

    private DatabaseReference reference, weekRef;
    private String userID;
    private androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    private User userProfile;

    // Round
    private TextView txtPushupCount;

    private ArrayList<Integer> rounds;
    private int round = 0;
    private int maxRounds, goal, currentPushups, userStrength, userHealth, overallPushups;
    private Proximiter proximiter;
    private boolean readyForPushup = true;

    // Break
    private CountDownTimer breakTimer;
    private long startMilliseconds;
    private TextView txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6, txtTime;
    private final int BREAK_TIME = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_exercise);

        rounds = getIntent().getIntegerArrayListExtra("rounds");
        maxRounds = rounds.size();
        startMilliseconds = System.currentTimeMillis();
        Log.i("ARRAY AFTER INTENT: ", rounds.toString());
        initViews();

        layoutRound.setVisibility(View.VISIBLE);
        layoutBreak.setVisibility(View.GONE);
        initListeners();
        goal = rounds.get(round);
        updateTextView();
        txtTitle.setText(String.format("ROUND: %d", round + 1));


        if (maxRounds == 4) {
            txtRound4.setVisibility(View.VISIBLE);
        } else if (maxRounds == 5) {
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setVisibility(View.VISIBLE);
        } else if (maxRounds == 6) {
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setVisibility(View.VISIBLE);
            txtRound6.setVisibility(View.VISIBLE);
        }
        setUpUser();
        getUserCurrentStats();

        breakTimer = new CountDownTimer(BREAK_TIME * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                txtTime.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
            }

            @Override
            public void onFinish() {
                switchLayout();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        proximiter.registerListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        proximiter.un_registerListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundLibrary.stopSound();
        proximiter.un_registerListener();
    }

    @Override
    public void onBackPressed() {
        displayClosingAlertBox();
    }

    private void displayClosingAlertBox() {
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);
        new AlertDialog.Builder(PushupExerciseActivity.this, R.style.MyDialogTheme)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exiting the Exercise")
                .setMessage(String.format("Quitting yields NO rewards! \nTotal Time: %02dm and %02ds\nTotal pushups: %d", seconds / 60, seconds % 60, overallPushups))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("ON STOP: ", "YES");
                        proximiter.un_registerListener();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * A function for initializing all Views
     */
    private void initViews() {
        layoutRound = findViewById(R.id.pushupExerciseRoundLayout);
        layoutBreak = findViewById(R.id.pushupExerciseBreakLayout);
        mainLayout = findViewById(R.id.pushupExerciseMainLayout);

        txtPushupCount = findViewById(R.id.pushupExerciseTxtPushupCount);
        txtTitle = findViewById(R.id.pushupExerciseTxtTitle);
        txtRound1 = findViewById(R.id.pushupExerciseTxtRound1);
        txtRound2 = findViewById(R.id.pushupExerciseTxtRound2);
        txtRound3 = findViewById(R.id.pushupExerciseTxtRound3);
        txtRound4 = findViewById(R.id.pushupExerciseTxtRound4);
        txtRound5 = findViewById(R.id.pushupExerciseTxtRound5);
        txtRound6 = findViewById(R.id.pushupExerciseTxtRound6);
        txtTime = findViewById(R.id.pushupExerciseTxtTime);

        proximiter = new Proximiter(PushupExerciseActivity.this);
    }

    /**
     * A function for initializing all listeners
     */
    private void initListeners() {
        proximiter.setListener(new Proximiter.Listener() {
            @Override
            public void onDistance(float cm) {
                // When user's face gets close to sensor, count a pushup
                if (cm < 8.0f && readyForPushup) {
                    readyForPushup = false;         //a boolean is used to make sure a pushup is only counted again after boolean is set to true
                    SoundLibrary.playSound(PushupExerciseActivity.this, R.raw.ding);
                    currentPushups++;
                    overallPushups++;
                    if (currentPushups == goal) {   //user completed round
                        round++;
                        if (round == maxRounds) {   //user completed exercise
                            endOfExercise();
                        } else {
                            currentPushups = 0;
                            goal = rounds.get(round);
                            switchLayout();
                        }

                    } else {
                        updateTextView();
                    }
                    // After user lifted his face, enable pushups
                } else {
                    readyForPushup = true;
                }
            }
        });

/*
        txtPushupCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundLibrary.playSound(PushupExerciseActivity.this, R.raw.ding);
                currentPushups++;
                overallPushups++;
                if (currentPushups == goal) {   //user completed round
                    round++;
                    if (round == maxRounds) {   //user completed exercise
                        endOfExercise();
                    } else {
                        currentPushups = 0;
                        goal = rounds.get(round);
                        switchLayout();
                    }

                } else {
                    updateTextView();
                }
            }
        });

 */
    }

    private void updateTextView() {
        txtPushupCount.setText(String.format("%02d / %02d", currentPushups, goal));
    }

    /**
     * A function for switching the layout from round to break
     */
    @SuppressLint("ResourceAsColor")
    private void switchLayout() {
        //instead of creating a new activity for break time. I decided to create a new layout for break, make it visible and make the round layout GONE
        //because i am making the layout gone, all children within the layout will adopt gone as well.
        if (isRound) {
            // Change to break
            readyForPushup = false;
            proximiter.un_registerListener();
            layoutRound.setVisibility(View.GONE);
            layoutBreak.setVisibility(View.VISIBLE);

            txtTitle.setText("BREAK");

            txtRound1.setText(String.format("ROUND 1: %02d", rounds.get(0)));
            txtRound1.setTextColor(Color.GREEN);
            txtRound2.setText(String.format("ROUND 2: %02d", rounds.get(1)));
            if (round == 2) {
                txtRound2.setTextColor(Color.GREEN);
            }
            txtRound3.setText(String.format("ROUND 3: %02d", rounds.get(2)));
            if (round == 3) {
                txtRound3.setTextColor(Color.GREEN);
            }

            try {
                txtRound4.setText(String.format("ROUND 3: %02d", rounds.get(3)));
                if (round == 4) {
                    txtRound4.setTextColor(Color.GREEN);
                }
                txtRound5.setText(String.format("ROUND 3: %02d", rounds.get(4)));
                if (round == 5) {
                    txtRound5.setTextColor(Color.GREEN);
                }
                txtRound6.setText(String.format("ROUND 3: %02d", rounds.get(5)));

            } catch (Exception e) {
                Log.i("PUSHUP EXERCISE: ", "NOT ENOUGH ROUNDS");
            }
            breakTimer.start();
            isRound = false;
        } else {
            // Change to round
            readyForPushup = true;
            proximiter.registerListener();
            layoutRound.setVisibility(View.VISIBLE);
            layoutBreak.setVisibility(View.GONE);

            txtTitle.setText(String.format("ROUND: %d", round + 1));
            updateTextView();
            breakTimer.cancel();
            isRound = true;
        }
    }

    /**
     * A function for obtaining the attributes from the user
     * Using a ListenerForSingleValueEvent because it only needs to be fetched once
     */
    private void getUserCurrentStats() {
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null)             //making sure user is not going to be null
                {
                    userProfile = snapshot.getValue(User.class);
                    userStrength = userProfile.getStrength();
                    userHealth = userProfile.getHealth();
                }
                else
                {
                    Log.i(TAG, "USER IS NULL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * A function for applying the user's rewards to their database and showing the user their exercise statistics
     */
    private void endOfExercise() {
        proximiter.un_registerListener();
        readyForPushup = false;
        reference.child(userID).child("strength").setValue(userStrength + getIntent().getIntExtra("strength", 0));      //reward values are given through the intent in the previous activity
        reference.child(userID).child("health").setValue(userHealth + getIntent().getIntExtra("health", 0));

        weekRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("progress");

        //this chunk of code gets all the children of that week (days) and removes the number in front of it in order to find today's day, then adds the progress to that day.
        //NOTE: the reason i am looking for day names without the number in the front is because Util.getCurrentWeekOfYear() will return a day such as "Thu, 29-04"
        //however in the database it may be stored as "1Thu, 29-04" (this is done so that days are stored in order in the DB)
        //so essentially i am removing that "1" so that i can get only the day name, and if that name matches today store the value in that day (child)
        weekRef.child(Util.getCurrentWeekOfYear()).child("days").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                {
                    for(DataSnapshot daySnapshot : snapshot.getChildren())
                    {
                        String dayName = daySnapshot.getKey().substring(1);
                        if(dayName.equals(Util.getTodayAsStringFormat()))
                        {
                            Log.d(TAG, "onDataChange: WEEK NUMBER " + Util.getCurrentWeekOfYear());
                            Log.d(TAG, "onDataChange: DAY " + daySnapshot.getKey());
                            weekRef.child(Util.getCurrentWeekOfYear()).child("days").child(daySnapshot.getKey()).child("pushups").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        //changing the pushup value for the current day in the database
                                        weekRef.child(Util.getCurrentWeekOfYear()).child("days").child(daySnapshot.getKey()).child("pushups").setValue(snapshot.getValue(Integer.class) + overallPushups);
                                    }
                                    else
                                    {
                                        Log.d(TAG, "onDataChange: PUSHUP PROGRESS NOT FOUND");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // currentTimeInMillis() returns the milliseconds for Epoch time, just like the Util.java class
        // Here i am subtracting the milliseconds at the start of the Activity from the milliseconds recorded at the end of the Activity
        // in order to get the amount of milliseconds the Activity has been running, then convert that to seconds
        int seconds = (int) ((System.currentTimeMillis() - startMilliseconds) / 1000);

        new AlertDialog.Builder(PushupExerciseActivity.this)
                .setTitle("Exercise Finished, Well Done!")
                .setMessage(String.format("Total Time: %02dm and %02ds\nTotal pushups: %d\nStrength +%02d\t\tHealth +%02d", seconds / 60, seconds % 60, overallPushups, getIntent().getIntExtra("strength", 0), getIntent().getIntExtra("health", 0)))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * A function for assigning the userID to the userID variable
     */
    private void setUpUser() {
        reference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userID = user.getUid();
        } else {
            Snackbar.make(PushupExerciseActivity.this, mainLayout, "Something went wrong, logout and login again!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            }).show();
        }
    }
}