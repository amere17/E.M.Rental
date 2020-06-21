package com.example.emrental;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

import com.bumptech.glide.Glide;
import com.example.emrental.SendNotificationPack.APIService;
import com.example.emrental.SendNotificationPack.Client;
import com.example.emrental.SendNotificationPack.Data;
import com.example.emrental.SendNotificationPack.MyResponse;
import com.example.emrental.SendNotificationPack.NotificationSender;
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
import com.squareup.okhttp.ResponseBody;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderActivity extends AppCompatActivity {
    private static final int TAKE_IMAGE_CODE = 1000;
    DatabaseReference ref, ref2;
    DocumentReference dr, dr2;
    Button OrderBtn, StatusBtn, DeleteTool, ShareBtn;
    FirebaseFirestore fstore;
    TextView tName, tPrice, tLocation, tType, tOwner, tTitle;
    Order order;
    CheckBox cb;
    ImageView tIV;
    public String userIdB;
    public String dealId;
    String toolId;
    public Date startDate;
    public Date endDate;
    private APIService apiService;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public String userIdA;
    Map<String, String> mToolList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
        cb = findViewById(R.id.termsCB);
        ShareBtn = findViewById(R.id.ShareBtn);
        tIV = findViewById(R.id.ToolIV);
        tTitle = findViewById(R.id.textView5);
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
                            String owner = documentSnapshot.getString("Full Name");
                            tOwner.setText(owner);
                        }
                    });
                    setView(documentSnapshot);
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {
                    createDeal();
                    showNotificationOngoing(getBaseContext(), "E.M.Rental", "20 Minutes To PickUp The Tool\n" +
                            "Warning: Rental Time > 10 Hours = Total Price *2");
                    send(mToolList.get("Owner"), "New Order");
                    getDiffDates();
                    StartNewActivity();
                } else {
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
                                    send(order.getUser(), "Please Pay For The Renter(click on Open Tool Page)");

                                }
                                StartNewActivity();
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
            }
        });

    }

    private void send(String id, final String msg) {
        FirebaseDatabase.getInstance().getReference().child("Tokens").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usertoken = dataSnapshot.getValue(String.class).trim();
                sendNotifications(usertoken, "E.M.Rental", msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                            && (order.getStatus().trim().equals("C"))
                            && order.getToolId().trim().equals(toolId.trim())) {
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
                        i.putExtra("OrderId", ds.getKey());
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
        double m = (double) minutes;
        double h = (double) hours;
        total = (m / 60 + h) * mprice;
        if (h > 10)
            total *= 2;
        str = String.format("%.2f", total);


        return str;
    }

    public void deleteDeal() {
        FirebaseDatabase.getInstance().getReference()
                .child("Deals").child(dealId).removeValue();
        dr.update("status", "1");
    }

    public void getDiffDates() {
        new CountDownTimer(1200000, 1000) {

            public void onTick(long millisUntilFinished) {
                Log.v("OrderActivity", "Seconds Remaining:" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                ref.orderByChild(toolId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot deal : dataSnapshot.getChildren()) {
                            Order order = deal.getValue(Order.class);
                            if (order.getStatus().equals("A")&&order.getToolId().equals(toolId)) {
                                dealId = deal.getKey();
                                deleteDeal();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }.start();

    }

    public void DeleteItem() {
        FirebaseDatabase.getInstance().getReference()
                .child("tools").child(toolId).removeValue();
        dr.delete();
        Intent i = new Intent(OrderActivity.this, HomeActivity.class);
        finish();
        startActivity(i);
    }

    public void termsDialog() throws IOException {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(OrderActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
        adBuilder.setTitle("Terms & Conditions");
        adBuilder.setMessage(readFile());
        adBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adBuilder.show();

    }

    private String readFile() throws IOException {
        BufferedReader reader = null;
        String mLine, full = "";
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("terms.txt")));
            while ((mLine = reader.readLine()) != null) {
                full += mLine;
                full += '\n';
            }

        } finally {
            reader.close();
        }
        return full;
    }

    private void Share() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "E.M.Rental");
            String shareMessage = "\nLet me recommend you this Tool From E.M.Rental\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + OrderActivity.this + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
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
                child("ToolImages").child(toolId + ".jpeg");
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
                Toast.makeText(OrderActivity.this, "onSuccess ", Toast.LENGTH_LONG).show();
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

    private void setView(DocumentSnapshot documentSnapshot) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference().
                child("ToolImages").child(toolId + ".jpeg");
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(OrderActivity.this).load(uri).into(tIV);
            }
        });
        if (userIdA.equals(userIdB)) {
            StatusBtn.setVisibility(View.VISIBLE);
            OrderBtn.setVisibility(View.INVISIBLE);
            DeleteTool.setVisibility(View.VISIBLE);
            tTitle.setText("Admin Page");
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
            OrderBtn.setText("Not Available, InProgress");
            OrderBtn.setClickable(false);
            StatusBtn.setClickable(true);
        } else if (documentSnapshot.getString("status").equals("2")) {
            OrderBtn.setText("Not Available, Waiting for PickUp");
            StatusBtn.setText("Tap Here To Start The Rental Time (New Order)");
            OrderBtn.setClickable(false);
            StatusBtn.setClickable(true);
        } else {
            StatusBtn.setText("No Orders");
            StatusBtn.setClickable(false);
            OrderBtn.setText("Order");
            OrderBtn.setClickable(true);
        }

        if (documentSnapshot.getString("status").equals("1") && !userIdB.equals(userIdA)) {
            updateStatus();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotificationOngoing(Context context, String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Name";
            String description = "Des";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.logo);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setLargeIcon(bitmapdraw.getBitmap())
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX); //Important for heads-up notification
        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, buildNotification);
    }

    private void createDeal() {
        mToolList.put("Owner", userIdA);
        mToolList.put("User", userIdB);
        mToolList.put("ToolId", toolId);
        mToolList.put("Status", "A");
        mToolList.put("start", "null");
        mToolList.put("end", "null");
        mToolList.put("totalPrice", "null");
        ref.push().setValue(mToolList);
        dr.update("status", "2");
    }

    private void StartNewActivity() {
        try {
            finish();
            Intent i = new Intent(OrderActivity.this, HomeActivity.class);
            startActivity(i);
        } catch (Exception e) {

        }
    }

    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message,userIdB,toolId,tName.getText().toString());
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(OrderActivity.this, "Failed ", Toast.LENGTH_LONG);
                    }else {
                        Toast.makeText(OrderActivity.this, "sent", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
