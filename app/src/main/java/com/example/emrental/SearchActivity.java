/*
Written by:Mohamad Amer & Muhammed Egbaryia
Date: 4/03/2020
Subject: Activity to Earch for a tool from the list
*/
package com.example.emrental;
//---------------- Android imports ------------------------

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * Search Activity
 * responsible for searching tools
 */
public class SearchActivity extends AppCompatActivity {
    //-------------------- Variables & Objects -------------------
    EditText etLocation;
    ListView lvItems;
    ArrayList<String> mArraylist = new ArrayList<>();
    Button searchBtnItems;
    DatabaseReference dr;
    FirebaseDatabase firebasedatabase;
    Tool item;
    int min, max;
    CheckBox cbBike, cbCar, cbScooter;
    Vector<String> cbTypes = new Vector<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference toolsList = db.collection("tools");
    RangeSeekBar etPrice;

    /**
     * init members
     *
     * @param savedInstanceState saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        //-------------- Attaching variables & objects with XML file ----------
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        item = new Tool();
        etLocation = findViewById(R.id.editText5);
        etPrice = (RangeSeekBar) findViewById(R.id.rangeSeekBar);
        searchBtnItems = findViewById(R.id.button2);
        lvItems = findViewById(R.id.m_tools);
        cbBike = findViewById(R.id.checkBox2);
        cbScooter = findViewById(R.id.checkBox3);
        cbCar = findViewById(R.id.checkBox4);
        etPrice.setSelectedMaxValue(100);
        etPrice.setSelectedMinValue(0);
        min = 0;
        max = 100;
        etPrice.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            /**
             * get price range from bar
             *
             * @param bar      bar
             * @param minValue min value
             * @param maxValue max value
             */
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                max = (int) bar.getSelectedMaxValue();
                min = (int) bar.getSelectedMinValue();
                Toast.makeText(getApplicationContext(), min + " " + max, Toast.LENGTH_SHORT).show();

            }
        });

        final ArrayAdapter<String> itemArrayAdapter =
                new ArrayAdapter<String>
                        (SearchActivity.this, android.R.layout.simple_list_item_1, mArraylist);
        lvItems.setAdapter(itemArrayAdapter);
        firebasedatabase = FirebaseDatabase.getInstance();
        // ------------- Tools list path in Firebase to show the list in search activity -----------
        dr = firebasedatabase.getReference("tools");
        // ---------------------- Display tool list -------------------
        // ------------- Next Task: Filtering the list by user inputs -----------
        searchBtnItems.setOnClickListener(new View.OnClickListener() {
            /**
             * collect and search item aording to user input
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), min + " " + max, Toast.LENGTH_LONG).show();
                cbTypes.clear();
                mArraylist.clear();
                fillVecTypes();
                final Vector vec = new Vector<>();
                toolsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    /**
                     * show results
                     * @param queryDocumentSnapshots query Document Snapshots
                     */
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> d = documentSnapshot.getData();
                            if ((TypeOn(d.get("type").toString().trim()) && Double.valueOf(d.get("price").toString()) > min
                                    && Double.valueOf(d.get("price").toString()) < max)
                                    || cbTypes.isEmpty()) {
                                vec.addElement(documentSnapshot.getId());
                                mArraylist.add(d.get("name") + "\n" + d.get("price") + " ₪/hr\n" + d.get("type") + "\n" + d.get("address"));
                            }
                        }
                        lvItems.setAdapter(itemArrayAdapter);
                    }
                });
                lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(SearchActivity.this, OrderActivity.class);
                        i.putExtra("ToolId", vec.elementAt(position).toString());
                        startActivity(i);
                    }
                });
            }
        });
    }

    private boolean TypeOn(String toolType){
        for(String type:cbTypes)
            if(toolType.trim().equals(type.trim()))
                return true;

        return false;
    }

    private void fillVecTypes(){
        if(cbBike.isChecked())
            cbTypes.addElement(cbBike.getText().toString().trim());
        if(cbCar.isChecked())
            cbTypes.addElement(cbCar.getText().toString().trim());
        if(cbScooter.isChecked())
            cbTypes.addElement(cbScooter.getText().toString().trim());
    }

}
