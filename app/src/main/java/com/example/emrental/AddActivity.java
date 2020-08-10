/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Activity for add new tool to the list
*/
package com.example.emrental;
//---------------- Android imports ------------------------
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Add Activity -
 * Responsible for adding new tool
 */
public class AddActivity extends AppCompatActivity {
    //----------------------- Variables & Objects -------------------
    FusedLocationProviderClient client;
    FirebaseFirestore mFireStore;
    DatabaseReference ref;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    RadioButton radioButton;
    public String id;
    Map<String, String> mToolList = new HashMap<>();
    String result;
    List<Address> list = null;

    /**
     * onCreate - first time creating class object of AddActivity
     * gets location permissions, initializes members...
     *
     * @param savedInstanceState last saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);

        //----------------- implement Firebase "FIRESTORE" & location client ------------
        mFireStore = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        client = LocationServices.getFusedLocationProviderClient(this);
        //-----------------  Attaching objects with XML file --------------
        final Button Cord = findViewById(R.id.button7);
        final Button Add = findViewById(R.id.button8);
        final EditText toolName = findViewById(R.id.editText);
        final EditText toolPrice = findViewById(R.id.editText3);
        final TextView tv = findViewById(R.id.textView2);
        final RadioGroup mType = findViewById(R.id.type);
        tv.setVisibility(View.INVISIBLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //----------------- Call function to request permission for Location service ---------
        //requestPer();
        //----------------- Current location for the item to add a marker on the map -------
        Cord.setOnClickListener(new View.OnClickListener() {
            /**
             * get coordinates when clicked on get location button
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddActivity.this, "Please turn on the GPS", Toast.LENGTH_SHORT).show();
                } else {
                    client.getLastLocation().addOnSuccessListener(AddActivity.this, new OnSuccessListener<Location>() {
                        /**
                         * got the location successfully
                         *
                         * @param location
                         */
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                                Address address;
                                try {
                                    list = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                address = list.get(0);
                                result = address.getAddressLine(0) + "\n" + address.getFeatureName();
                                Toast.makeText(AddActivity.this, result, Toast.LENGTH_LONG).show();
                                tv.setText(location.getLatitude() + " " + location.getLongitude());
                            } else {
                                Toast.makeText(AddActivity.this, "Please turn on the GPS", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        //----------------- Upload image for the tool -----------------

        //---------------- methods for clicking add button ------------
        //-- add a new tool with all the info to the firebase database -
        ref = FirebaseDatabase.getInstance().getReference().child("tools");
        Add.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick listener of the add tool button
             * collect input data
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                boolean validPrice = true;
                String mUserUid = currentFirebaseUser.getUid();
                String mToolName = toolName.getText().toString().trim();
                String mToolPrice = toolPrice.getText().toString().trim();
                int mRadioOpt = mType.getCheckedRadioButtonId();
                radioButton = findViewById(mRadioOpt);
                String m_location = tv.getText().toString();
                if (!mToolPrice.isEmpty()) {
                    int mPrice = Integer.parseInt(mToolPrice);
                    if (mPrice > 100 || mPrice < 0)
                        validPrice = false;
                }
                // Validation to add tool with the right inputs
                if (!TextUtils.isEmpty(mToolName) && !TextUtils.isEmpty(mToolPrice) && !TextUtils.isEmpty(m_location)
                        && radioButton.isChecked() && validPrice) {

                    // add new tool to realtime database and the collection firebase "tools"
                    mToolList.put("name", mToolName);
                    mToolList.put("price", mToolPrice);
                    mToolList.put("userid", mUserUid);
                    mToolList.put("type", radioButton.getText().toString());
                    mToolList.put("location", tv.getText().toString());
                    mToolList.put("status", "1");
                    mToolList.put("address", result);
                    mFireStore.collection("tools").add(mToolList).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        /**
                         * onSuccess listener - create new tool and save into the DB
                         *
                         * @param documentReference
                         */
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddActivity.this, "Tool Saved", Toast.LENGTH_SHORT).show();

                            id = documentReference.getId();
                            ref.child(id).setValue(mToolList);
                        }
                    });


                    // Stop this activity and return to the Home Activity
                    finish();
                    Intent i = new Intent(AddActivity.this, HomeActivity.class);
                    startActivity(i);

                } else {
                    toolPrice.setError("Price range 0-100");
                    Toast.makeText(AddActivity.this, "All the inputs required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //--- function to request permission for Location service ----

    /**
     * request GPS/Location permissions
     */
    private void requestPer(){
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION},1);
    }
}
