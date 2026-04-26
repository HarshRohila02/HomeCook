package com.example.homecook.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HelpSupportActivity extends AppCompatActivity {

    private ImageView ivBackHelp;
    private Button btnContactSupport, btnRaiseTicket;
    private EditText etTicketTitle, etTicketDesc;
    private TextView tvTerms, tvPrivacy, tvAbout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ivBackHelp = findViewById(R.id.ivBackHelp);
        btnContactSupport = findViewById(R.id.btnContactSupport);
        btnRaiseTicket = findViewById(R.id.btnRaiseTicket);
        etTicketTitle = findViewById(R.id.etTicketTitle);
        etTicketDesc = findViewById(R.id.etTicketDesc);
        tvTerms = findViewById(R.id.tvTerms);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        tvAbout = findViewById(R.id.tvAbout);

        ivBackHelp.setOnClickListener(v -> finish());

        btnContactSupport.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@homecook.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        btnRaiseTicket.setOnClickListener(v -> raiseTicket());

        tvTerms.setOnClickListener(v -> Toast.makeText(this, "Terms & Conditions clicked", Toast.LENGTH_SHORT).show());
        tvPrivacy.setOnClickListener(v -> Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show());
        tvAbout.setOnClickListener(v -> Toast.makeText(this, "HomeCook v1.0.0", Toast.LENGTH_SHORT).show());
    }

    private void raiseTicket() {
        String title = etTicketTitle.getText().toString().trim();
        String desc = etTicketDesc.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "guest";
        String ticketId = UUID.randomUUID().toString();

        Map<String, Object> ticket = new HashMap<>();
        ticket.put("ticketId", ticketId);
        ticket.put("userId", userId);
        ticket.put("issueTitle", title);
        ticket.put("issueDescription", desc);
        ticket.put("status", "Open");
        ticket.put("createdAt", System.currentTimeMillis());

        db.collection("supportTickets").document(ticketId)
                .set(ticket)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(HelpSupportActivity.this, "Ticket raised successfully!", Toast.LENGTH_SHORT).show();
                    etTicketTitle.setText("");
                    etTicketDesc.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HelpSupportActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
