package com.example.homecook.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderTrackingActivity extends AppCompatActivity {

    private String orderId;
    private FirebaseFirestore db;

    private TextView tvOrderId, tvEstimate, tvTotal;
    private TextView statusConfirmed, statusPreparing, statusOut, statusDelivered;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        db = FirebaseFirestore.getInstance();
        orderId = getIntent().getStringExtra("orderId");

        tvOrderId = findViewById(R.id.tvTrackingOrderId);
        tvEstimate = findViewById(R.id.tvTrackingEstimate);
        tvTotal = findViewById(R.id.tvTrackingTotal);

        statusConfirmed = findViewById(R.id.statusConfirmed);
        statusPreparing = findViewById(R.id.statusPreparing);
        statusOut = findViewById(R.id.statusOut);
        statusDelivered = findViewById(R.id.statusDelivered);

        btnBack = findViewById(R.id.btnBackToOrders);

        findViewById(R.id.ivBackTracking).setOnClickListener(v -> finish());

        if (orderId != null) {
            tvOrderId.setText("Order ID: " + orderId);
            loadOrderDetails();
        }

        btnBack.setOnClickListener(v -> {
            // OrdersActivity will be implemented in Phase 5E
            try {
                Intent intent = new Intent(this, Class.forName("com.example.homecook.activities.OrdersActivity"));
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                Toast.makeText(this, "Orders history coming soon!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String status = documentSnapshot.getString("status");
                String estimate = documentSnapshot.getString("estimatedDelivery");
                double total = documentSnapshot.getDouble("totalAmount");

                tvEstimate.setText("Estimated Delivery: " + estimate);
                tvTotal.setText("Total Amount: ₹" + (int) total);

                updateTimeline(status);
            }
        });
    }

    private void updateTimeline(String status) {
        // Reset colors
        int gray = Color.parseColor("#CCCCCC");
        int orange = Color.parseColor("#FF9800");

        statusConfirmed.setTextColor(gray);
        statusPreparing.setTextColor(gray);
        statusOut.setTextColor(gray);
        statusDelivered.setTextColor(gray);

        // Highlight based on current status
        if (status != null) {
            statusConfirmed.setTextColor(orange); // Confirmed is always first
            if (status.equals("Preparing")) {
                statusPreparing.setTextColor(orange);
            } else if (status.equals("Out for Delivery")) {
                statusPreparing.setTextColor(orange);
                statusOut.setTextColor(orange);
            } else if (status.equals("Delivered")) {
                statusPreparing.setTextColor(orange);
                statusOut.setTextColor(orange);
                statusDelivered.setTextColor(orange);
            }
        }
    }
}