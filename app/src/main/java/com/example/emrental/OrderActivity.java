package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.min;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.m;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class OrderActivity extends AppCompatActivity {
    private static final int TAKE_IMAGE_CODE = 1000;
    DatabaseReference ref, ref2;
    DocumentReference dr, dr2;
    Button OrderBtn, StatusBtn,DeleteTool,ShareBtn;
    FirebaseFirestore fstore;
    TextView tName, tPrice, tLocation, tType, tOwner;
    Order order;
    CheckBox cb;
    ImageView tIV;
    public String userIdB;
    public String dealId;
    String toolId,id;
    public Date startDate;
    public Date endDate;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public String userIdA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_order);
        ref = FirebaseDatabase.getInstance().getReference().child("Deals");
        ref2 = FirebaseDatabase.getInstance().getReference().child("tools");
        StatusBtn = findViewById(R.id.StatusBtn);
        OrderBtn = findViewById(R.id.bOrder);
        tName = findViewById(R.id.tName);
        tPrice = findViewById(R.id.tPrice);
        tLocation = findViewById(R.id.tLocation);
        tType = findViewById(R.id.tType);
        tOwner = findViewById(R.id.tOwner);
        DeleteTool = findViewById(R.id.delToolbtn);
        cb= findViewById(R.id.termsCB);
        ShareBtn = findViewById(R.id.ShareBtn);
        tIV = findViewById(R.id.ToolIV);
        ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Share();
            }
        });
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    termsDialog();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        toolId = getIntent().getExtras().getString("ToolId");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        } else {
            userIdB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        if (userIdB == FirebaseAuth.getInstance().getCurrentUser().getUid()) {
            StatusBtn.setVisibility(View.INVISIBLE);
            DeleteTool.setVisibility(View.INVISIBLE);
        }
        fstore = FirebaseFirestore.getInstance();
        dr = fstore.collection("tools").document(toolId);
        dr.addSnapshotListener(OrderActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    tName.setText(documentSnapshot.getString("name"));
                    tPrice.setText(documentSnapshot.getString("price"));
                    tLocation.setText(documentSnapshot.getString("address"));
                    tType.setText(documentSnapshot.getString("type"));
                    userIdA = documentSnapshot.getString("userid");
                    dr2 = fstore.collection("Users").document(documentSnapshot.getString("userid"));
                    dr2.addSnapshotListener(OrderActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            String owner = tOwner.getText() + " " + documentSnapshot.getString("Full Name");
                            tOwner.setText(owner);
                        }
                    });
                    final StorageReference reference = FirebaseStorage.getInstance().getReference().
                            child("ToolImages").child(toolId+ ".jpeg");
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(OrderActivity.this).load(uri).into(tIV);
                        }
                    });
                    //String currUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    if (userIdA.equals(userIdB)) {
                        StatusBtn.setVisibility(View.VISIBLE);
                        OrderBtn.setVisibility(View.INVISIBLE);
                        DeleteTool.setVisibility(View.VISIBLE);
                        cb.setVisibility(View.INVISIBLE);
                    } else {
                        OrderBtn.setVisibility(View.VISIBLE);
                        StatusBtn.setVisibility(View.INVISIBLE);
                        DeleteTool.setVisibility(View.INVISIBLE);
                        cb.setVisibility(View.VISIBLE);
                        tIV.setEnabled(false);
                    }
                    if (documentSnapshot.getString("status").equals("0")) {
                        StatusBtn.setText("End");
                        OrderBtn.setText("InProgress");
                        OrderBtn.setClickable(false);
                    } else {
                        StatusBtn.setText("InProgress");
                        OrderBtn.setText("Order");
                        OrderBtn.setClickable(true);
                    }

                    if (documentSnapshot.getString("status").equals("1") && !userIdB.equals(userIdA)) {
                        updateStatus();
                    }
                }
            }
        });
        tIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handleImageClick(v);
                } catch (RuntimeException e) {

                }
            }
        });

        tOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderActivity.this, ProfileActivity.class);
                i.putExtra("UserId", userIdA);
                startActivity(i);
                finish();
            }
        });

        OrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(cb.isChecked()){
                Map<String, String> mToolList = new HashMap<>();
                mToolList.put("Owner", userIdA);
                mToolList.put("User", userIdB);
                mToolList.put("ToolId", toolId);
                mToolList.put("Status", "A");
                mToolList.put("start", "null");
                mToolList.put("end", "null");
                mToolList.put("totalPrice", "null");
                ref.push().setValue(mToolList);
                finish();
               }else{
                   Toast.makeText(OrderActivity.this, "You Must Read The Terms", Toast.LENGTH_LONG).show();
               }
            }
        });

        order = new Order();
        StatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            order = ds.getValue(Order.class);
                            if (order.getToolId().equals(toolId)) {
                                dealId = ds.getKey();
                                if (order.getStatus().equals("A")) {
                                    startDate = Calendar.getInstance().getTime();
                                    ref.child(ds.getKey()).child("Status").setValue("B");
                                    String timestamp = formatter.format(startDate);
                                    ref.child(dealId).child("start").setValue(timestamp);
                                    StatusBtn.setText("End");
                                    OrderBtn.setText("InProgress");
                                    dr.update("status", "0");
                                    OrderBtn.setClickable(false);
                                }
                                if (order.getStatus().equals("B")) {
                                    endDate = Calendar.getInstance().getTime();
                                    dr.update("status", "1");
                                    ref.child(ds.getKey()).child("Status").setValue("C");
                                    String timestamp = formatter.format(endDate);
                                    ref.child(ds.getKey()).child("end").setValue(timestamp);
                                    if (order.getToolId().equals(toolId.trim()) && userIdB.equals(order.getOwner().trim())) {
                                        updateStatus();
                                        Toast.makeText(OrderActivity.this,
                                                "Done! Payout will appear in your PayPal Account", Toast.LENGTH_LONG).show();
                                    }
                                }

                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        DeleteTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem();
                dr.delete();
                Intent i = new Intent(OrderActivity.this, HomeActivity.class);
                finish();
                startActivity(i);
            }
        });

    }

    void updateStatus() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    order = ds.getValue(Order.class);
                    if (userIdB.equals(order.getUser())
                            && (order.getStatus().trim().equals("C"))) {
                        try {
                            String mstr = getTotal(formatter.parse(order.getEnd()),
                                    formatter.parse(order.getStart()), tPrice.getText().toString().trim());
                            ref.child(ds.getKey()).child("totalPrice").setValue(mstr);
                            order.setTotalPrice(mstr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(OrderActivity.this, PaymentActivity.class);
                        i.putExtra("StartDate", order.getStart());
                        i.putExtra("EndDate", order.getEnd());
                        i.putExtra("OwnerTool", order.getOwner());
                        i.putExtra("Total", order.getTotalPrice());
                        ref.child(ds.getKey()).child("Status").setValue("D");
                        startActivity(i);
                        finish();
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getTotal(Date end, Date start, String price) {
        String str = null;
        double total;
        double mprice = Double.parseDouble(price);
        long diff = end.getTime() - start.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        double m = (double) minutes;
        double h = (double) hours;
        double d = (double) days;
        total = (m / 60 + h + d * 24) * mprice;
        str = String.format("%.2f", total);


        return str;
    }

    public String getDiffDates(long day, long hours, long minutes) {
        String str = day + ":" + hours + ":" + minutes;
        return str;
    }

    public void DeleteItem(){
        FirebaseDatabase.getInstance().getReference()
                .child("tools").child(toolId).removeValue();
    }

    public void termsDialog() throws IOException {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(OrderActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        adBuilder.setTitle("Terms & Conditions");
        File mFolder = new File(getFilesDir() + "/assets");
        File imgFile = new File(mFolder.getAbsolutePath() + "/terms.txt");
        adBuilder.setMessage(readFile(imgFile.getPath()));
        adBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.show();

    }

    private String readFile(String path) throws IOException {
        BufferedReader reader = null;
        String mLine,full="";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("terms.txt")));
            while ((mLine = reader.readLine()) != null) {
                full+=mLine;
                full+='\n';
            }

        } finally {
            reader.close();
        }
        return full;
    }

    private void Share(){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "E.M.Rental");
            String shareMessage= "\nLet me recommend you this Tool From E.M.Rental\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + OrderActivity.this +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    public void handleImageClick(View view) {
        Intent i1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (i1.resolveActivity(getPackageManager()) != null)
            startActivityForResult(i1, TAKE_IMAGE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            Bitmap b = (Bitmap) data.getExtras().get("data");
            tIV.setImageBitmap(b);
            handleUpload(b);
        }
    }

    private void handleUpload(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("ToolImages").child(toolId+ ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getDownloadUrl(reference);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(OrderActivity.this,"onSuccess ", Toast.LENGTH_LONG).show();
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
                Toast.makeText(OrderActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderActivity.this, "Tool Image Failed...", Toast.LENGTH_LONG).show();
            }
        });

    }

}
