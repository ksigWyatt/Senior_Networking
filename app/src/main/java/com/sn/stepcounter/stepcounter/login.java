package com.sn.stepcounter.stepcounter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.SubmitProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity  {
    TextView sign_up_btn;
    TextInputEditText email,password1;
    private FirebaseAuth mAuth;
    SubmitProcessButton btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_txtbox);
        password1 =findViewById(R.id.password_textbox);
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Sign_up.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });


        btnSignIn =  findViewById(R.id.login_btn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignIn.setProgress(0);
                signin(email.getText().toString(),password1.getText().toString());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
        }
    }

    public void signin(String emailaddress, String password) {
        if (emailaddress.isEmpty() || emailaddress == null) {
            email.setError("Email address required ");
            email.requestFocus();
        } else if (!emailaddress.contains("@")) {
            email.setError("Not a valid Email address  ");
            email.requestFocus();
        } else if (password.isEmpty()) {
            password1.setError("Password required");
            password1.requestFocus();
        }else{
            btnSignIn.setProgress(50);
            mAuth.signInWithEmailAndPassword(emailaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                btnSignIn.setProgress(100);
                                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(getApplicationContext(), task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();

                            }


                        }
                    });
        }
    }

}


