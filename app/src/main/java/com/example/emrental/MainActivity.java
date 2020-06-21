/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Register activity for new users in the application
*/
package com.example.emrental;
// ------------------ Android imports ----------------

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    // ----------------- Variables & Objects -------------------
    EditText emailId, passwordId, paypalId, fullnameId, phoneId;
    Button signUpBtn, UpldPI;
    TextView signIntv, PayPalCrt;
    String userId;
    FirebaseAuth fbAuth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        fbAuth = FirebaseAuth.getInstance();
        // --------------- Check if the user already signed in ------------
        if (fbAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        fstore = FirebaseFirestore.getInstance();
        // ------------- Attaching objects with XML File -------------
        emailId = findViewById(R.id.editText2);
        passwordId = findViewById(R.id.editText4);
        paypalId = findViewById(R.id.editText9);
        fullnameId = findViewById(R.id.editText13);
        phoneId = findViewById(R.id.editText11);
        signUpBtn = findViewById(R.id.button);
        signIntv = findViewById(R.id.textView);
        PayPalCrt = findViewById(R.id.PayPaltv);
        PayPalCrt.setMovementMethod(LinkMovementMethod.getInstance());
        PayPalCrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bIntent = new Intent(Intent.ACTION_VIEW);
                bIntent.setData(Uri.parse("http://paypal.com"));
                startActivity(bIntent);
            }
        });
        // ------------------ Sign Up button methods -------------------
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            String email, password, fullname, phone, paypal;

            @Override
            // -------------- Sign Up inputs Validations ------------
            // -------------- If all inputs valid, move to login activity --------------
            public void onClick(View v) {
                email = emailId.getText().toString().trim();
                password = passwordId.getText().toString().trim();
                fullname = fullnameId.getText().toString().trim();
                phone = phoneId.getText().toString().trim();
                paypal = paypalId.getText().toString().trim();
                boolean is_Valid = true;
                if (TextUtils.isEmpty(email.trim())) {
                    emailId.setError("Email is Required.");
                    is_Valid = false;
                }
                if (!isEmail(email.trim())) {
                    emailId.setError("Email is invalid");
                    is_Valid = false;
                }
                if (TextUtils.isEmpty(password.trim())) {
                    passwordId.setError("Password is Required");
                    is_Valid = false;
                }
                if (TextUtils.isEmpty(fullname.trim())) {
                    fullnameId.setError("Full Name is Required");
                    is_Valid = false;
                }
                if (phone.trim().length() != 10 || phone.contains(" ")) {
                    phoneId.setError("Phone Number must include 10 digits");
                    is_Valid = false;
                }
                if (TextUtils.isEmpty(paypal.trim())) {
                    paypalId.setError("PayPal is Required");
                    Toast.makeText(MainActivity.this, "Create new PayPal Account", Toast.LENGTH_SHORT).show();
                    is_Valid = false;
                }
                if (!isFullname(fullname.trim())) {
                    fullnameId.setError("Name is invalid");
                    is_Valid = false;
                }
                if (!isEmail(paypal)) {
                    paypalId.setError("Invalid PayPal Email");
                    Toast.makeText(MainActivity.this, "Create new PayPal Account", Toast.LENGTH_SHORT).show();
                    is_Valid = false;
                }
                if (password.trim().length() < 8) {
                    passwordId.setError("Password must be >= 8");
                    is_Valid = false;
                }
                if (!is_Valid)
                    return;
                // ----------------------- add the new user data to the Firebase --------------
                fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            userId = fbAuth.getCurrentUser().getUid();
                            DocumentReference dr = fstore.collection("Users").document(userId);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Full Name", fullname);
                            user.put("Email", email);
                            user.put("Phone", phone);
                            user.put("PayPal", paypal);
                            user.put("rate", "0");
                            dr.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(MainActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(i);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Email is already existed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // ------------------ Moving to the login page by Clicking the TextView------------------
        signIntv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ProgressDialog progressBar = ProgressDialog.show(MainActivity.this, "Title",
                        "Login Page");
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                return false;
            }
        });
    }

    public static boolean isFullname(String str) {
        String expression = "^[a-zA-Z\\s]+";
        return str.matches(expression);
    }

    public static boolean isEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
