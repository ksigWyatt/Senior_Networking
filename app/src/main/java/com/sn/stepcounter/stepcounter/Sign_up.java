package com.sn.stepcounter.stepcounter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.SubmitProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_up extends AppCompatActivity {
   android.support.v7.widget.Toolbar mToolbar;
   TextInputEditText email, password1,password2;
    private FirebaseAuth mAuth;
    SubmitProcessButton btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mToolbar = findViewById(R.id.toolbar_signup);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_sign_up);
        password1 = findViewById(R.id.password1_sign_up);
        password2 = findViewById(R.id.password2_sign_up);
        btnSignIn =  findViewById(R.id.sign_up_btn_2);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignIn.setProgress(0);
                signup(email.getText().toString(),password1.getText().toString());
            }
        });
    }


    public void signup(String emailaddress,String password){
        if(emailaddress.isEmpty()|| emailaddress==null){
            email.setError("Email address required ");
            email.requestFocus();
        }else  if(!emailaddress.contains("@")){
            email.setError("Not a valid Email address  ");
            email.requestFocus();
        }else  if(!password.equals(password2.getText().toString() ) ||password.isEmpty()){
            password2.setError("Password not match");
            password2.requestFocus();
        }else{
            btnSignIn.setProgress(50);
            mAuth.createUserWithEmailAndPassword(emailaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                btnSignIn.setProgress(100);
                                Intent i = new Intent(getApplicationContext(),login.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Sign_up.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                btnSignIn.setProgress(-1);
                            }


                        }
                    });
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}
