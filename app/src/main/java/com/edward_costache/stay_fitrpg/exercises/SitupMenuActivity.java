package com.edward_costache.stay_fitrpg.exercises;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.edward_costache.stay_fitrpg.R;

import java.util.ArrayList;

/**
 * Created by Edward Costache
 */
public class SitupMenuActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButtonMedium;
    private TextView txtStrength, txtHealth, txtRound1, txtRound2, txtRound3, txtRound4, txtRound5, txtRound6;
    private Button btnStart;

    private int maxReps;
    private int staminaGained;
    private int healthGained;
    private final ArrayList<Integer> rounds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_situp_menu);
        initViews();
        radioButtonMedium.setChecked(true);
        setMedium();
        updateViews();
        setOnCheckedListeners();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SitupMenuActivity.this, SitupExerciseActivity.class);
                intent.putExtra("stamina", staminaGained);
                intent.putExtra("health", healthGained);
                intent.putExtra("rounds", rounds);

                Log.i("ARRAY LIST: ", rounds.toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * A function that sets up actions when one of the radio buttons in pressed
     */
    private void setOnCheckedListeners()
    {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.situpRadioButtonVeryEasy:
                        setVeryEasy();
                        break;
                    case R.id.situpRadioButtonEasy:
                        setEasy();
                        break;
                    case R.id.situpRadioButtonMedium:
                        setMedium();
                        break;
                    case R.id.situpRadioButtonHard:
                        setHard();
                        break;

                }
                updateViews();
            }
        });
    }

    /**
     * A function for setting the difficulty of the Situp exercise to very easy
     */
    private void setVeryEasy()
    {
        staminaGained = 5;
        healthGained = 4;
        maxReps = 8;

        //Round 4
        txtRound4.setVisibility(View.GONE);
        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
    }

    /**
     * A function for setting the difficulty for the Situp exercise to easy
     */
    private void setEasy()
    {
        staminaGained = 6;
        healthGained = 5;
        maxReps = 10;

        //Round 5
        txtRound5.setVisibility(View.GONE);
        //Round 6
        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
    }

    /**
     * A function for setting the difficulty for the Situp exercise to medium
     */
    private void setMedium()
    {
        staminaGained = 7;
        healthGained = 6;
        maxReps = 12;

        txtRound6.setVisibility(View.GONE);

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
    }

    /**
     * A function for setting the difficulty of the Situp exercise to hard
     */
    private void setHard()
    {
        staminaGained = 9;
        healthGained = 8;
        maxReps = 16;

        rounds.clear();
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
        rounds.add((int)(maxReps*0.5));
        rounds.add((int)(maxReps*0.8));
        rounds.add(maxReps);
    }

    /**
     * A function for displaying the updated information when a difficulty is set
     */
    private void updateViews()
    {
        //Round 1
        txtRound1.setText(String.format("ROUND 1: %02d", rounds.get(0)));
        //Round 2
        txtRound2.setText(String.format("ROUND 2: %02d", rounds.get(1)));
        //Round 3
        txtRound3.setText(String.format("ROUND 3: %02d", rounds.get(2)));

        //try catch used to stop the code at the required rounds.
        //calling setText will produce an error if the TextView is set to Gone
        //i.e setMedium() sets the visibility of round 5 and 6 TextViews to Gone which will produce an error here
        //unless its a try catch, the code will only update for round 4 TextView and skip the rest as round 5 will give error
        //this trick avoids if statements for all TextViews to check if they are visible.
        try {
            txtRound4.setText(String.format("ROUND 4: %02d", rounds.get(3)));
            txtRound4.setVisibility(View.VISIBLE);
            txtRound5.setText(String.format("ROUND 5: %02d", rounds.get(4)));
            txtRound5.setVisibility(View.VISIBLE);
            txtRound6.setText(String.format("ROUND 6: %02d", rounds.get(5)));
            txtRound6.setVisibility(View.VISIBLE);
        }
        catch (Exception e)
        {
            Log.i("UPDATE VIEWS", ": STOPPED");
        }


        txtStrength.setText(String.format("STAMINA: +%02d", staminaGained));
        txtHealth.setText(String.format("HEALTH: +%02d", healthGained));
    }

    /**
     * A function for initializing the Views for the Situp exercise
     */
    private void initViews()
    {
        radioGroup = findViewById(R.id.situpDifficultyRadioGrp);
        radioButtonMedium = findViewById(R.id.situpRadioButtonMedium);

        txtStrength = findViewById(R.id.situpTxtStrength);
        txtHealth = findViewById(R.id.situpTxtHealth);
        txtRound1 = findViewById(R.id.situpTxtRound1);
        txtRound2 = findViewById(R.id.situpTxtRound2);
        txtRound3 = findViewById(R.id.situpTxtRound3);
        txtRound4 = findViewById(R.id.situpTxtRound4);
        txtRound5 = findViewById(R.id.situpTxtRound5);
        txtRound6 = findViewById(R.id.situpTxtRound6);

        btnStart = findViewById(R.id.situpBtnStart);
    }
}