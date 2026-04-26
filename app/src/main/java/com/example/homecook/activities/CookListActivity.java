package com.example.homecook.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.CookListAdapter;
import com.example.homecook.models.Cook;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_list);

        db = FirebaseFirestore.getInstance();
        
        ivBack = findViewById(R.id.ivBack);
        etSearch = findViewById(R.id.etSearchCooks);
        rvCookList = findViewById(R.id.rvCookList);
        
        cookList = new ArrayList<>();
        filteredList = new ArrayList<>();
        
        rvCookList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CookListAdapter(this, filteredList);
        rvCookList.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

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

        // Filter Buttons
        findViewById(R.id.filterAll).setOnClickListener(v -> filterByCuisine("All"));
        findViewById(R.id.filterNorth).setOnClickListener(v -> filterByCuisine("North Indian"));
        findViewById(R.id.filterSouth).setOnClickListener(v -> filterByCuisine("South Indian"));
        findViewById(R.id.filterHealthy).setOnClickListener(v -> filterByCuisine("Healthy"));
        findViewById(R.id.filterItalian).setOnClickListener(v -> filterByCuisine("Italian"));
    }

    private void loadCooks() {
        db.collection("cooks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cookList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    cookList.add(document.toObject(Cook.class));
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
            if (cook.getName().toLowerCase().contains(text.toLowerCase()) || 
                cook.getCuisine().toLowerCase().contains(text.toLowerCase())) {
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
                if (cook.getCuisine().toLowerCase().contains(cuisine.toLowerCase())) {
                    filteredList.add(cook);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}