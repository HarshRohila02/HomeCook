package com.example.homecook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.SearchCookAdapter;
import com.example.homecook.adapters.SearchDishAdapter;
import com.example.homecook.models.Cook;
import com.example.homecook.models.Dish;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearchDishes, btnSearchCooks;
    private ChipGroup cgRecentSearches;
    private RecyclerView rvSearchResults;
    private TextView tvResultsCount;
    private View layoutRecentSearches;
    private LinearLayout llNoResults;

    private FirebaseFirestore db;
    private boolean isSearchingDishes = true;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "RecentSearches";
    private static final String KEY_RECENT = "recent_queries";

    private List<Dish> dishResults;
    private List<Cook> cookResults;
    private SearchDishAdapter dishAdapter;
    private SearchCookAdapter cookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        etSearch = findViewById(R.id.etSearch);
        btnSearchDishes = findViewById(R.id.btnSearchDishes);
        btnSearchCooks = findViewById(R.id.btnSearchCooks);
        cgRecentSearches = findViewById(R.id.cgRecentSearches);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        layoutRecentSearches = findViewById(R.id.layoutRecentSearches);
        llNoResults = findViewById(R.id.llNoResults);

        findViewById(R.id.ivBackSearch).setOnClickListener(v -> finish());

        // Initialize result lists and adapters
        dishResults = new ArrayList<>();
        cookResults = new ArrayList<>();
        dishAdapter = new SearchDishAdapter(this, dishResults);
        cookAdapter = new SearchCookAdapter(this, cookResults);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(dishAdapter); // default to dishes

        loadRecentSearches();

        btnSearchDishes.setOnClickListener(v -> {
            isSearchingDishes = true;
            updateTabUI();
            performSearch(etSearch.getText().toString().trim());
        });

        btnSearchCooks.setOnClickListener(v -> {
            isSearchingDishes = false;
            updateTabUI();
            performSearch(etSearch.getText().toString().trim());
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = etSearch.getText().toString().trim();
                if (!TextUtils.isEmpty(query)) {
                    saveRecentSearch(query);
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        // Popular search chips
        ChipGroup cgPopular = findViewById(R.id.cgPopularSearches);
        if (cgPopular != null) {
            for (int i = 0; i < cgPopular.getChildCount(); i++) {
                View child = cgPopular.getChildAt(i);
                if (child instanceof Chip) {
                    Chip chip = (Chip) child;
                    chip.setOnClickListener(v -> {
                        etSearch.setText(chip.getText());
                        saveRecentSearch(chip.getText().toString());
                        performSearch(chip.getText().toString());
                    });
                }
            }
        }

        // Handle incoming category from HomeActivity
        String category = getIntent().getStringExtra("category");
        if (category != null && !category.isEmpty()) {
            etSearch.setText(category);
            performSearch(category);
        }
    }

    private void updateTabUI() {
        if (isSearchingDishes) {
            btnSearchDishes.setBackgroundResource(R.drawable.btn_rounded);
            btnSearchDishes.setTextColor(getResources().getColor(android.R.color.white));
            btnSearchCooks.setBackgroundResource(R.drawable.bg_button_outline);
            btnSearchCooks.setTextColor(getResources().getColor(R.color.primary));
        } else {
            btnSearchCooks.setBackgroundResource(R.drawable.btn_rounded);
            btnSearchCooks.setTextColor(getResources().getColor(android.R.color.white));
            btnSearchDishes.setBackgroundResource(R.drawable.bg_button_outline);
            btnSearchDishes.setTextColor(getResources().getColor(R.color.primary));
        }
    }

    private void performSearch(String query) {
        if (TextUtils.isEmpty(query)) return;

        layoutRecentSearches.setVisibility(View.GONE);
        tvResultsCount.setVisibility(View.VISIBLE);
        tvResultsCount.setText("Searching...");
        rvSearchResults.setVisibility(View.GONE);
        if (llNoResults != null) llNoResults.setVisibility(View.GONE);

        String queryLower = query.toLowerCase();

        if (isSearchingDishes) {
            searchDishes(queryLower, query);
        } else {
            searchCooks(queryLower, query);
        }
    }

    private void searchDishes(String queryLower, String originalQuery) {
        rvSearchResults.setAdapter(dishAdapter);
        
        db.collection("dishes").get().addOnSuccessListener(querySnapshot -> {
            dishResults.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Dish dish = doc.toObject(Dish.class);
                if (dish != null && matchesDish(dish, queryLower)) {
                    dishResults.add(dish);
                }
            }
            dishAdapter.notifyDataSetChanged();
            showResults(dishResults.size(), originalQuery);
        }).addOnFailureListener(e -> {
            tvResultsCount.setText("Search failed");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void searchCooks(String queryLower, String originalQuery) {
        rvSearchResults.setAdapter(cookAdapter);
        
        db.collection("cooks").get().addOnSuccessListener(querySnapshot -> {
            cookResults.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                Cook cook = doc.toObject(Cook.class);
                if (cook != null && matchesCook(cook, queryLower)) {
                    cookResults.add(cook);
                }
            }
            cookAdapter.notifyDataSetChanged();
            showResults(cookResults.size(), originalQuery);
        }).addOnFailureListener(e -> {
            tvResultsCount.setText("Search failed");
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean matchesDish(Dish dish, String query) {
        String name = dish.getName() != null ? dish.getName().toLowerCase() : "";
        String category = dish.getCategory() != null ? dish.getCategory().toLowerCase() : "";
        String mealType = dish.getMealType() != null ? dish.getMealType().toLowerCase() : "";
        String desc = dish.getDescription() != null ? dish.getDescription().toLowerCase() : "";
        return name.contains(query) || category.contains(query) || mealType.contains(query) || desc.contains(query);
    }

    private boolean matchesCook(Cook cook, String query) {
        String name = cook.getName() != null ? cook.getName().toLowerCase() : "";
        String cuisine = cook.getCuisine() != null ? cook.getCuisine().toLowerCase() : "";
        return name.contains(query) || cuisine.contains(query);
    }

    private void showResults(int count, String query) {
        if (count > 0) {
            tvResultsCount.setText(count + " result" + (count > 1 ? "s" : "") + " found for '" + query + "'");
            rvSearchResults.setVisibility(View.VISIBLE);
            if (llNoResults != null) llNoResults.setVisibility(View.GONE);
        } else {
            tvResultsCount.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.GONE);
            if (llNoResults != null) llNoResults.setVisibility(View.VISIBLE);
        }
    }

    private void saveRecentSearch(String query) {
        Set<String> recent = new HashSet<>(sharedPreferences.getStringSet(KEY_RECENT, new HashSet<>()));
        recent.add(query);
        sharedPreferences.edit().putStringSet(KEY_RECENT, recent).apply();
        loadRecentSearches();
    }

    private void loadRecentSearches() {
        cgRecentSearches.removeAllViews();
        Set<String> recent = sharedPreferences.getStringSet(KEY_RECENT, new HashSet<>());
        for (String s : recent) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setClickable(true);
            chip.setOnClickListener(v -> {
                etSearch.setText(s);
                performSearch(s);
            });
            cgRecentSearches.addView(chip);
        }
    }
}
