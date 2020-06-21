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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ProfileActivity extends AppCompatActivity implements UpdateProfile.dialogListner {

    //----------------------- Variables & Objects -------------------
    TextView phonetv, emailtv, fullnametv, paypaltv, ratetv, dealtv;
    Button logoutbtn, edit, editProfileImage;
    ListView dealslv, toolslv;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    ImageView pIV;
    DatabaseReference ref, ref2;
    String userId;
    String rate;
    ArrayList<String> mArraylist = new ArrayList<>();
    ArrayList<String> mArraylist2 = new ArrayList<>();
    Tool item;
    Order order;
    final int TAKE_IMAGE_CODE = 100;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference toolsList = db.collection("tools");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        Bundle extraStr = getIntent().getExtras();
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String mString = "UserId";
        //-----------------  Attaching objects with XML file --------------
        phonetv = findViewById(R.id.phone);
        emailtv = findViewById(R.id.email);
        fullnametv = findViewById(R.id.fullname);
        paypaltv = findViewById(R.id.paypal);
        logoutbtn = findViewById(R.id.logout);
        dealslv = findViewById(R.id.dealsl);
        toolslv = findViewById(R.id.toolsl);
        ratetv = findViewById(R.id.rateResult);
        edit = findViewById(R.id.editProfile);
        editProfileImage = findViewById(R.id.editprofileimage);
        pIV = (ImageView) findViewById(R.id.profileIV);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tools");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Deals");
        tab1.setIndicator("Tools");
        tab1.setContent(R.id.Tools);
        tab2.setIndicator("Deals");
        tab2.setContent(R.id.Deals);
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        //---------------- get firebase data for current user --------------
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        pIV.setImageResource(R.drawable.logo_green);

        if (extraStr == null || extraStr.getString(mString).equals(fAuth.getCurrentUser().getUid().trim())) {
            try {
                userId = fAuth.getCurrentUser().getUid();
            } catch (Exception e) {

            }
        } else {
            userId = extraStr.getString(mString);
            logoutbtn.setVisibility(View.INVISIBLE);
            dealslv.setVisibility(View.INVISIBLE);
            edit.setVisibility(View.INVISIBLE);
            editProfileImage.setVisibility(View.INVISIBLE);
            tabHost.getTabWidget().removeView(tabHost.getTabWidget().getChildTabViewAt(1));
            pIV.setEnabled(false);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Update Your profile Image By Clicking on it", Toast.LENGTH_LONG).show();

                openDialog();
            }


        });
        pIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handleImageClick(v);
                } catch (RuntimeException e) {

                }
            }
        });
        //------------ get all the data for the current user to display in the profile ------
        if (fAuth.getCurrentUser() == null) {
            finish();
        } else {
            DocumentReference dr = fstore.collection("Users").document(userId);
            dr.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    try {
                        phonetv.setText(documentSnapshot.getString("Phone"));
                        fullnametv.setText(documentSnapshot.getString("Full Name"));
                        emailtv.setText(documentSnapshot.getString("Email"));
                        paypaltv.setText(documentSnapshot.getString("PayPal"));
                        rate = documentSnapshot.getString("rate");
                        ratetv.setText("Rate: " + rate + "/5");
                        final StorageReference reference;
                        reference = FirebaseStorage.getInstance().getReference().
                                child("ProfileImages").child(userId + ".jpeg");

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (reference.getDownloadUrl() != null)
                                    Glide.with(ProfileActivity.this).load(uri).into(pIV);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    } catch (Exception t) {
                        finish();
                    }

                }
            });
        }
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
                    Map<String, Object> d = documentSnapshot.getData();
                    if (d.get("userid").equals(userId)) {
                        vec.addElement(documentSnapshot.getId());
                        mArraylist.add(d.get("name") + "\n" + "Price: " + d.get("price") + " ₪/hr\n" + "Type: " + d.get("type") + "\n" + "Address: " + d.get("address"));
                    }
                }
                toolslv.setAdapter(itemArrayAdapter);
            }
        });
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

        final Vector vec2 = new Vector<>();
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    order = ds.getValue(Order.class);
                    if (order.getOwner().equals(userId) || order.getUser().equals(userId)) {
                        mArraylist2.add("Total Price: " + order.getTotalPrice() + " ₪\n" + "Date: " + order.getStart() +
                                "\n" + "Status: " + CheckStatus(order.getStatus()));
                        vec2.addElement(ds.getKey());
                    }

                }
                dealslv.setAdapter(dealArrayAdapter);
                dealslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(ProfileActivity.this, DealDisplay.class);
                        i.putExtra("DealId", vec2.elementAt(position).toString());
                        startActivity(i);
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

                FirebaseDatabase.getInstance().getReference()
                        .child("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    //closing this activity
                    //starting login activity
                    Intent intToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intToLogin);
                }

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

    private String CheckStatus(String status) {
        if (status.equals("C"))
            return "Payment Needed";
        else if (status.equals("D"))
            return "Payment Confirmed";
        return "in Progress";
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
        args.putString("FullName", fullnametv.getText().toString());
        args.putString("PayPal", paypaltv.getText().toString());
        args.putString("Email", emailtv.getText().toString());
        args.putString("Phone", phonetv.getText().toString());
        update.setArguments(args);
        update.show(getSupportFragmentManager(), "Update profile");
    }

    @Override
    public void applyTexts(String paypal, String fullname, String phone) {
        Map<String, String> userData = new HashMap<>();
        userData.put("Full Name", fullname);
        userData.put("PayPal", paypal);
        userData.put("Phone", phone);
        userData.put("Email", emailtv.getText().toString());
        if (rate.equals("0"))
            userData.put("rate", "0");
        else
            userData.put("rate", rate);
        fstore.collection("Users").document(userId).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Changes Saved", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void handleImageClick(View view) {
        Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (i1.resolveActivity(getPackageManager()) != null)
            startActivityForResult(i1, TAKE_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            pIV.setImageBitmap(b);
            handleUpload(b);
        }
    }

    private void handleUpload(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("ProfileImages").child(userId + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(ProfileActivity.this, "onSuccess ", Toast.LENGTH_LONG).show();
                setUserProfileUrl(uri);
            }
        });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "profile Image Failed...", Toast.LENGTH_LONG).show();
            }
        });

    }
}
