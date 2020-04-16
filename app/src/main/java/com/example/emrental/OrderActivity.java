package com.example.emrental;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class OrderActivity extends AppCompatActivity {
    DatabaseReference ref;
    DocumentReference dr;
    Button OrderBtn;
    FirebaseFirestore fstore;
    TextView tName,tPrice,tLocation,tType;
    Tools item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        OrderBtn = findViewById(R.id.bOrder);
        tName = findViewById(R.id.tName);
        tPrice = findViewById(R.id.tPrice);
        tLocation = findViewById(R.id.tLocation);
        tType = findViewById(R.id.tType);

        String toolId = getIntent().getExtras().getString("ToolId");
        fstore = FirebaseFirestore.getInstance();
        dr = fstore.collection("tools").document(toolId);
        dr.addSnapshotListener(OrderActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    tName.setText(documentSnapshot.getString("name"));
                    tPrice.setText(documentSnapshot.getString("price"));
                    tLocation.setText(documentSnapshot.getString("location"));
                    tType.setText(documentSnapshot.getString("type"));
            }
        });
        Toast.makeText(OrderActivity.this,toolId,Toast.LENGTH_LONG).show();

    }
}
