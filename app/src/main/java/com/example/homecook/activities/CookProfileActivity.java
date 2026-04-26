package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.adapters.SmallDishAdapter;
import com.example.homecook.models.Cook;
import com.example.homecook.models.Dish;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CookProfileActivity extends AppCompatActivity {

    private String cookId;
    private FirebaseFirestore db;
    private ImageView ivHeader, ivBack;
    private TextView tvName, tvCuisine, tvRating, tvReviews, tvDistance, tvAbout, tvHygiene;
    private Button btnViewMenu, btnSubscription;
    private RecyclerView rvSampleDishes;
    private SmallDishAdapter adapter;
    private List<Dish> dishList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        cookId = getIntent().getStringExtra("cookId");
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        ivHeader = findViewById(R.id.ivCookProfileHeader);
        ivBack = findViewById(R.id.ivBackProfile);
        tvName = findViewById(R.id.tvCookNameProfile);
        tvCuisine = findViewById(R.id.tvCuisineProfile);
        tvRating = findViewById(R.id.tvRatingProfile);
        tvReviews = findViewById(R.id.tvReviewsProfile);
        tvDistance = findViewById(R.id.tvDistanceProfile);
        tvAbout = findViewById(R.id.tvAboutCook);
        tvHygiene = findViewById(R.id.tvHygieneBadge);
        btnViewMenu = findViewById(R.id.btnViewFullMenu);
        btnSubscription = findViewById(R.id.btnGetSubscription);
        rvSampleDishes = findViewById(R.id.rvCookSampleDishes);

        dishList = new ArrayList<>();
        rvSampleDishes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new SmallDishAdapter(this, dishList);
        rvSampleDishes.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadCookDetails();
        loadSampleDishes();

        btnViewMenu.setOnClickListener(v -> {
             Intent intent = new Intent(this, CookMenuActivity.class);
             intent.putExtra("cookId", cookId);
             startActivity(intent);
        });

        btnSubscription.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubscriptionPlansActivity.class);
            intent.putExtra("cookId", cookId);
            startActivity(intent);
        });
    }

    private void loadCookDetails() {
        db.collection("cooks").document(cookId).get().addOnSuccessListener(documentSnapshot -> {
            Cook cook = documentSnapshot.toObject(Cook.class);
            if (cook != null) {
                tvName.setText(cook.getName());
                tvCuisine.setText(cook.getCuisine());
                tvRating.setText("★ " + cook.getRating());
                tvReviews.setText("(" + cook.getReviewsCount() + " reviews)");
                tvDistance.setText("📍 " + cook.getDistance());
                tvAbout.setText(cook.getAbout());
                tvHygiene.setVisibility(cook.isHygieneVerified() ? View.VISIBLE : View.GONE);

                int resId = getResources().getIdentifier(cook.getImageName(), "drawable", getPackageName());
                if (resId != 0) {
                    Glide.with(this).load(resId).placeholder(R.drawable.ic_launcher_background).into(ivHeader);
                }
            }
        });
    }

    private void loadSampleDishes() {
        db.collection("dishes")
                .whereEqualTo("cookId", cookId)
                .limit(3)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dishList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            dishList.add(document.toObject(Dish.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}