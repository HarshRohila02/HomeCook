package com.example.homecook.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
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

    private FirebaseFirestore db;
    private boolean isSearchingDishes = true;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "RecentSearches";
    private static final String KEY_RECENT = "recent_queries";

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

        findViewById(R.id.ivBackSearch).setOnClickListener(v -> finish());

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));

        loadRecentSearches();

        btnSearchDishes.setOnClickListener(v -> {
            isSearchingDishes = true;
            updateTabUI();
            performSearch(etSearch.getText().toString());
        });

        btnSearchCooks.setOnClickListener(v -> {
            isSearchingDishes = false;
            updateTabUI();
            performSearch(etSearch.getText().toString());
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
        for (int i = 0; i < cgPopular.getChildCount(); i++) {
            Chip chip = (Chip) cgPopular.getChildAt(i);
            chip.setOnClickListener(v -> {
                etSearch.setText(chip.getText());
                performSearch(chip.getText().toString());
            });
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

        String collection = isSearchingDishes ? "dishes" : "cooks";
        String field = isSearchingDishes ? "name" : "name"; // Assuming both have 'name'

        db.collection(collection)
                .whereGreaterThanOrEqualTo(field, query)
                .whereLessThanOrEqualTo(field, query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = queryDocumentSnapshots.size();
                    tvResultsCount.setText(count + " results found for '" + query + "'");
                    // In a real app, you'd set up an adapter here.
                    // For now, we'll show a toast if something is clicked.
                    if (count == 0) {
                        Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    tvResultsCount.setText("Search failed");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
