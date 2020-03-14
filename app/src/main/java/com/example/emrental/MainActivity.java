/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Register activity for new users in the application
*/
package com.example.emrental;
//------------------ Android imports ----------------
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    //----------------- Variables & Objects -------------------
    public EditText emailId, passwordId, paypalId, fullnameId, phoneId;
    Button signUpBtn;
    TextView signIntv;
    String userId;
    public FirebaseAuth fbAuth;
    FirebaseFirestore fstore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        fbAuth = FirebaseAuth .getInstance();
        fstore = FirebaseFirestore.getInstance();
        //------------- attaching objects with XML File -------------
        emailId = (EditText)findViewById(R.id.editText2);
        passwordId = (EditText)findViewById(R.id.editText4);
        paypalId = (EditText)findViewById(R.id.editText9);
        fullnameId= (EditText)findViewById(R.id.editText13);
        phoneId = (EditText)findViewById(R.id.editText11);
        signUpBtn = (Button)findViewById(R.id.button);
        signIntv = (TextView)findViewById(R.id.textView);
        //------------------ Sign Up button methods -------------------
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            String email, password, fullname, phone, paypal;
            @Override
            //-------------- Sign Up inputs Validations ------------
            //-------------- If all inputs valid move to login activity --------------
            public void onClick(View v) {
                email = emailId.getText().toString().trim();
                password = passwordId.getText().toString().trim();
                fullname = fullnameId.getText().toString().trim();
                phone = phoneId.getText().toString().trim();
                paypal = paypalId.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailId.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordId.setError("Password is Required");
                    return;
                }
                if(TextUtils.isEmpty(fullname)){
                    fullnameId.setError("Full Name is Required");
                    return;
                }
                if(TextUtils.isEmpty(paypal)){
                    paypalId.setError("PayPal.me is Required");
                    SystemClock.sleep(4000);
                    paypalId.setError("Search in Google: Create PayPal.me Account");
                    SystemClock.sleep(4000);
                    return;
                }
                if(!URLUtil.isValidUrl(paypal)){
                    paypalId.setError("Please Enter Valid Url");
                    return;
                }
                if(password.length()<8){
                    passwordId.setError("Password must be >= 8");
                    return;
                }
             fbAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()){

                         userId = fbAuth.getCurrentUser().getUid();
                         DocumentReference dr = fstore.collection("Users").document(userId);
                         Map<String,Object> user = new HashMap<>();
                         Vector<String> deals = new Vector<>();
                         user.put("Full Name",fullname);
                         user.put("Email",email);
                         user.put("Phone",phone);
                         user.put("PayPal",paypal);
                         user.put("Deals",deals);
                         dr.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Toast.makeText(MainActivity.this,"User Created.", Toast.LENGTH_SHORT).show();
                                 Intent i = new Intent(MainActivity.this,LoginActivity.class);
                                 startActivity(i);
                             }
                         });
                     }
                     else {
                         Toast.makeText(MainActivity.this,"Error.", Toast.LENGTH_SHORT).show();
                     }
                 }
             });
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
