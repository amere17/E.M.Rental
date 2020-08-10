/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Register activity for new users in the application
*/
package com.example.emrental;
// ------------------ Android imports ----------------

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Main Activity - Registration Page
 * collect data from user and creates a new account,
 * if passed all the basic validations, in th DB
 */
public class MainActivity extends AppCompatActivity {
    // ----------------- Variables & Objects -------------------
    EditText emailId, passwordId, paypalId, fullnameId, phoneId;
    Button signUpBtn;
    TextView signIntv, PayPalCrt;
    String userId;
    FirebaseAuth fbAuth;
    FirebaseFirestore fstore;

    /**
     * init members
     *
     * @param savedInstanceState load last saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        fbAuth = FirebaseAuth.getInstance();
        // --------------- Check if the user already signed in ------------
        try {
            if (fbAuth.getCurrentUser() != null) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                finish();
            }
        } catch (Exception e) {
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
            /**
             * onClick listener - create a new Paypal account if does't exist
             *
             * @param v view
             */
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

            /**
             * Sign Up - onClick listener
             * collect data, check validity
             * if OK-> create new account
             *
             * @param v view
             */
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
                    /**
                     * create a new user in the DB
                     *
                     * @param task task
                     */
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
            /**
             * Sign In textView listener
             * already has an account
             * move user to the login
             *
             * @param v     view
             * @param event event
             * @return false (default)
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
                return false;
            }
        });
    }

    /**
     * full name validator
     * check if matches name pattern
     *
     * @param str name
     * @return true if matches the pattern; else false
     */
    public static boolean isFullname(String str) {
        String expression = "^[a-zA-Z\\s]+";
        return str.matches(expression);
    }

    /**
     * email validator
     * check if matches email pattern
     * @param email name
     * @return true if matches the pattern; else false
     */
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
