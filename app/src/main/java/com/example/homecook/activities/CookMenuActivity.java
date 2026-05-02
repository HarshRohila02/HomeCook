package com.example.homecook.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.MenuDishAdapter;
import com.example.homecook.models.Cook;
import com.example.homecook.models.Dish;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CookMenuActivity extends AppCompatActivity {

    private static final String TAG = "HomeCook";
    private String cookId;
    private FirebaseFirestore db;
    private RecyclerView rvMenu;
    private MenuDishAdapter adapter;
    private List<Dish> allDishes;      // All dishes loaded once
    private List<Dish> filteredList;    // Filtered subset shown in RecyclerView
    private TextView tvCookName;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_menu);

        cookId = getIntent().getStringExtra("cookId");
        db = FirebaseFirestore.getInstance();

        if (cookId == null || cookId.isEmpty()) {
            Toast.makeText(this, "Error: Cook data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "CookMenuActivity: opened with cookId = '" + cookId + "'");

        ivBack = findViewById(R.id.ivBackMenu);
        tvCookName = findViewById(R.id.tvCookNameMenu);
        rvMenu = findViewById(R.id.rvCookMenu);

        allDishes = new ArrayList<>();
        filteredList = new ArrayList<>();
        
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuDishAdapter(this, filteredList);
        rvMenu.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadCookInfo();
        loadAllMenuDishes();

        // Filter chips logic
        Button[] filterBtns = {
            findViewById(R.id.filterMenuAll),
            findViewById(R.id.filterMenuBreakfast),
            findViewById(R.id.filterMenuLunch),
            findViewById(R.id.filterMenuDinner)
        };
        String[] filterTypes = {"All", "Breakfast", "Lunch", "Dinner"};
        
        for (int i = 0; i < filterBtns.length; i++) {
            final int idx = i;
            filterBtns[i].setOnClickListener(v -> {
                filterMenu(filterTypes[idx]);
                // Update chip visual state
                for (Button btn : filterBtns) {
                    btn.setBackgroundResource(R.drawable.bg_chip_unselected);
                    btn.setTextColor(getResources().getColor(R.color.text_secondary));
                }
                filterBtns[idx].setBackgroundResource(R.drawable.bg_chip_selected);
                filterBtns[idx].setTextColor(getResources().getColor(R.color.primary));
            });
        }
    }

    private void loadCookInfo() {
        db.collection("cooks").document(cookId).get().addOnSuccessListener(doc -> {
            Cook cook = doc.toObject(Cook.class);
            if (cook != null) {
                tvCookName.setText("by " + cook.getName());
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "CookMenuActivity: Failed to load cook info", e);
        });
    }

    /**
     * Load ALL dishes for this cook using a single whereEqualTo (no composite index needed).
     * Then filter client-side.
     */
    private void loadAllMenuDishes() {
        Log.d(TAG, "CookMenuActivity: Loading ALL dishes for cookId='" + cookId + "'");

        db.collection("dishes")
                .whereEqualTo("cookId", cookId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allDishes.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Dish dish = document.toObject(Dish.class);
                            if (dish != null) {
                                allDishes.add(dish);
                                Log.d(TAG, "CookMenuActivity: Loaded '" + dish.getName() + "' (" + dish.getMealType() + ")");
                            }
                        }

                        Log.d(TAG, "CookMenuActivity: Total dishes loaded = " + allDishes.size());

                        // Show ALL by default
                        filterMenu("All");

                        if (allDishes.isEmpty()) {
                            Toast.makeText(this, "No dishes found. Go to Home screen first to load menu data.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e(TAG, "CookMenuActivity: Query FAILED: " + error);
                        Toast.makeText(this, "Failed to load menu: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Client-side filtering — no extra Firestore queries.
     */
    private void filterMenu(String type) {
        filteredList.clear();
        if (type.equals("All")) {
            filteredList.addAll(allDishes);
        } else {
            for (Dish dish : allDishes) {
                if (dish.getMealType() != null && dish.getMealType().equalsIgnoreCase(type)) {
                    filteredList.add(dish);
                }
            }
        }
        adapter.notifyDataSetChanged();

        Log.d(TAG, "CookMenuActivity: Filter '" + type + "' → " + filteredList.size() + " dishes");

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No " + type + " items available", Toast.LENGTH_SHORT).show();
        }
    }
}