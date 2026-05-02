package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderCheckoutActivity extends AppCompatActivity {

    private double subtotal, deliveryFee, totalAmount;
    private String userId, userAddress = "No address added";

    private TextView tvAddress, tvSubtotal, tvDelivery, tvTotal;
    private RadioGroup rgDeliveryType, rgPayment;
    private CheckBox cbTerms;
    private Button btnPlaceOrder;
    private ImageView ivBack;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_checkout);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            Toast.makeText(this, "Please login to proceed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get data from intent
        subtotal = getIntent().getDoubleExtra("subtotal", 0);
        deliveryFee = getIntent().getDoubleExtra("deliveryFee", 40);
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);

        // Initialize views
        ivBack = findViewById(R.id.ivBackOrderCheckout);
        tvAddress = findViewById(R.id.tvCheckoutAddress);
        tvSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvDelivery = findViewById(R.id.tvCheckoutDelivery);
        tvTotal = findViewById(R.id.tvCheckoutTotal);
        rgDeliveryType = findViewById(R.id.rgDeliveryType);
        rgPayment = findViewById(R.id.rgOrderPayment);
        cbTerms = findViewById(R.id.cbOrderTerms);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        ivBack.setOnClickListener(v -> finish());

        // Set initial values
        updateSummary();
        loadUserAddress();

        // Delivery type change listener
        rgDeliveryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPickup) {
                deliveryFee = 0;
            } else {
                deliveryFee = 40;
            }
            updateSummary();
        });

        btnPlaceOrder.setOnClickListener(v -> {
            if (rgOrderPaymentNotSelected()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            placeOrder();
        });
    }

    private boolean rgOrderPaymentNotSelected() {
        return rgPayment.getCheckedRadioButtonId() == -1;
    }

    private void updateSummary() {
        totalAmount = subtotal + deliveryFee;
        tvSubtotal.setText("₹" + (int) subtotal);
        tvDelivery.setText("₹" + (int) deliveryFee);
        tvTotal.setText("₹" + (int) totalAmount);
    }

    private void loadUserAddress() {
        if (userId == null) return;
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("defaultAddress")) {
                userAddress = documentSnapshot.getString("defaultAddress");
                tvAddress.setText(userAddress);
            } else {
                tvAddress.setText("No address added");
            }
        });
    }

    private void placeOrder() {
        String deliveryType = ((RadioButton) findViewById(rgDeliveryType.getCheckedRadioButtonId())).getText().toString();
        String paymentMethod = ((RadioButton) findViewById(rgPayment.getCheckedRadioButtonId())).getText().toString();

        Intent intent = new Intent(this, OrderPaymentSuccessActivity.class);
        intent.putExtra("subtotal", subtotal);
        intent.putExtra("deliveryFee", deliveryFee);
        intent.putExtra("totalAmount", totalAmount);
        intent.putExtra("deliveryType", deliveryType);
        intent.putExtra("address", userAddress);
        intent.putExtra("paymentMethod", paymentMethod);
        startActivity(intent);
    }
}