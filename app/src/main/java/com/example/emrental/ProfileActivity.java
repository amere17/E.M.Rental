/*
        Written by:Mohamad Amer & Muhammed Egbaryia
        Date: 4/03/2020
        Subject: Activity is to get the data from the firebase for the current user
        and show the data in profile activity
*/
package com.example.emrental;

//---------------- Android imports ------------------------
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {
    //----------------------- Variables & Objects -------------------
    TextView phonetv, emailtv, fullnametv, paypaltv;
    ImageView avatar;
    Button logoutbtn;
    ListView dealslv,toolslv;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);

        //-----------------  Attaching objects with XML file --------------
        phonetv = findViewById(R.id.phone);
        emailtv = findViewById(R.id.email);
        fullnametv = findViewById(R.id.fullname);
        paypaltv = findViewById(R.id.paypal);
        logoutbtn = findViewById(R.id.logout);
        avatar = findViewById(R.id.imageView);
        dealslv = findViewById(R.id.dealsl);
        toolslv = findViewById(R.id.toolsl);
        //---------------- get firebase data for current user --------------
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        //---------------- get all the data for the current user and display it ------
        DocumentReference dr = fstore.collection("Users").document(userId);
        dr.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phonetv.setText(documentSnapshot.getString("Phone"));
                fullnametv.setText(documentSnapshot.getString("Full Name"));
                emailtv.setText(documentSnapshot.getString("Email"));
                paypaltv.setText(documentSnapshot.getString("PayPal"));
            }
        });
        //-------------- method for logout button -----------------
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToLogin = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intToLogin);
            }
        });


    }
}
