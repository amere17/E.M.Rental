package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DealDisplay extends AppCompatActivity {
    TextView start,end,total;
    DatabaseReference ref;
    Order order =new Order();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deal_display);
        ref = FirebaseDatabase.getInstance().getReference().child("Deals");
        total= findViewById(R.id.dealtotal);
        end = findViewById(R.id.dealend);
        start = findViewById(R.id.dealstart);
        final String dealId= getIntent().getExtras().getString("DealId");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    order = ds.getValue(Order.class);
                    if(ds.getKey().equals(dealId)){
                        total.setText(order.getTotalPrice());
                        end.setText(order.getEnd());
                        start.setText(order.getStart());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
