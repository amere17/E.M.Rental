/*
        Written by:Mohamad Amer & Muhammed Egbaryia
        Date: 4/03/2020
        Subject: Activity is to get the data from the firebase for the current user
        and show the data in profile activity
*/
package com.example.emrental;

//---------------- Android imports ------------------------
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
public class ProfileActivity extends AppCompatActivity {

    //----------------------- Variables & Objects -------------------
    TextView phonetv, emailtv, fullnametv, paypaltv;
    ImageView avatar;
    Button logoutbtn;
    ListView dealslv,toolslv;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    DatabaseReference ref,ref2;
    String userId;
    ArrayList<String> mArraylist = new ArrayList<>();
    ArrayList<String> mArraylist2 = new ArrayList<>();
    Tool item;
    Order order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        Bundle extraStr = getIntent().getExtras();
        String mString = "UserId";
        //-----------------  Attaching objects with XML file --------------
        phonetv = findViewById(R.id.phone);
        emailtv = findViewById(R.id.email);
        fullnametv = findViewById(R.id.fullname);
        paypaltv = findViewById(R.id.paypal);
        logoutbtn = findViewById(R.id.logout);
        avatar = findViewById(R.id.imageView);
        dealslv = findViewById(R.id.dealsl);
        toolslv = findViewById(R.id.toolsl);
        //---------------- get firebase data for current user --------------
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        if(extraStr == null){
        userId = fAuth.getCurrentUser().getUid();
        }
        else {
            userId = extraStr.getString(mString);
            logoutbtn.setVisibility(View.GONE);
            dealslv.setVisibility(View.GONE);
        }

        //------------ get all the data for the current user to display in the profile ------
        DocumentReference dr = fstore.collection("Users").document(userId);
        dr.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phonetv.setText(documentSnapshot.getString("Phone"));
                fullnametv.setText(documentSnapshot.getString("Full Name"));
                emailtv.setText(documentSnapshot.getString("Email"));
                paypaltv.setText(documentSnapshot.getString("PayPal"));
            }
        });
        //--------------- fill the Tools list that published by the current user -------------------
        final ArrayAdapter<String> itemArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,android.R.layout.simple_list_item_1,mArraylist);
        final ArrayAdapter<String> dealArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,android.R.layout.simple_list_item_1,mArraylist2);

        ref = FirebaseDatabase.getInstance().getReference("tools");
        ref2 = FirebaseDatabase.getInstance().getReference("Deals");

        item = new Tool();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    item = ds.getValue(Tool.class);
                    if(item.getUserid().equals(userId)) {
                        mArraylist.add(item.getName() + "\n" + item.getPrice() + " " + item.getType() + "\n" + item.getLocation());
                    }

                }
                toolslv.setAdapter(itemArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    order = ds.getValue(Order.class);
                    if((order.getOwner().equals(userId) || order.getUser().equals(userId)) && order.getStatus().equals("C")) {
                        mArraylist2.add(order.getTotalPrice() +"\n"+order.getEnd()+"\n");
                    }

                }
                dealslv.setAdapter(dealArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-------------- method for logout button -----------------
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToLogin = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intToLogin);
            }
        });
        phonetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsappContact(phonetv.getText().toString());
            }
        });

    }
    void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }
}
