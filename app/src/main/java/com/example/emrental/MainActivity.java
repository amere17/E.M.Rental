/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Register activity for new users in applications
*/
package com.example.emrental;
//------------------ Android imports ----------------
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    //----------------- Variables & Objects -------------------
    public EditText emailId, passwordId;
    Button signUpBtn;
    TextView signIntv;
    FirebaseAuth fbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        fbAuth = FirebaseAuth.getInstance();
        //------------- attaching objects with XML File -------------
        emailId = (EditText)findViewById(R.id.editText2);
        passwordId = (EditText)findViewById(R.id.editText4);
        signUpBtn = (Button)findViewById(R.id.button);
        signIntv = (TextView)findViewById(R.id.textView);
        //------------------ Sign Up button methods -------------------
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            //-------------- Sign Up inputs Validations ------------
            //-------------- If all inputs valid move to login activity --------------
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pssd = passwordId.getText().toString();
                if(email.isEmpty())
                {
                    emailId.setError("Please enter your email");
                    emailId.requestFocus();
                }
                else if(pssd.isEmpty())
                {
                    emailId.setError("Please enter your password");
                    emailId.requestFocus();
                }
                else{
                    fbAuth.createUserWithEmailAndPassword(email,pssd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Please Try Again",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                            }
                        }
                    });
                }
            }
        });
        //------------------ move to login activty while click on this text ------------------
        signIntv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ProgressDialog progressBar = ProgressDialog.show(MainActivity.this, "Title",
                        "Login Page");
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                return false;
            }
        });
    }
}
