package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.MySubscriptionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class MySubscriptionsActivity extends AppCompatActivity {

    private RecyclerView rvMySubscriptions;
    private LinearLayout llEmptySubs;
    private MySubscriptionAdapter adapter;
    private List<DocumentSnapshot> subscriptionList;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscriptions);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getUid();
        } else {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageView ivBack = findViewById(R.id.ivBackMySubs);
        ivBack.setOnClickListener(v -> finish());

        rvMySubscriptions = findViewById(R.id.rvMySubscriptions);
        llEmptySubs = findViewById(R.id.llEmptySubs);

        rvMySubscriptions.setLayoutManager(new LinearLayoutManager(this));
        subscriptionList = new ArrayList<>();
        adapter = new MySubscriptionAdapter(this, subscriptionList);
        rvMySubscriptions.setAdapter(adapter);

        Button btnBrowse = findViewById(R.id.btnBrowseCooks);
        btnBrowse.setOnClickListener(v -> {
            startActivity(new Intent(this, CookListActivity.class));
            finish();
        });

        loadSubscriptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSubscriptions();
    }

    private void loadSubscriptions() {
        if (userId == null) return;

        db.collection("subscriptions")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subscriptionList.clear();
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        subscriptionList.addAll(queryDocumentSnapshots.getDocuments());
                        rvMySubscriptions.setVisibility(View.VISIBLE);
                        llEmptySubs.setVisibility(View.GONE);
                    } else {
                        rvMySubscriptions.setVisibility(View.GONE);
                        llEmptySubs.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load subscriptions", Toast.LENGTH_SHORT).show();
                    rvMySubscriptions.setVisibility(View.GONE);
                    llEmptySubs.setVisibility(View.VISIBLE);
                });
    }
}
