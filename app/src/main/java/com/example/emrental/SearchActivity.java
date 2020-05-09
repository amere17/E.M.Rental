/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Activity to Earch for a tool from the list
*/
package com.example.emrental;
//---------------- Android imports ------------------------
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    //-------------------- Variables & Objects -------------------
    EditText etLocation,etPrice;
    ListView lvItems;
    ArrayList<String> mArraylist = new ArrayList<>();
    Button searchBtnItems;
    DatabaseReference dr;
    FirebaseDatabase firebasedatabase;
    Tool item;
    CheckBox cbBike,cbCar,cbScooter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        //-------------- Attaching variables & objects with XML file ----------
        item = new Tool();
        etLocation = findViewById(R.id.editText5);
        etPrice = findViewById(R.id.editText7);
        searchBtnItems = findViewById(R.id.button2);
        lvItems = findViewById(R.id.m_tools);
        cbBike = findViewById(R.id.checkBox2);
        cbScooter = findViewById(R.id.checkBox3);
        cbCar = findViewById(R.id.checkBox4);
        final ArrayAdapter<String> itemArrayAdapter =
                new ArrayAdapter<String>
                        (SearchActivity.this,android.R.layout.simple_list_item_1,mArraylist);
        lvItems.setAdapter(itemArrayAdapter);
        firebasedatabase = FirebaseDatabase.getInstance();
        // ------------- Tools list path in Firebase to show the list in search activity -----------
        dr = firebasedatabase.getReference("tools");
        // ---------------------- Display tool list -------------------
        // ------------- Next Task: Filtering the list by user inputs -----------
        dr.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    item = ds.getValue(Tool.class);
                    mArraylist.add(item.getName()+"\n"+item.getPrice()+" "+item.getType()+"\n"+item.getLocation());
                }
                lvItems.setAdapter(itemArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
