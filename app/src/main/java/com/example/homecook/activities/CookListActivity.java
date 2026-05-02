package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.example.homecook.adapters.CookListAdapter;
import com.example.homecook.models.Cook;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CookListActivity extends AppCompatActivity {

    private RecyclerView rvCookList;
    private CookListAdapter adapter;
    private List<Cook> cookList;
    private List<Cook> filteredList;
    private FirebaseFirestore db;
    private EditText etSearch;
    private ImageView ivBack;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_list);

        db = FirebaseFirestore.getInstance();
        
        ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearchCooks);
        rvCookList = findViewById(R.id.rvCookList);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        
        cookList = new ArrayList<>();
        filteredList = new ArrayList<>();
        
        rvCookList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CookListAdapter(this, filteredList);
        rvCookList.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        // Bottom Nav setup
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cooks);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_cooks) return true;
                
                Intent intent = null;
                if (id == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (id == R.id.nav_cart) {
                    intent = new Intent(this, CartActivity.class);
                } else if (id == R.id.nav_orders) {
                    intent = new Intent(this, OrdersActivity.class);
                } else if (id == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    // Do not finish() here if you want to keep the stack, 
                    // but usually for main tabs we want to clear or switch.
                    return true;
                }
                return false;
            });
        }

        // Fetch Cooks
        loadCooks();

        // Search logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter Buttons with visual toggle
        Button[] filterBtns = {
            findViewById(R.id.filterAll),
            findViewById(R.id.filterNorth),
            findViewById(R.id.filterSouth),
            findViewById(R.id.filterHealthy),
            findViewById(R.id.filterItalian)
        };
        String[] cuisineTypes = {"All", "North Indian", "South Indian", "Healthy", "Italian"};
        
        // Set initial "All" as selected
        filterBtns[0].setBackgroundResource(R.drawable.bg_chip_selected);
        filterBtns[0].setTextColor(getResources().getColor(R.color.primary));
        
        for (int i = 0; i < filterBtns.length; i++) {
            final int idx = i;
            filterBtns[i].setOnClickListener(v -> {
                filterByCuisine(cuisineTypes[idx]);
                for (Button btn : filterBtns) {
                    btn.setBackgroundResource(R.drawable.bg_chip_unselected);
                    btn.setTextColor(getResources().getColor(R.color.text_secondary));
                }
                filterBtns[idx].setBackgroundResource(R.drawable.bg_chip_selected);
                filterBtns[idx].setTextColor(getResources().getColor(R.color.primary));
            });
        }
    }

    private void loadCooks() {
        db.collection("cooks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                cookList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Cook cook = document.toObject(Cook.class);
                    if (cook != null) cookList.add(cook);
                }
                filteredList.clear();
                filteredList.addAll(cookList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (Cook cook : cookList) {
            String name = cook.getName() != null ? cook.getName() : "";
            String cuisine = cook.getCuisine() != null ? cook.getCuisine() : "";
            if (name.toLowerCase().contains(text.toLowerCase()) || 
                cuisine.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(cook);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void filterByCuisine(String cuisine) {
        filteredList.clear();
        if (cuisine.equals("All")) {
            filteredList.addAll(cookList);
        } else {
            for (Cook cook : cookList) {
                String cookCuisine = cook.getCuisine() != null ? cook.getCuisine() : "";
                if (cookCuisine.toLowerCase().contains(cuisine.toLowerCase())) {
                    filteredList.add(cook);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
