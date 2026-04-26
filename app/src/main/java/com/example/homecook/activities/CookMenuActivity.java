package com.example.homecook.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
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

    private String cookId;
    private FirebaseFirestore db;
    private RecyclerView rvMenu;
    private MenuDishAdapter adapter;
    private List<Dish> dishList;
    private List<Dish> filteredList;
    private TextView tvCookName;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_menu);

        cookId = getIntent().getStringExtra("cookId");
        db = FirebaseFirestore.getInstance();

        ivBack = findViewById(R.id.ivBackMenu);
        tvCookName = findViewById(R.id.tvCookNameMenu);
        rvMenu = findViewById(R.id.rvCookMenu);

        dishList = new ArrayList<>();
        filteredList = new ArrayList<>();
        
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuDishAdapter(this, filteredList);
        rvMenu.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadCookInfo();
        loadMenuDishes();

        // Filter chips logic
        findViewById(R.id.filterMenuAll).setOnClickListener(v -> filterMenu("All"));
        findViewById(R.id.filterMenuBreakfast).setOnClickListener(v -> filterMenu("Breakfast"));
        findViewById(R.id.filterMenuLunch).setOnClickListener(v -> filterMenu("Lunch"));
        findViewById(R.id.filterMenuDinner).setOnClickListener(v -> filterMenu("Dinner"));
    }

    private void loadCookInfo() {
        db.collection("cooks").document(cookId).get().addOnSuccessListener(doc -> {
            Cook cook = doc.toObject(Cook.class);
            if (cook != null) {
                tvCookName.setText("by " + cook.getName());
            }
        });
    }

    private void loadMenuDishes() {
        db.collection("dishes").whereEqualTo("cookId", cookId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dishList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    dishList.add(document.toObject(Dish.class));
                }
                filteredList.clear();
                filteredList.addAll(dishList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void filterMenu(String type) {
        filteredList.clear();
        if (type.equals("All")) {
            filteredList.addAll(dishList);
        } else {
            for (Dish dish : dishList) {
                if (dish.getMealType().equalsIgnoreCase(type)) {
                    filteredList.add(dish);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}