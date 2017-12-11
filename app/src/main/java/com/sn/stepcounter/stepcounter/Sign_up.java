package com.sn.stepcounter.stepcounter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dd.processbutton.iml.SubmitProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Sign_up extends AppCompatActivity {
    android.support.v7.widget.Toolbar mToolbar;
    TextInputEditText email, password1, password2;
    private FirebaseAuth mAuth;
    SubmitProcessButton btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mToolbar = findViewById(R.id.toolbar_signup);
        // support back arrow on the toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // signup page text fields
        email = findViewById(R.id.email_sign_up);
        password1 = findViewById(R.id.password1_sign_up);
        password2 = findViewById(R.id.password2_sign_up);
        //assign signup bttn
        btnSignUp = findViewById(R.id.sign_up_btn_2);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignUp.setProgress(0);
                //call sign up method
                signup(email.getText().toString(), password1.getText().toString());
            }
        });

        password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // make the signup button viewable to the user
                if (b) {
                    ScrollView sv = (ScrollView) findViewById(R.id.sv_sign_up);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }

        });
    }

    // sign up account for firebase auth with email and password method
    public void signup(String emailaddress, String password) {
        if (emailaddress.isEmpty() || emailaddress == null) {
            email.setError("Email address required ");
            email.requestFocus();
        } else if (!emailaddress.contains("@")) {
            email.setError("Not a valid Email address  ");
            email.requestFocus();
        } else if (!password.equals(password2.getText().toString()) || password.isEmpty()) {
            password2.setError("Password does not match");
            password2.requestFocus();
        } else {
            btnSignUp.setProgress(50);
            mAuth.createUserWithEmailAndPassword(emailaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI
                                btnSignUp.setProgress(100);
                                Intent i = new Intent(getApplicationContext(), login.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                            } else {
                                // If sign up fails, display a message to the user.
                                Toast.makeText(Sign_up.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                btnSignUp.setProgress(0);
                            }
                        }
                    });
        }
    }

    // the backarrow on the tool bar pressed
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        return true;
    }

    // android back nav button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
