/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Login Activity and validation functions
*/
package com.example.emrental;
//------------------ Android Imports --------------------

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    //----------------------- Variables & Objects -------------------
    public EditText emailIdl, passwordIdl;
    Button signInBtn;
    TextView signUptv;
    FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener mAuthL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);
        //-------------------- attaching firebase to the application ------------------
        FirebaseApp.initializeApp(this);
        fbAuth = FirebaseAuth.getInstance();
        //--------------------- attaching objects with the XML file
        emailIdl = (EditText) findViewById(R.id.editText12);
        passwordIdl = (EditText) findViewById(R.id.editText10);
        signInBtn = (Button) findViewById(R.id.button);
        signUptv = (TextView) findViewById(R.id.textView);
        //------------------------------ Sign In Button After Registration ---------------
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailIdl.getText().toString();
                String pssd = passwordIdl.getText().toString();
                //------------------ Email & Password inputs Validation ----------------
                if (email.isEmpty()) {
                    emailIdl.setError("Please enter your email");
                    emailIdl.requestFocus();
                } else if (pssd.isEmpty()) {
                    emailIdl.setError("Please enter your password");
                    emailIdl.requestFocus();
                } else if (!pssd.isEmpty() && !email.isEmpty()) {
                    // --------- Check the sign in inputs if it match's the data in Firebase -------
                    mAuthL = new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            FirebaseUser mFireU = fbAuth.getCurrentUser();
                        }
                    };
                    fbAuth.signInWithEmailAndPassword(email, pssd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Try to login again", Toast.LENGTH_SHORT).show();
                            } else {
                                DeleteAndSetToken();
                                finish();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
            }


        });
        //------------------- Button to move this activity to Sign Up Activity -------------------
        signUptv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ProgressDialog progressBar = ProgressDialog.show(LoginActivity.this, "Title",
                        "Login Page");
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                return false;
            }
        });

    }

    private void DeleteAndSetToken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseDatabase.getInstance().getReference("Tokens").child(firebaseUser.getUid()).setValue(refreshToken);
    }

}
