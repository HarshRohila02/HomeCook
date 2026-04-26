package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.OrderAdapter;
import com.example.homecook.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private String userId;

    private TextView tvFilterCurrent, tvFilterPast, tvEmpty;
    private View filterIndicator;
    private boolean showingCurrent = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        rvOrders = findViewById(R.id.rvOrders);
        tvFilterCurrent = findViewById(R.id.tvFilterCurrent);
        tvFilterPast = findViewById(R.id.tvFilterPast);
        tvEmpty = findViewById(R.id.tvEmptyOrders);
        filterIndicator = findViewById(R.id.filterIndicator);
        ImageView ivBack = findViewById(R.id.ivBackOrders);

        ivBack.setOnClickListener(v -> finish());

        orderList = new ArrayList<>();
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(adapter);

        tvFilterCurrent.setOnClickListener(v -> {
            showingCurrent = true;
            updateFilterUI();
            loadOrders();
        });

        tvFilterPast.setOnClickListener(v -> {
            showingCurrent = false;
            updateFilterUI();
            loadOrders();
        });

        loadOrders();
    }

    private void updateFilterUI() {
        if (showingCurrent) {
            tvFilterCurrent.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvFilterPast.setTextColor(getResources().getColor(android.R.color.darker_gray));
            // Move indicator logic could be added here if needed
        } else {
            tvFilterPast.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            tvFilterCurrent.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void loadOrders() {
        if (userId == null) return;

        Query query = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                orderList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Order order = document.toObject(Order.class);
                    if (showingCurrent) {
                        if (!"Delivered".equals(order.getStatus())) {
                            orderList.add(order);
                        }
                    } else {
                        if ("Delivered".equals(order.getStatus())) {
                            orderList.add(order);
                        }
                    }
                }
                
                if (orderList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}