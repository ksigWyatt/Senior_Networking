package com.sn.stepcounter.stepcounter;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.SubmitProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class login extends AppCompatActivity {
    TextView sign_up_btn;
    TextInputEditText email, password1;
    private FirebaseAuth mAuth;
    SubmitProcessButton btnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //init Firebase auth
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_txtbox);
        password1 = findViewById(R.id.password_textbox);
        password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // make log in button viewable for the user
                    ScrollView sv = (ScrollView) findViewById(R.id.sv);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }

        });

        //init sign-up btn
        sign_up_btn = findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Sign_up.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        //init sign-in-bttn and click listener
        btnSignIn = findViewById(R.id.login_btn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSignIn.setProgress(0);
                signin(email.getText().toString(), password1.getText().toString());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // if user signed in before
        if (user != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("UsersStepData/" + user.getUid());
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Date now = new Date();
                    SimpleDateFormat simpleDateformat = new SimpleDateFormat("MM-dd-yyyy");
                    SimpleDateFormat simpleDateformat2 = new SimpleDateFormat("E");
                    // / the day of the week abbreviated
                    String date = simpleDateformat.format(now);
                    String day = simpleDateformat2.format(now);
                    // initial the database for the new user
                    if (dataSnapshot.getChildrenCount() == 0 || dataSnapshot == null) {
                        HashMap<String, StepData> map = initUserData();

                        for (String key : map.keySet()) {
                            myRef.child("WeeklyStepData").child(key).setValue(map.get(key));
                        }
                    } else {
                        if (dataSnapshot.child("WeeklyStepData").child(day).child("date").
                                getValue(String.class) != null) {
                            if (!dataSnapshot.child("WeeklyStepData").child(day).child("date").
                                    getValue(String.class).equals(date)) {
                                for (DataSnapshot childSnap : dataSnapshot.child("WeeklyStepData").
                                        getChildren()) {
                                    myRef.child("WeeklyStepData").child(childSnap.getKey()).
                                            child("stepsCount").setValue((float) 0);
                                }
                            }
                        }
                    }
                    myRef.child("WeeklyStepData").child(day).child("date").setValue(date);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }
    // Firebase auth sign in method
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
        } else {
            btnSignIn.setProgress(50);
            mAuth.signInWithEmailAndPassword(emailaddress, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser currentUser = FirebaseAuth.getInstance().
                                        getCurrentUser();
                                btnSignIn.setProgress(100);
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                                overridePendingTransition(R.anim.slide_from_right,
                                        R.anim.slide_to_left);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("UsersStepData/"
                                        + currentUser.getUid());
                                // initial the database for the new user
                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Date now = new Date();
                                        SimpleDateFormat simpleDateformat =
                                                new SimpleDateFormat("MM-dd-yyyy");
                                        SimpleDateFormat simpleDateformat2 =
                                                new SimpleDateFormat("E");
                                        // the day of the week abbreviated
                                        String date = simpleDateformat.format(now);
                                        String day = simpleDateformat2.format(now);
                                        // init new user
                                        if (dataSnapshot.getChildrenCount() == 0 ||
                                                dataSnapshot == null) {
                                            HashMap<String, StepData> map = initUserData();

                                            for (String key : map.keySet()) {
                                                myRef.child("WeeklyStepData").child(key).
                                                        setValue(map.get(key));
                                            }
                                        } else {
                                            // reset data after one week
                                            if (dataSnapshot.child("WeeklyStepData").child(day).
                                                    child("date").getValue(String.class) != null) {
                                                if (!dataSnapshot.child("WeeklyStepData").
                                                        child(day).child("date").
                                                        getValue(String.class).equals(date)) {
                                                    for (DataSnapshot childSnap : dataSnapshot.
                                                            child("WeeklyStepData").getChildren()) {
                                                        myRef.child("WeeklyStepData").
                                                                child(childSnap.getKey()).
                                                                child("stepsCount").
                                                                setValue((float) 0);

                                                    }

                                                }
                                            }

                                        }
                                        myRef.child("WeeklyStepData").child(day).child("date").
                                                setValue(date);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.

                                Toast.makeText(getApplicationContext(), task.getException().
                                                getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                btnSignIn.setProgress(0);

                            }


                        }
                    });
        }
    }


    public HashMap<String, StepData> initUserData() {
        HashMap<String, StepData> map = new HashMap<>();
        StepData data0 = new StepData(0, 0, null);
        StepData data1 = new StepData(1, 0, null);
        StepData data2 = new StepData(2, 0, null);
        StepData data3 = new StepData(3, 0, null);
        StepData data4 = new StepData(4, 0, null);
        StepData data5 = new StepData(5, 0, null);
        StepData data6 = new StepData(6, 0, null);

        map.put("Mon", data0);
        map.put("Tue", data1);
        map.put("Wed", data2);
        map.put("Thu", data3);
        map.put("Fri", data4);
        map.put("Sat", data5);
        map.put("Sun", data6);

        return map;


    }



}


