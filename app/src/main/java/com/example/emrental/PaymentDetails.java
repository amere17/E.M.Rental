package com.example.emrental;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {

    TextView textID, textAmount, textStatus;
    RatingBar rb;
    Button submitBtn;
    String m_Owner;
    FirebaseFirestore fstore;
    User user;
    DocumentReference dr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_payment_details);
        fstore = FirebaseFirestore.getInstance();
        textID = (TextView) findViewById(R.id.textID);
        textAmount = (TextView) findViewById(R.id.textAmount);
        textStatus = (TextView) findViewById(R.id.textStatus);
        rb = findViewById(R.id.ratingBar);
        submitBtn = findViewById(R.id.SubmitBtn);

        // Get Intent
        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final float rateR = rb.getRating();
                if(rateR  < 0.5){
                    Toast.makeText(PaymentDetails.this, "Please Rate The Seller", Toast.LENGTH_SHORT).show();
                }
                else{
                    user = new User();
                    m_Owner = getIntent().getExtras().getString("Owner");
                    dr = fstore.collection("Users").document(m_Owner);
                    dr.addSnapshotListener(PaymentDetails.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            String curr = documentSnapshot.getString("rate");
                            float currRate = Float.parseFloat(curr);
                            currRate+=rateR;
                            if(!curr.equals("0")){
                                currRate*=0.5;
                            }
                            user.setName(documentSnapshot.getString("Full Name"));
                            user.setPaypal(documentSnapshot.getString("PayPal"));
                            user.setRate(String.valueOf(currRate));
                            user.setMail(documentSnapshot.getString("Email"));
                            user.setNumber(documentSnapshot.getString("Phone"));
                            update(user);
                        }
                    });

                    finish();
                }
            }
        });
    }
    private void update(User mUser){
        fstore = FirebaseFirestore.getInstance();
        dr = fstore.collection("Users").document(m_Owner);
        dr.update("Email",mUser.getMail(),"Full Name",mUser.getFullName(),
                "PayPal",mUser.getPaypal(),
                "Phone",mUser.getNumber(),"rate",mUser.getRate());
    }
    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            textID.setText(response.getString("id"));
            textStatus.setText(response.getString("state"));
            textAmount.setText("₪" + paymentAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
