/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: The home display for the application
*/
package com.example.emrental;
//---------------- Android imports ------------------------

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

//-------------------- Main Activity for users after login ------------------

/**
 * Home Activity -
 * main page, showing the map after logging in,
 * map includes existed tools objects
 * buttons of profile, search, add tool (+), my location
 */
public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "Error";
    public static Activity fa;
    //------------- Variables & Objects -------------------
    GoogleMap mMap;
    Button AddToolBtn, ProfileBtn, SearchBtn;
    public FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference toolsList;
    FusedLocationProviderClient client;
    public Location currLocation = null;

    /**
     * onCreate - init members and DB, check permissions
     *
     * @param savedInstanceState last saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        if (db.collection("tools") != null)
            toolsList = db.collection("tools");
        //-------------- attach variables with XML file ----------
        ProfileBtn = (Button) findViewById(R.id.button11);
        AddToolBtn = (Button) findViewById(R.id.button12);
        SearchBtn = (Button) findViewById(R.id.btnSearch);
        fa = this;
        //-------------- method for User profile Button -------------
        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profIntent);
            }
        });
        //-------------- method for add Tool Button -------------
        AddToolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(addIntent);
            }
        });
        //-------------- method for search Button -------------
        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(addIntent);
            }
        });
        //------------- request permission for the location service -----------
        requestPerCurrent();
        client = LocationServices.getFusedLocationProviderClient(this);
        // ------------ Find the current user location ----------
        getCurrLocation();
        // ------------ Methods for the view of the map ----------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomeActivity.this);
    }

    /**
     * get current location
     */
    private void getCurrLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.getLastLocation().addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currLocation = location;
                getCurrentLocation();
            }
        });
    }

    // --------------- Show the tools form the list in firebase on the map ------------------

    /**
     * adding tool items on map when all is loaded and ready
     *
     * @param googleMap map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        getCurrLocation();
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        // ------------------ Read data from the tools list in Firebase ----------------------
        // ------------------ Put markers on the map for all the tools  ----------------------

        toolsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String location = documentSnapshot.get("location").toString();
                    Double mLat, mLong;
                    String parts[] = location.split(" ");
                    // -------- Coordinates for each tool --------
                    mLat = Double.parseDouble(parts[0]);
                    mLong = Double.parseDouble(parts[1]);
                    LatLng toolLocation = new LatLng(mLat, mLong);
                    // -------- Match the marker icon with the type of the tool ----------
                    if (documentSnapshot.get("type").equals("Bike")) {
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_bike);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    } else if (documentSnapshot.get("type").equals("Car")) {
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_car);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    } else {
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_scooter);
                        mMap.addMarker(new MarkerOptions().snippet(documentSnapshot.getId()).position(toolLocation).icon(BitmapDescriptorFactory.fromBitmap(icon_Bitmap(bitmapdraw))));
                    }
                    // -------------------- Zoom in to the current user location -------------------
                    // ------------- Error when there is a problem to read the current location ----
                }
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            /**
             * show tool details when marker on map was clicked and opens OrderActivity
             *
             * @param marker item on map
             * @return false - placeholder
             */
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Intent i = new Intent(HomeActivity.this, OrderActivity.class);
                i.putExtra("ToolId", marker.getSnippet());
                startActivity(i);
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     * request location permissions
     */
    private void requestPer() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    dialog.cancel();
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }

    }

    /**
     * Function to create icon for each type of tool
     *
     * @param markerPath marker path
     * @return bitmap icon
     */
    public Bitmap icon_Bitmap(BitmapDrawable markerPath) {
        int height = 200;
        int width = 350;
        Bitmap b = markerPath.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    public void getCurrentLocation() {
        if (currLocation != null) {
            LatLng curLocation = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, 12));
        } else {
            requestPer();
        }
    }

    public void getCurrentLocation(View view) {
        getCurrLocation();
    }

    private void requestPerCurrent() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }
}