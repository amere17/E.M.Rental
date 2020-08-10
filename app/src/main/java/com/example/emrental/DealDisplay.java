package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
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


/**
 * DealDisplay
 * shows the deal details when clicked on the deals list in profile
 */
public class DealDisplay extends AppCompatActivity {
    TextView start, end, total, status, tenant, tool, tenantDec;
    DatabaseReference ref, ref2;
    FirebaseFirestore fstore;
    Order order = new Order();

    /**
     * onCreate: init members
     *
     * @param savedInstanceState last saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deal_display);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ref = FirebaseDatabase.getInstance().getReference().child("Deals");
        ref2 = FirebaseDatabase.getInstance().getReference().child("tools");
        total = findViewById(R.id.dealtotal);
        end = findViewById(R.id.dealend);
        start = findViewById(R.id.dealstart);
        status = findViewById(R.id.dealStatus);
        tenant = findViewById(R.id.dealUser);
        tenantDec = findViewById(R.id.txtUser);
        tool = findViewById(R.id.dealTool);
        final String dealId = getIntent().getExtras().getString("DealId");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * read data from DB
             *
             * @param dataSnapshot data snapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    order = ds.getValue(Order.class);
                    if (ds.getKey().equals(dealId)) {
                        total.setText(order.getTotalPrice());
                        end.setText(order.getEnd());
                        start.setText(order.getStart());
                        status.setText(CheckStatus(order.getStatus()));
                        getToolData(order.getToolId());
                        if (order.getStatus().equals("C"))
                            getUserData(order.getUser());
                        else {
                            tenant.setVisibility(View.GONE);
                            tenantDec.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * gets user data from DB
     *
     * @param user userID
     */
    private void getUserData(String user) {
        fstore = FirebaseFirestore.getInstance();
        DocumentReference dr = fstore.collection("Users").document(user);
        dr.addSnapshotListener(DealDisplay.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String nameUser, phoneUser;
                phoneUser = documentSnapshot.getString("Phone");
                nameUser = documentSnapshot.getString("Full Name");
                tenant.setText("User Name: " + nameUser + "\nPhone Number: " + phoneUser);
            }
        });
    }

    /**
     * gets tool data from DB
     *
     * @param toolId toolID
     */
    private void getToolData(final String toolId) {
        ref2.orderByChild(toolId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Tool toolc = postSnapshot.getValue(Tool.class);
                    if (postSnapshot.getKey().equals(toolId))
                        tool.setText("Tool Name: " + toolc.getName() + "\nTool Type: " + toolc.getType());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * check deal stats
     *
     * @param status order status
     * @return string of deal status
     */
    private String CheckStatus(String status) {
        if (status.equals("C"))
            return "Payment Needed";
        else if (status.equals("D"))
            return "Payment Confirmed";
        return "in Progress";
    }
}
