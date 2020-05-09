package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class OrderActivity extends AppCompatActivity {
    DatabaseReference ref,ref2;
    DocumentReference dr,dr2;
    private Button OrderBtn,StatusBtn;
    FirebaseFirestore fstore;
    TextView tName,tPrice,tLocation,tType,tOwner;
    Tool item;
    Order order;
    FirebaseAuth fAuth;
    public String userIdB;
    public int c = 0;
    public Date startDate;
    public Date endDate;
    public long totalDealTime;
    SimpleDateFormat simpleDate =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
    public String userIdA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ref = FirebaseDatabase.getInstance().getReference().child("Deals");
        ref2 = FirebaseDatabase.getInstance().getReference().child("tools");
        StatusBtn = (Button)findViewById(R.id.StatusBtn);
        OrderBtn = (Button)findViewById(R.id.bOrder);
        tName = findViewById(R.id.tName);
        tPrice = findViewById(R.id.tPrice);
        tLocation = findViewById(R.id.tLocation);
        tType = findViewById(R.id.tType);
        tOwner = findViewById(R.id.tOwner);
        final String toolId = getIntent().getExtras().getString("ToolId");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
        }
        else{
            userIdB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if(userIdB ==  FirebaseAuth.getInstance().getCurrentUser().getUid())
            StatusBtn.setVisibility(View.INVISIBLE);
        fstore = FirebaseFirestore.getInstance();
        dr = fstore.collection("tools").document(toolId);
        dr.addSnapshotListener(OrderActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    tName.setText(documentSnapshot.getString("name"));
                    tPrice.setText(documentSnapshot.getString("price"));
                    tLocation.setText(documentSnapshot.getString("location"));
                    tType.setText(documentSnapshot.getString("type"));
                    userIdA = documentSnapshot.getString("userid");


                dr2 = fstore.collection("Users").document(documentSnapshot.getString("userid"));
                    dr2.addSnapshotListener(OrderActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            String owner = tOwner.getText()+" "+ documentSnapshot.getString("Full Name");
                            tOwner.setText("Renter: "+owner.toUpperCase());
                        }
                    });
                if(documentSnapshot.getString("status").equals("0")){
                    StatusBtn.setText("End");
                    OrderBtn.setText("Not Available");
                    OrderBtn.setClickable(false);
                }
                else{
                    StatusBtn.setText("InProgress");
                    OrderBtn.setText("Order");
                    OrderBtn.setClickable(true);
                }
                if(userIdA.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    StatusBtn.setVisibility(View.VISIBLE);
                    OrderBtn.setVisibility(View.INVISIBLE);
                }
            }

        });


        tOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderActivity.this,ProfileActivity.class);
                i.putExtra("UserId",userIdA);
                startActivity(i);
                finish();
            }
        });

        OrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,String> mToolList = new HashMap<>();
                mToolList.put("Owner",userIdA);
                mToolList.put("User",userIdB);
                mToolList.put("ToolId",toolId);
                mToolList.put("Status","A");
                mToolList.put("start","null");
                mToolList.put("end","null");
                mToolList.put("totalPrice","null");
                ref.push().setValue(mToolList);

            }
        });

        order = new Order();
        StatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            order = ds.getValue(Order.class);
                            if(order.getToolId().equals(toolId)){
                                if(order.getStatus().equals("A")){
                                    startDate = Calendar.getInstance().getTime();
                                    ref.child(ds.getKey()).child("Status").setValue("B");
                                    String timestamp = simpleDate.format(startDate);
                                    ref.child(ds.getKey()).child("start").setValue(timestamp);
                                    StatusBtn.setText("End");
                                    OrderBtn.setText("Not Available");
                                    dr.update("status","0");
                                    OrderBtn.setClickable(false);
                                    break;
                                }
                                else if(order.getStatus().equals("B")){
                                    endDate = Calendar.getInstance().getTime();
                                    ref.child(ds.getKey()).child("Status").setValue("C");
                                    String timestamp = simpleDate.format(endDate);
                                    dr.update("status","1");
                                    ref.child(ds.getKey()).child("end").setValue(timestamp);
                                    break;
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

}
