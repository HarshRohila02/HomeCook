package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "HomeCook";
    private String cookId;
    private FirebaseFirestore db;
    private ImageView ivHeader, ivBack;
    private TextView tvName, tvCuisine, tvRating, tvReviews, tvDistance, tvAbout, tvHygiene;
    private Button btnViewMenu, btnSubscription;
    private Button btnBreakfast, btnLunch, btnDinner;
    private RecyclerView rvSampleDishes;
    private SmallDishAdapter adapter;
    private List<Dish> allDishesForCook;  // All dishes loaded once
    private List<Dish> filteredDishes;     // Filtered subset shown in RecyclerView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        cookId = getIntent().getStringExtra("cookId");
        db = FirebaseFirestore.getInstance();

        if (cookId == null || cookId.isEmpty()) {
            Toast.makeText(this, "Error: Cook data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "CookProfileActivity: opened with cookId = '" + cookId + "'");

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

        btnBreakfast = findViewById(R.id.filterBreakfastProfile);
        btnLunch = findViewById(R.id.filterLunchProfile);
        btnDinner = findViewById(R.id.filterDinnerProfile);

        allDishesForCook = new ArrayList<>();
        filteredDishes = new ArrayList<>();
        rvSampleDishes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new SmallDishAdapter(this, filteredDishes);
        rvSampleDishes.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadCookDetails();

        // Load ALL dishes for this cook in ONE query, then filter client-side
        loadAllDishesForCook();

        btnBreakfast.setOnClickListener(v -> filterByMealType("Breakfast"));
        btnLunch.setOnClickListener(v -> filterByMealType("Lunch"));
        btnDinner.setOnClickListener(v -> filterByMealType("Dinner"));

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

                String imgName = cook.getImageName();
                if (imgName != null && !imgName.isEmpty()) {
                    int resId = getResources().getIdentifier(imgName, "drawable", getPackageName());
                    if (resId != 0) {
                        Glide.with(this).load(resId).placeholder(R.drawable.ic_launcher_background).into(ivHeader);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "CookProfileActivity: Failed to load cook details", e);
            Toast.makeText(this, "Failed to load cook details", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Load ALL dishes for this cook using a SINGLE whereEqualTo query (no composite index needed).
     * Then filter client-side by mealType.
     */
    private void loadAllDishesForCook() {
        Log.d(TAG, "CookProfileActivity: Loading ALL dishes for cookId='" + cookId + "'");

        db.collection("dishes")
                .whereEqualTo("cookId", cookId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allDishesForCook.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Dish dish = document.toObject(Dish.class);
                            if (dish != null) {
                                allDishesForCook.add(dish);
                                Log.d(TAG, "CookProfileActivity: Loaded dish '" + dish.getName() + "' (mealType=" + dish.getMealType() + ")");
                            }
                        }
                        Log.d(TAG, "CookProfileActivity: Total dishes for cook = " + allDishesForCook.size());

                        if (allDishesForCook.isEmpty()) {
                            Toast.makeText(this, "No dishes found for this cook yet. Go to Home first to load data.", Toast.LENGTH_LONG).show();
                        }

                        // Default: show Breakfast
                        filterByMealType("Breakfast");
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e(TAG, "CookProfileActivity: Query FAILED: " + error);
                        Toast.makeText(this, "Failed to load dishes: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Client-side filtering — no Firestore query needed, no composite index needed.
     */
    private void filterByMealType(String mealType) {
        updateButtonStyles(mealType);

        filteredDishes.clear();
        for (Dish dish : allDishesForCook) {
            if (dish.getMealType() != null && dish.getMealType().equalsIgnoreCase(mealType)) {
                filteredDishes.add(dish);
            }
        }
        adapter.notifyDataSetChanged();

        Log.d(TAG, "CookProfileActivity: Filter '" + mealType + "' → " + filteredDishes.size() + " dishes");

        if (filteredDishes.isEmpty() && !allDishesForCook.isEmpty()) {
            Toast.makeText(this, "No " + mealType + " items available", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateButtonStyles(String selectedType) {
        btnBreakfast.setBackgroundResource(selectedType.equals("Breakfast") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        btnLunch.setBackgroundResource(selectedType.equals("Lunch") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        btnDinner.setBackgroundResource(selectedType.equals("Dinner") ? R.drawable.bg_chip_selected : R.drawable.bg_chip_unselected);
        
        btnBreakfast.setTextColor(selectedType.equals("Breakfast") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
        btnLunch.setTextColor(selectedType.equals("Lunch") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
        btnDinner.setTextColor(selectedType.equals("Dinner") ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
    }
}