package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.example.homecook.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderPaymentSuccessActivity extends AppCompatActivity {

    private String orderId, userId;
    private double subtotal, deliveryFee, totalAmount;
    private String deliveryType, address, paymentMethod;
    private FirebaseFirestore db;

    private TextView tvOrderId, tvTotal, tvPayment;
    private Button btnTrack, btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment_success);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        // Get data from intent
        subtotal = getIntent().getDoubleExtra("subtotal", 0);
        deliveryFee = getIntent().getDoubleExtra("deliveryFee", 0);
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        deliveryType = getIntent().getStringExtra("deliveryType");
        address = getIntent().getStringExtra("address");
        paymentMethod = getIntent().getStringExtra("paymentMethod");

        orderId = "ORD" + System.currentTimeMillis();

        // Initialize views
        tvOrderId = findViewById(R.id.tvOrderSuccessId);
        tvTotal = findViewById(R.id.tvOrderSuccessTotal);
        tvPayment = findViewById(R.id.tvOrderSuccessPayment);
        btnTrack = findViewById(R.id.btnTrackOrder);
        btnHome = findViewById(R.id.btnOrderGoHome);

        tvOrderId.setText(orderId);
        tvTotal.setText("₹" + (int) totalAmount);
        tvPayment.setText(paymentMethod);

        saveOrderAndClearCart();

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderTrackingActivity.class);
            intent.putExtra("orderId", orderId);
            startActivity(intent);
        });
    }

    private void saveOrderAndClearCart() {
        if (userId == null) return;

        // 1. Load cart items
        db.collection("cart").document(userId).collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> itemsList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            itemsList.add(document.getData());
                        }

                        // 2. Prepare Order Data
                        Map<String, Object> order = new HashMap<>();
                        order.put("orderId", orderId);
                        order.put("userId", userId);
                        order.put("items", itemsList);
                        order.put("subtotal", subtotal);
                        order.put("deliveryFee", deliveryFee);
                        order.put("totalAmount", totalAmount);
                        order.put("deliveryType", deliveryType);
                        order.put("address", address);
                        order.put("paymentMethod", paymentMethod);
                        order.put("status", "Preparing");
                        order.put("createdAt", new Date());
                        order.put("estimatedDelivery", "30 mins");

                        // 3. Save Order and Clear Cart using Batch
                        WriteBatch batch = db.batch();
                        batch.set(db.collection("orders").document(orderId), order);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            batch.delete(document.getReference());
                        }

                        batch.commit().addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Error placing order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}