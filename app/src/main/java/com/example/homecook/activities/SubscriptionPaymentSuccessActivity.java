package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SubscriptionPaymentSuccessActivity extends AppCompatActivity {

    private String planName, cookId, paymentMethod, transactionId;
    private double price, gst, totalAmount;
    private int durationDays;
    private String startDateStr, validUntilStr;
    private Date validUntilDate;

    private TextView tvTransactionId, tvPlanName, tvValidUntil, tvPrice, tvGST, tvTotal;
    private Button btnGoHome, btnViewProfile;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_payment_success);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get data from Intent
        planName = getIntent().getStringExtra("planName");
        price = getIntent().getDoubleExtra("price", 0);
        gst = getIntent().getDoubleExtra("gst", 0);
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        durationDays = getIntent().getIntExtra("durationDays", 0);
        cookId = getIntent().getStringExtra("cookId");
        paymentMethod = getIntent().getStringExtra("paymentMethod");

        // Generate Transaction ID
        transactionId = "TPN" + System.currentTimeMillis();

        // Calculate Dates
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        startDateStr = sdf.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, durationDays);
        validUntilDate = calendar.getTime();
        validUntilStr = sdf.format(validUntilDate);

        // Initialize Views
        tvTransactionId = findViewById(R.id.tvSuccessTransactionId);
        tvPlanName = findViewById(R.id.tvSuccessPlanName);
        tvValidUntil = findViewById(R.id.tvSuccessValidUntil);
        tvPrice = findViewById(R.id.tvSuccessPrice);
        tvGST = findViewById(R.id.tvSuccessGST);
        tvTotal = findViewById(R.id.tvSuccessTotalAmount);
        btnGoHome = findViewById(R.id.btnGoToHome);
        btnViewProfile = findViewById(R.id.btnViewCookProfile);

        // Set Data to UI
        tvTransactionId.setText(transactionId);
        tvPlanName.setText(planName);
        tvValidUntil.setText(validUntilStr);
        tvPrice.setText("₹" + (int) price);
        tvGST.setText("₹" + (int) gst);
        tvTotal.setText("₹" + (int) totalAmount);

        // Save to Firestore
        saveSubscription();

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnViewProfile.setOnClickListener(v -> {
            if (cookId != null && !cookId.isEmpty()) {
                Intent intent = new Intent(this, CookProfileActivity.class);
                intent.putExtra("cookId", cookId);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveSubscription() {
        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> subscription = new HashMap<>();
        subscription.put("userId", userId);
        subscription.put("cookId", cookId);
        subscription.put("planName", planName);
        subscription.put("price", price);
        subscription.put("gst", gst);
        subscription.put("totalAmount", totalAmount);
        subscription.put("durationDays", durationDays);
        subscription.put("paymentMethod", paymentMethod);
        subscription.put("transactionId", transactionId);
        subscription.put("startDate", new Date());
        subscription.put("validUntil", validUntilDate);
        subscription.put("active", true);

        db.collection("subscriptions")
                .document(transactionId)
                .set(subscription)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Subscription activated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}