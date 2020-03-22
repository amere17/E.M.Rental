/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: The home display for the application
*/
package com.example.emrental;
//---------------- Android imports ------------------------
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//-------------------- Main Activity for users after login ------------------
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    //------------- Variables & Objects -------------------
    GoogleMap mMap;
    Button  AddToolBtn,ProfileBtn,SearchBtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference toolsList = db.collection("tools");
    FusedLocationProviderClient client;
    public Location currLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //-------------- attach variables with XML file ----------
        ProfileBtn = (Button)findViewById(R.id.button11);
        AddToolBtn = (Button)findViewById(R.id.button12);
        SearchBtn = (Button)findViewById(R.id.btnSearch);
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
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(addIntent);
            }
        });
        requestPer();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getCurrLocation();

    }
    private void getCurrLocation(){
        client.getLastLocation().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                  currLocation = location;
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }
    //--------------- Show the tools form the list in firebase on the map ------------------
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
                    mMap.addMarker(new MarkerOptions().position(toolLocation).title(documentSnapshot.get("name").toString()));
                    if ( currLocation !=null){
                        LatLng curLocation = new LatLng(currLocation.getLatitude(),currLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLocation,10), 5000, null);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error! Please Turn on GPS", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mMap.setMyLocationEnabled(true);

    }
    private void requestPer(){
        ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION},1);
    }
}
