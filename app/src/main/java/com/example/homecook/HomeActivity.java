package com.example.homecook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.activities.CartActivity;
import com.example.homecook.activities.CookListActivity;
import com.example.homecook.activities.OrdersActivity;
import com.example.homecook.activities.ProfileActivity;
import com.example.homecook.activities.SearchActivity;
import com.example.homecook.activities.auth.LoginActivity;
import com.example.homecook.adapters.CookAdapter;
import com.example.homecook.adapters.DishAdapter;
import com.example.homecook.models.Cook;
import com.example.homecook.models.Dish;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvPopularDishes, rvNearbyCooks;
    private DishAdapter dishAdapter;
    private CookAdapter cookAdapter;
    private List<Dish> dishList;
    private List<Cook> cookList;
    private FirebaseFirestore db;
    private ImageView ivProfile;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            navigateToLogin();
            return;
        }

        db = FirebaseFirestore.getInstance();

        rvPopularDishes = findViewById(R.id.rvPopularDishes);
        rvNearbyCooks = findViewById(R.id.rvNearbyCooks);
        ivProfile = findViewById(R.id.ivProfile);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        dishList = new ArrayList<>();
        cookList = new ArrayList<>();

        // Setup RecyclerViews
        rvPopularDishes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dishAdapter = new DishAdapter(this, dishList);
        rvPopularDishes.setAdapter(dishAdapter);

        rvNearbyCooks.setLayoutManager(new LinearLayoutManager(this));
        cookAdapter = new CookAdapter(this, cookList);
        rvNearbyCooks.setAdapter(cookAdapter);

        // Profile Click
        if (ivProfile != null) {
            ivProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));
        }

        // Search Bar Click
        View searchBar = findViewById(R.id.etHomeSearch);
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));
        }

        // Bottom Nav setup
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) return true;
                
                Intent intent = null;
                if (id == R.id.nav_cooks) {
                    intent = new Intent(this, CookListActivity.class);
                } else if (id == R.id.nav_cart) {
                    intent = new Intent(this, CartActivity.class);
                } else if (id == R.id.nav_orders) {
                    intent = new Intent(this, OrdersActivity.class);
                } else if (id == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    return true;
                }
                return false;
            });
        }

        // Seed data if needed, then fetch
        seedDummyDataIfNeeded();

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                navigateToLogin();
            });
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void fetchDishes() {
        db.collection("dishes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                dishList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Dish dish = document.toObject(Dish.class);
                    if (dish != null) dishList.add(dish);
                }
                dishAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load dishes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCooks() {
        db.collection("cooks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                cookList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Cook cook = document.toObject(Cook.class);
                    if (cook != null) cookList.add(cook);
                }
                cookAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load cooks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seedDummyDataIfNeeded() {
        db.collection("dishes").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().isEmpty()) {
                    addDummyDishes();
                } else {
                    fetchDishes();
                }
            }
        });

        db.collection("cooks").limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                if (task.getResult().isEmpty()) {
                    addDummyCooks();
                } else {
                    fetchCooks();
                }
            }
        });
    }

    private void addDummyDishes() {
        String[] names = {"Aloo Paratha", "Paneer Roti", "Chhole Bhature", "Dal Tadka", "Veg Thali"};
        double[] prices = {125, 250, 150, 120, 180};
        String[] images = {"img_aloo_paratha", "img_paneer_roti", "img_chhole_bhature", "img_dal_tadka", "img_veg_thali"};

        for (int i = 0; i < names.length; i++) {
            String id = "dish_" + (i + 1);
            Dish dish = new Dish(id, "cook_1", names[i], prices[i], "North Indian", "Lunch", "Delicious " + names[i], "Ingredients", images[i], true);
            db.collection("dishes").document(id).set(dish);
        }
        fetchDishes();
    }

    private void addDummyCooks() {
        Cook[] dummyCooks = {
            new Cook("cook_1", "Priya Sharma", "North Indian Cuisine", "1.2 km", 4.9, 120, "cook_1", "About Priya", true, true),
            new Cook("cook_2", "Raj Patel", "Italian & Continental", "0.6 km", 4.8, 85, "cook_2", "About Raj", true, true),
            new Cook("cook_3", "Anita Verma", "Healthy Meals", "1.5 km", 4.7, 200, "cook_3", "About Anita", true, true)
        };

        for (Cook cook : dummyCooks) {
            db.collection("cooks").document(cook.getCookId()).set(cook);
        }
        fetchCooks();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
