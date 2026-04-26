package com.example.homecook.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReferralActivity extends AppCompatActivity {

    private TextView tvReferralCode, btnCopyCode;
    private Button btnShareInvite;
    private ImageView ivBackReferral;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userId;
    private String referralCode = "HOMECOOK100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        tvReferralCode = findViewById(R.id.tvReferralCode);
        btnCopyCode = findViewById(R.id.btnCopyCode);
        btnShareInvite = findViewById(R.id.btnShareInvite);
        ivBackReferral = findViewById(R.id.ivBackReferral);

        ivBackReferral.setOnClickListener(v -> finish());

        loadUserAndGenerateCode();

        btnCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Referral Code", referralCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        btnShareInvite.setOnClickListener(v -> {
            String shareMessage = "Hey! Use my referral code " + referralCode + 
                    " to get amazing home-cooked meals on HomeCook app. Download now!";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "HomeCook Referral");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }

    private void loadUserAndGenerateCode() {
        if (userId == null) return;

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Check if code already exists
                    String existingCode = document.getString("referralCode");
                    if (existingCode != null && !existingCode.isEmpty()) {
                        referralCode = existingCode;
                    } else {
                        // Generate new code
                        String phone = document.getString("phone");
                        if (phone != null && phone.length() >= 4) {
                            referralCode = "HOMECOOK" + phone.substring(phone.length() - 4);
                        } else {
                            referralCode = "HOMECOOK100";
                        }
                        // Save to Firestore
                        db.collection("users").document(userId).update("referralCode", referralCode);
                    }
                    tvReferralCode.setText(referralCode);
                }
            }
        });
    }
}
