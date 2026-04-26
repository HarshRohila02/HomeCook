package com.example.homecook.activities;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.SubscriptionPlanAdapter;
import com.example.homecook.models.SubscriptionPlan;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlansActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private SubscriptionPlanAdapter adapter;
    private List<SubscriptionPlan> planList;
    private String cookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_plans);

        cookId = getIntent().getStringExtra("cookId");

        ImageView ivBack = findViewById(R.id.ivBackSubs);
        ivBack.setOnClickListener(v -> finish());

        rvPlans = findViewById(R.id.rvSubscriptionPlans);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        planList = new ArrayList<>();
        loadPlans();

        adapter = new SubscriptionPlanAdapter(this, planList, cookId);
        rvPlans.setAdapter(adapter);
    }

    private void loadPlans() {
        String benefits = "• Access to cook profile\n• Order homemade meals\n• Flexible meal plan\n• Cancel anytime";
        
        planList.add(new SubscriptionPlan("3-Day Pack", 1000, 3, benefits));
        planList.add(new SubscriptionPlan("Weekly Pack", 1500, 7, benefits));
        planList.add(new SubscriptionPlan("Monthly Pack", 6000, 30, benefits));
    }
}