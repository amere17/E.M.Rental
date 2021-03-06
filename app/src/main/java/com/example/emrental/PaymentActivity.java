package com.example.emrental;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emrental.config.Config;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

/**
 * Payment Activity - based on PayPal ode
 */
public class PaymentActivity extends AppCompatActivity {

    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    Date CurrDate;
    FirebaseFirestore fstore;
    DocumentReference dr;
    DatabaseReference ref;
    TextView mOwner, mTotal, mStartDate, mEndDate, mCurrDate;
    Button mPay;
    String m_OrderId;
    String m_Owner;

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    float amount;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    /**
     * init members
     *
     * @param savedInstanceState saved Instance State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);
        ref = FirebaseDatabase.getInstance().getReference().child("Deals");
        fstore = FirebaseFirestore.getInstance();
        mOwner = (TextView) findViewById(R.id.ownerTool);
        mTotal = (TextView) findViewById(R.id.totalPrice);
        mStartDate = (TextView) findViewById(R.id.startDate);
        mEndDate = (TextView) findViewById(R.id.endDate);
        mCurrDate = (TextView) findViewById(R.id.currDate);
        mPay = findViewById(R.id.btnPayNow);
        final String m_Start = getIntent().getExtras().getString("StartDate");
        final String m_End = getIntent().getExtras().getString("EndDate");
        m_Owner = getIntent().getExtras().getString("OwnerTool");
        final String m_Total = getIntent().getExtras().getString("Total");
        m_OrderId = getIntent().getExtras().getString("OrderId");
        CurrDate = Calendar.getInstance().getTime();
        String currTimeStr = simpleDate.format(CurrDate);
        mTotal.setText("Total Price: " + m_Total);
        mStartDate.setText("Start Rental: " + m_Start);
        mEndDate.setText("End Rental: " + m_End);
        mCurrDate.setText("Current Time:" + currTimeStr);
        dr = fstore.collection("Users").document(m_Owner.trim());
        dr.addSnapshotListener(PaymentActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                mOwner.setText(documentSnapshot.getString("PayPal"));
            }
        });
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PayPalService.ACCOUNT_SERVICE,mOwner.getText().toString());
        startService(intent);
        amount = Float.parseFloat(m_Total);
        if (amount == 0) {
            mPay.setVisibility(View.INVISIBLE);
            ref.child(m_OrderId).child("Status").setValue("D");
            Toast.makeText(PaymentActivity.this, "Nothing To Pay", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PaymentActivity.this.finish();
                }
            }, 3000);
        }
        mPay.setOnClickListener(new View.OnClickListener() {
            /**
             * manage payment process with PayPal
             *
             * @param v view
             */
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

    }

    /**
     * paypal code
     */
    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)),
                "ILS", mOwner.getText().toString(), PayPalPayment.PAYMENT_INTENT_SALE);
        payPalPayment.payeeEmail(mOwner.getText().toString().trim());
        Intent intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

    }

    /**
     * confirmation info
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(com.paypal.android.sdk.payments.
                        PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        ref.child(m_OrderId).child("Status").setValue("D");
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("PaymentDetails", paymentDetails)
                                .putExtra("PaymentAmount", String.valueOf(amount))
                                .putExtra("Owner", m_Owner)
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }

}
