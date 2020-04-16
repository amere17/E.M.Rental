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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
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
        //-------------- method for User Profile Button -------------
        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(profIntent);
            }
        });
        //-------------- method for Add Tool Button -------------
        AddToolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(),AddActivity.class);
                startActivity(addIntent);
            }
        });
        //-------------- method for Search Button -------------
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(addIntent);
            }
        });
        //------------- request permission for the location service -----------
        requestPer();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // ------------ Find the current user location ----------
        getCurrLocation();

        // ------------ Methods for the view of the map ----------
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }
    private void getCurrLocation(){
        client.getLastLocation().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                  currLocation = location;
            }
        });
    }
    // --------------- Show the tools form the list in firebase on the map ------------------
    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        // ------------------ Read data from the tools list in Firebase ----------------------
        // ------------------ Put markers on the map for all the tools  ----------------------
        toolsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String location = documentSnapshot.get("location").toString();
                    Double mLat, mLong;
                    String parts[] = location.split(" ");
                    // -------- Coordinates for each tool --------
                    mLat = Double.parseDouble(parts[0]);
                    mLong = Double.parseDouble(parts[1]);
                    LatLng toolLocation = new LatLng(mLat, mLong);
                    // -------- Match the marker icon with the type of the tool ----------
                    if (documentSnapshot.get("type").equals("Bike")) {
                        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_bike);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    } else if (documentSnapshot.get("type").equals("Car")) {
                        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_car);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    } else{
                        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.icon_scooter);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    }
                    // -------------------- Zoom in to the current user location -------------------
                    // ------------- Error when there is a problem to read the current location ----
                   if ( currLocation !=null){
                        LatLng curLocation = new LatLng(currLocation.getLatitude(),currLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 17));
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error! Please Turn on GPS", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent i = new Intent(HomeActivity.this,OrderActivity.class);
                Bundle b = new Bundle();
                i.putExtra("ToolId",marker.getSnippet());
                startActivity(i);
                return false;
            }
        });
        mMap.setMyLocationEnabled(true);
    }
    private void requestPer(){
        ActivityCompat.requestPermissions(this,new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION},1);
    }
    // -------- Function to create icon for each type of tool --------
    public Bitmap icon_Bitmap(BitmapDrawable markerPath){
        int height = 70;
        int width = 70;
        Bitmap b = markerPath.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }
}
