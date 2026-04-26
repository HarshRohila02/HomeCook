package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.NotificationAdapter;
import com.example.homecook.models.NotificationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private TextView tvNoNotifications;
    private ImageView ivBack;
    
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        rvNotifications = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        ivBack = findViewById(R.id.ivBackNotifications);

        ivBack.setOnClickListener(v -> finish());

        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(this, notificationList, this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        if (userId == null) return;

        db.collection("users").document(userId).collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        notificationList.clear();
                        if (task.getResult().isEmpty()) {
                            seedDummyNotifications();
                        } else {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                notificationList.add(doc.toObject(NotificationItem.class));
                            }
                            adapter.notifyDataSetChanged();
                            tvNoNotifications.setVisibility(View.GONE);
                        }
                    } else {
                        tvNoNotifications.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void seedDummyNotifications() {
        String[] titles = {"New Dish Available", "Subscription Reminder", "Offer Alert", "Review Reminder"};
        String[] messages = {
            "Chef Priya just added 'Butter Chicken' to her menu. Try it now!",
            "Your subscription with Chef Raj expires in 3 days. Renew to stay healthy!",
            "Get 20% off on your next 3 orders using code HOMECOOK20.",
            "How was your last meal from Anita? Please leave a review."
        };

        for (int i = 0; i < titles.length; i++) {
            String id = UUID.randomUUID().toString();
            NotificationItem item = new NotificationItem(
                    id,
                    titles[i],
                    messages[i],
                    System.currentTimeMillis() - (i * 3600000L), // Stagger times
                    false
            );
            db.collection("users").document(userId).collection("notifications")
                    .document(id).set(item);
            notificationList.add(item);
        }
        adapter.notifyDataSetChanged();
        tvNoNotifications.setVisibility(View.GONE);
    }

    @Override
    public void onNotificationClick(NotificationItem item) {
        if (!item.isRead()) {
            db.collection("users").document(userId).collection("notifications")
                    .document(item.getId())
                    .update("read", true)
                    .addOnSuccessListener(aVoid -> {
                        item.setRead(true);
                        adapter.notifyDataSetChanged();
                    });
        }
    }
}
