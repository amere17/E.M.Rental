/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Sign In class for registered users
*/
package com.example.emrental;
//---------------- Android imports ------------------------
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;

//-------------------- Main Activity for users after login ------------------
public class HomeActivity extends AppCompatActivity {
    //------------- Variables & Objects -------------------
    FirebaseAuth fireA;
    private FirebaseAuth.AuthStateListener mAuthL;
    Button logoutBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //-------------- attach variables with XML file ----------
        logoutBtn = (Button)findViewById(R.id.button6);
        //-------------- method for Logout Button --------------
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToLogin = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intToLogin);
            }
        });

    }
}
