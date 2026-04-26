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

public class SubscriptionCheckoutActivity extends AppCompatActivity {

    private String planName, cookId;
    private double price, gst, totalAmount;
    private int durationDays;

    private TextView tvPlanName, tvDuration, tvPrice, tvGST, tvTotal;
    private RadioGroup rgPayment;
    private CheckBox cbTerms;
    private Button btnProceed;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_checkout);

        // Get data from intent
        planName = getIntent().getStringExtra("planName");
        price = getIntent().getDoubleExtra("price", 0);
        durationDays = getIntent().getIntExtra("durationDays", 0);
        cookId = getIntent().getStringExtra("cookId");

        // Calculate amounts
        gst = price * 0.18;
        totalAmount = price + gst;

        // Initialize views
        ivBack = findViewById(R.id.ivBackCheckout);
        tvPlanName = findViewById(R.id.tvCheckoutPlanName);
        tvDuration = findViewById(R.id.tvCheckoutDuration);
        tvPrice = findViewById(R.id.tvSummaryPrice);
        tvGST = findViewById(R.id.tvSummaryGST);
        tvTotal = findViewById(R.id.tvSummaryTotal);
        rgPayment = findViewById(R.id.rgPaymentMethods);
        cbTerms = findViewById(R.id.cbTerms);
        btnProceed = findViewById(R.id.btnProceedToPayment);

        // Set data to views
        tvPlanName.setText(planName);
        tvDuration.setText("Duration: " + durationDays + " Days");
        tvPrice.setText("₹" + (int) price);
        tvGST.setText("₹" + (int) gst);
        tvTotal.setText("₹" + (int) totalAmount);

        ivBack.setOnClickListener(v -> finish());

        btnProceed.setOnClickListener(v -> {
            if (rgPayment.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRb = findViewById(rgPayment.getCheckedRadioButtonId());
            String paymentMethod = selectedRb.getText().toString();

            // Proceed to success activity with all required data for Phase 4C
            Intent intent = new Intent(SubscriptionCheckoutActivity.this, SubscriptionPaymentSuccessActivity.class);
            intent.putExtra("planName", planName);
            intent.putExtra("price", price);
            intent.putExtra("gst", gst);
            intent.putExtra("totalAmount", totalAmount);
            intent.putExtra("durationDays", durationDays);
            intent.putExtra("cookId", cookId);
            intent.putExtra("paymentMethod", paymentMethod);
            startActivity(intent);
        });
    }
}