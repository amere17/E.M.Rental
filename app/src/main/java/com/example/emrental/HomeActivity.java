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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//-------------------- Main Activity for users after login ------------------
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    //------------- Variables & Objects -------------------
    GoogleMap mMap;
    private FirebaseAuth.AuthStateListener mAuthL;
    Button logoutBtn,AddToolBtn,ProfileBtn;
    private MapView mapView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference toolsList = db.collection("tools");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //-------------- attach variables with XML file ----------
        ProfileBtn = (Button)findViewById(R.id.button11);
        AddToolBtn = (Button)findViewById(R.id.button12);
        //-------------- method for Profile & Add Tool Button -------------
        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(profIntent);
            }
        });
        AddToolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(),AddActivity.class);
                startActivity(addIntent);
            }
        });
        //--------------- Show Tool Markers on the Map ------------------
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);


    }
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        toolsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String location = documentSnapshot.get("location").toString();
                    Double mLat, mLong;
                    String parts[] = location.split(" ");
                    mLat = Double.parseDouble(parts[0]);
                    mLong = Double.parseDouble(parts[1]);
                    LatLng toolLocation = new LatLng(mLat,mLong);
                    Toast.makeText(getApplicationContext(), mLat+" "+mLong, Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(toolLocation).title(documentSnapshot.get("name").toString()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(toolLocation));
                }
            }
        });
        mMap.setMyLocationEnabled(true);
    }
}
