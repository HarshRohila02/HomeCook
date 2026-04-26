package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.WalletTransactionAdapter;
import com.example.homecook.models.WalletTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WalletActivity extends AppCompatActivity {

    private TextView tvBalance;
    private Button btnAddMoney;
    private ImageView ivBack;
    private RecyclerView rvTransactions;
    
    private WalletTransactionAdapter adapter;
    private List<WalletTransaction> transactionList;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private double currentBalance = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        tvBalance = findViewById(R.id.tvWalletBalance);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        ivBack = findViewById(R.id.ivBackWallet);
        rvTransactions = findViewById(R.id.rvWalletTransactions);

        ivBack.setOnClickListener(v -> finish());

        transactionList = new ArrayList<>();
        adapter = new WalletTransactionAdapter(this, transactionList);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(adapter);

        loadWalletData();

        btnAddMoney.setOnClickListener(v -> addDummyMoney());
    }

    private void loadWalletData() {
        if (userId == null) return;

        // 1. Load Balance
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    Double balance = doc.getDouble("walletBalance");
                    if (balance != null) {
                        currentBalance = balance;
                    } else {
                        currentBalance = 0.0;
                    }
                    tvBalance.setText("₹" + (int) currentBalance);
                }
            }
        });

        // 2. Load Transactions
        db.collection("users").document(userId).collection("walletTransactions")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        transactionList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            transactionList.add(doc.toObject(WalletTransaction.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void addDummyMoney() {
        double addAmount = 500.0;
        double newBalance = currentBalance + addAmount;

        // Create transaction
        String tid = UUID.randomUUID().toString();
        WalletTransaction transaction = new WalletTransaction(
                tid,
                "Added Money (Dummy)",
                addAmount,
                "CREDIT",
                System.currentTimeMillis()
        );

        // Update Balance and save transaction
        db.collection("users").document(userId).update("walletBalance", newBalance)
                .addOnSuccessListener(aVoid -> {
                    db.collection("users").document(userId).collection("walletTransactions")
                            .document(tid).set(transaction)
                            .addOnSuccessListener(aVoid2 -> {
                                Toast.makeText(this, "₹500 added to wallet", Toast.LENGTH_SHORT).show();
                                loadWalletData();
                            });
                });
    }
}
