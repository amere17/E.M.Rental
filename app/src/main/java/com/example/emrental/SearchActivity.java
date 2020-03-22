package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    EditText etLocation,etPrice;
    ListView lvItems;
    ArrayList<String> mArraylist = new ArrayList<>();
    Button searchBtnItems;
    DatabaseReference dr;
    FirebaseDatabase firebasedatabase;
    Tools item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        item = new Tools();
        etLocation = findViewById(R.id.editText5);
        etPrice = findViewById(R.id.editText7);
        searchBtnItems = findViewById(R.id.button2);
        lvItems = findViewById(R.id.m_tools);

       final ArrayAdapter<String> itemArrayAdapter = new ArrayAdapter<String>(SearchActivity.this,android.R.layout.simple_list_item_1,mArraylist);
        lvItems.setAdapter(itemArrayAdapter);
        firebasedatabase = FirebaseDatabase.getInstance();
        dr = firebasedatabase.getReference("tools");
        Toast.makeText(getApplicationContext(),"1", Toast.LENGTH_LONG).show();
        dr.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    item = ds.getValue(Tools.class);
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
