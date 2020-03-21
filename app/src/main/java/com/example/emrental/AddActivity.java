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
import android.location.Location;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class AddActivity extends AppCompatActivity {
    //----------------------- Variables & Objects -------------------
    private FusedLocationProviderClient client;
    private FirebaseFirestore mFireStore;
    public Location mLocation ;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    RadioButton radioButton;
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
        final Button Upld = findViewById(R.id.button9);
        final Button Add = findViewById(R.id.button8);
        final EditText toolName = findViewById(R.id.editText);
        final EditText toolPrice = findViewById(R.id.editText3);
        ImageView toolPic = findViewById(R.id.imageView4);
        final TextView tv = findViewById(R.id.textView2);
        final RadioGroup mType = findViewById(R.id.type);
        //----------------- Call function to request permission for Location service ---------
        requestPer();
        //----------------- current location for the item to add a marker on the map -------
        Cord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                client.getLastLocation().addOnSuccessListener(AddActivity.this, new OnSuccessListener<Location>() {
                    @Override
                     public void onSuccess(Location location) {

                        if(location != null){
                            //mLocation.setLatitude(location.getLatitude());
                            //mLocation.setLongitude(location.getLongitude());
                            tv.setText(location.getLatitude() + " " + location.getLongitude());
                        }
                        else{
                         }
                    }
                });
            }
        });
        //----------------- Upload image for the tool -----------------
        Upld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });
        //---------------- methods for clicking add button ------------
        //-- add a new tool with all the info to the firebase database -
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUserUid = currentFirebaseUser.getUid();
                //Tool tool = new Tool(mToolName,mToolPrice,mType);
                String mToolName = toolName.getText().toString();
                String mToolPrice = toolPrice.getText().toString();
                int mRadioOpt = mType.getCheckedRadioButtonId();
                radioButton = findViewById(mRadioOpt);
                String mtype = radioButton.getText().toString();

                // map for all the data to add the tool easily to collection in firebase "tools"
                Map<String,String> mToolList = new HashMap<>();
                mToolList.put("name",mToolName);
                mToolList.put("price",mToolPrice);
                mToolList.put("userid",mUserUid);
                mToolList.put("type",mtype);
                mToolList.put("location",tv.getText().toString());
                mFireStore.collection("tools").document().set(mToolList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddActivity.this, "Tool Saved", Toast.LENGTH_SHORT).show();

                    }
                });
                finish();
                Intent i = new Intent(AddActivity.this,HomeActivity.class);
                startActivity(i);
            }
        });
    }

    private void choose() {

    }
    //--- function to request permission for Location service ----
    private void requestPer(){
        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION},1);
    }
}
