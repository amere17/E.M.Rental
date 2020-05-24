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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ProfileActivity extends AppCompatActivity implements UpdateProfile.dialogListner {

    //----------------------- Variables & Objects -------------------
    TextView phonetv, emailtv, fullnametv, paypaltv, ratetv;
    ImageView avatar;
    Button logoutbtn, edit;
    ListView dealslv, toolslv;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    DatabaseReference ref, ref2;
    String userId;
    String rate;
    ArrayList<String> mArraylist = new ArrayList<>();
    ArrayList<String> mArraylist2 = new ArrayList<>();
    Tool item;
    Order order;
    Map m = new HashMap();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference toolsList = db.collection("tools");
    private CollectionReference dealsList = db.collection("Deals");


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
        ratetv = findViewById(R.id.rateResult);
        edit = findViewById(R.id.editProfile);
        //---------------- get firebase data for current user --------------
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        if (extraStr == null || extraStr.getString(mString).equals(fAuth.getCurrentUser().getUid().trim())) {
            userId = fAuth.getCurrentUser().getUid();
        } else {
            userId = extraStr.getString(mString);
            logoutbtn.setVisibility(View.GONE);
            dealslv.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Toast.makeText(ProfileActivity.this, "Tap on Tool/Deal to delete OR Update Your Profile", Toast.LENGTH_LONG).show();
                    openDialog();
            }


        });
        //------------ get all the data for the current user to display in the profile ------
        DocumentReference dr = fstore.collection("Users").document(userId);
        dr.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phonetv.setText(documentSnapshot.getString("Phone"));
                fullnametv.setText(documentSnapshot.getString("Full Name"));
                emailtv.setText(documentSnapshot.getString("Email"));
                paypaltv.setText(documentSnapshot.getString("PayPal"));
                rate = documentSnapshot.getString("rate");
                ratetv.setText(rate + "/5");

            }
        });
        //--------------- fill the Tools list that published by the current user -------------------
        final ArrayAdapter<String> itemArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, mArraylist);
        final ArrayAdapter<String> dealArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.simple_list_item_1, mArraylist2);
        item = new Tool();
        final Vector vec = new Vector<>();

        ref = FirebaseDatabase.getInstance().getReference("tools");
        ref2 = FirebaseDatabase.getInstance().getReference("Deals");
        toolsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    vec.addElement(documentSnapshot.getId());
                }
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    item = ds.getValue(Tool.class);
                    if (item.getUserid().equals(userId)) {
                        mArraylist.add(item.getName() + "\n" + item.getPrice() + " " + item.getType() + "\n" + item.getLocation());
                    }
                }

                toolslv.setAdapter(itemArrayAdapter);
                toolslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(ProfileActivity.this, OrderActivity.class);
                        i.putExtra("ToolId", vec.elementAt(position).toString());
                        startActivity(i);
                    }
                });
                dealslv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        mArraylist.remove(position);
                        itemArrayAdapter.notifyDataSetChanged();
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final Vector vec2 = new Vector<>();
        dealsList.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    vec2.addElement(documentSnapshot.getId());
                }
            }
        });
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    order = ds.getValue(Order.class);
                    if ((order.getOwner().equals(userId) || order.getUser().equals(userId)) && order.getStatus().equals("D")) {
                        mArraylist2.add(order.getTotalPrice() + "\n" + order.getEnd() + "\n");
                    }

                }
                dealslv.setAdapter(dealArrayAdapter);
                dealslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Intent i = new Intent(ProfileActivity.this, OrderActivity.class);
                        //i.putExtra("ToolId", vec2.elementAt(position).toString());
                        //startActivity(i);
                    }
                });
                dealslv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return false;
                    }
                });
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
                Intent intToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intToLogin);
            }
        });
        phonetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userId.equals(fAuth.getCurrentUser().getUid()))
                    openWhatsappContact(phonetv.getText().toString());
            }
        });

    }

    void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, "Hey, i interested in your rental tool"));
    }

    private void openDialog() {
        UpdateProfile update = new UpdateProfile();
        Bundle args = new Bundle();
        args.putString("FullName",fullnametv.getText().toString());
        args.putString("PayPal",paypaltv.getText().toString());
        args.putString("Email",emailtv.getText().toString());
        args.putString("Phone",phonetv.getText().toString());
        update.setArguments(args);
        update.show(getSupportFragmentManager(),"Update Profile");
    }

    @Override
    public void applyTexts(String paypal, String fullname, String phone, String email) {
        Map<String,String> userData = new HashMap<>();
        userData.put("Full Name",fullname);
        userData.put("Email",email);
        userData.put("PayPal",paypal);
        userData.put("Phone",phone);
        userData.put("rate",rate);
        fstore.collection("Users").document(userId).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Changes Saved", Toast.LENGTH_LONG).show();
            }
        });
    }
}
