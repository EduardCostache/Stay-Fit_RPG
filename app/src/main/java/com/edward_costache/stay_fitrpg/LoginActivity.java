package com.edward_costache.stay_fitrpg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Edward Costache
 */
public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText editTxtEmail, editTxtPassword;
    private TextView txtSignup, txtForgotPassword, txtVersion;
    private androidx.constraintlayout.widget.ConstraintLayout layout;

    private CheckBox checkBoxRememberMe;
    private View window;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferencesAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initViews();
        mAuth = FirebaseAuth.getInstance();

        String intentEmail = getIntent().getStringExtra("email");
        String intentPassword = getIntent().getStringExtra("password");
        if(intentEmail != null)
        {
            editTxtEmail.setText(intentEmail);
            editTxtPassword.setText(intentPassword);
        }

        sharedPreferencesAccount = getSharedPreferences("account", MODE_PRIVATE);
        if(sharedPreferencesAccount.getBoolean("rememberMe", false))
        {
            String email = sharedPreferencesAccount.getString("email", "");
            String password = sharedPreferencesAccount.getString("password", "");
            login(email, password);
        }

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSignup();
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCredentialLogin();

                InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editTxtEmail.getWindowToken(), 0);
                mgr.hideSoftInputFromWindow(editTxtPassword.getWindowToken(), 0);
            }
        });

        checkBoxRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferencesAccount.edit();
                if(buttonView.isChecked())
                {
                    editor.putBoolean("rememberMe", true);
                }
                else
                {
                    editor.putBoolean("rememberMe", false);
                }
                editor.apply();
            }
        });
    }

    /**
     * A function for initializing the Views
     */
    private void initViews() {
        btnLogin = findViewById(R.id.loginBtnLogin);
        txtSignup = findViewById(R.id.loginTxtSignup);
        txtForgotPassword = findViewById(R.id.loginTxtForgot);
        txtVersion = findViewById(R.id.loginVersion);

        layout = findViewById(R.id.loginLayout);

        checkBoxRememberMe = findViewById(R.id.loginRememberCheckbox);

        editTxtEmail = findViewById(R.id.loginEditTxtEmail);
        editTxtPassword = findViewById(R.id.loginEditTxtPassword);

        window = findViewById(R.id.loginWindow);

        progressBar = findViewById(R.id.loginProgressBar);
    }

    /**
     * A function for checking all TextFields are correct, if they are call login()
     */
    private void checkCredentialLogin() {
        String email = editTxtEmail.getText().toString();
        String password = editTxtPassword.getText().toString();

        if (email.isEmpty()) {
            editTxtEmail.setError("Please prove a Email!");
            editTxtEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTxtEmail.setError("Pleas enter Valid Email!");
            editTxtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTxtPassword.setError("Please provide a Password!");
            editTxtPassword.requestFocus();
            return;
        }
        else if(password.length()<6)
        {
            editTxtPassword.setError("Password must be longer than 5 characters!");
            editTxtPassword.requestFocus();
            return;
        }

        login(email, password);

        if(checkBoxRememberMe.isChecked())
        {
            SharedPreferences.Editor editor = sharedPreferencesAccount.edit();
            editor.putString("email", email);
            editor.putString("password", password);
            editor.apply();
        }
    }

    /**
     * A function that logs the user in when provided with an email and password
     * @param email Required to log in
     * @param password Required to log in
     */
    private void login(String email, String password)
    {
        updateUI(true);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finishAffinity();
                } else {
                    updateUI(false);
                    Snackbar.make(LoginActivity.this, layout, "Email Or Password are Incorrect, Please try again!", 3000).setAction("Close", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }).show();
                }
            }
        });
    }

    /**
     * A function that navigates the user to the SignUpActivity
     */
    private void moveSignup() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * A function that updates the UI to a loading screen when enabled
     * @param disappear Determines whether the UI should be loading or not
     */
    private void updateUI(boolean disappear)
    {
        if(disappear) {
            btnLogin.setVisibility(View.GONE);
            checkBoxRememberMe.setVisibility(View.GONE);
            editTxtEmail.setVisibility(View.GONE);
            editTxtPassword.setVisibility(View.GONE);
            txtSignup.setVisibility(View.GONE);
            txtForgotPassword.setVisibility(View.GONE);
            txtVersion.setVisibility(View.GONE);

            window.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            btnLogin.setVisibility(View.VISIBLE);
            checkBoxRememberMe.setVisibility(View.VISIBLE);
            editTxtEmail.setVisibility(View.VISIBLE);
            editTxtPassword.setVisibility(View.VISIBLE);
            txtSignup.setVisibility(View.VISIBLE);
            txtForgotPassword.setVisibility(View.VISIBLE);
            txtVersion.setVisibility(View.VISIBLE);

            window.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}