package com.example.homecook;

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
import com.example.homecook.activities.CartActivity;
import com.example.homecook.activities.CookListActivity;
import com.example.homecook.activities.OrdersActivity;
import com.example.homecook.activities.ProfileActivity;
import com.example.homecook.activities.SavedAddressActivity;
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
import com.google.firebase.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeCook";
    private RecyclerView rvPopularDishes, rvNearbyCooks;
    private DishAdapter dishAdapter;
    private CookAdapter cookAdapter;
    private List<Dish> dishList;
    private List<Cook> cookList;
    private FirebaseFirestore db;
    private ImageView ivProfile;
    private TextView tvAddressLabel, tvAddressDetail;
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
        tvAddressLabel = findViewById(R.id.tvAddressLabel);
        tvAddressDetail = findViewById(R.id.tvAddressDetail);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Address click -> Saved Addresses
        View addressSection = findViewById(R.id.addressSection);
        if (addressSection != null) {
            addressSection.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, SavedAddressActivity.class)));
        }

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

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                navigateToLogin();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        seedDummyDataIfNeeded();
        loadDefaultAddress();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void loadDefaultAddress() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String label = doc.getString("defaultAddressLabel");
                        String address = doc.getString("defaultAddress");
                        if (tvAddressLabel != null && label != null) {
                            tvAddressLabel.setText(label);
                        }
                        if (tvAddressDetail != null && address != null) {
                            tvAddressDetail.setText(address);
                        }
                    }
                });
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
                Log.d(TAG, "fetchDishes: Loaded " + dishList.size() + " dishes");
                dishAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "fetchDishes: FAILED", task.getException());
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
                Log.d(TAG, "fetchCooks: Loaded " + cookList.size() + " cooks");
                cookAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "fetchCooks: FAILED", task.getException());
                Toast.makeText(this, "Failed to load cooks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seedDummyDataIfNeeded() {
        // Step 1: Check dishes count
        db.collection("dishes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                int count = task.getResult().size();
                Log.d(TAG, "seedCheck: Found " + count + " dishes in Firestore");
                if (count >= 27) {
                    // Data exists and is complete
                    fetchDishes();
                } else {
                    // Data is missing or incomplete — wipe and reseed using WriteBatch
                    Log.d(TAG, "seedCheck: Need to reseed. Deleting " + count + " stale dishes...");
                    wipeDishesAndReseed(task.getResult());
                }
            } else {
                Log.e(TAG, "seedCheck dishes FAILED", task.getException());
            }
        });

        // Step 2: Check cooks count
        db.collection("cooks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                int count = task.getResult().size();
                Log.d(TAG, "seedCheck: Found " + count + " cooks in Firestore");
                if (count >= 3) {
                    fetchCooks();
                } else {
                    Log.d(TAG, "seedCheck: Need to reseed cooks...");
                    wipeCooksAndReseed(task.getResult());
                }
            } else {
                Log.e(TAG, "seedCheck cooks FAILED", task.getException());
            }
        });
    }

    private void wipeDishesAndReseed(com.google.firebase.firestore.QuerySnapshot existingDocs) {
        // Step 1: Delete all existing dishes in a batch
        WriteBatch deleteBatch = db.batch();
        for (QueryDocumentSnapshot doc : existingDocs) {
            deleteBatch.delete(db.collection("dishes").document(doc.getId()));
        }
        deleteBatch.commit().addOnCompleteListener(deleteTask -> {
            if (deleteTask.isSuccessful()) {
                Log.d(TAG, "wipeDishes: Delete batch successful. Now seeding 27 dishes...");
                // Step 2: ONLY after deletes complete, write all new dishes in a batch
                seedAllDishes();
            } else {
                Log.e(TAG, "wipeDishes: Delete batch FAILED", deleteTask.getException());
                // Try seeding anyway
                seedAllDishes();
            }
        });
    }

    private void wipeCooksAndReseed(com.google.firebase.firestore.QuerySnapshot existingDocs) {
        WriteBatch deleteBatch = db.batch();
        for (QueryDocumentSnapshot doc : existingDocs) {
            deleteBatch.delete(db.collection("cooks").document(doc.getId()));
        }
        deleteBatch.commit().addOnCompleteListener(deleteTask -> {
            if (deleteTask.isSuccessful()) {
                Log.d(TAG, "wipeCooks: Delete batch successful. Now seeding 3 cooks...");
                seedAllCooks();
            } else {
                Log.e(TAG, "wipeCooks: Delete batch FAILED", deleteTask.getException());
                seedAllCooks();
            }
        });
    }

    private void seedAllDishes() {
        WriteBatch batch = db.batch();

        // ===== PRIYA SHARMA (cook_1) - North Indian =====
        batch.set(db.collection("dishes").document("dish_1"),  new Dish("dish_1",  "cook_1", "Aloo Paratha",       80, "North Indian", "Breakfast", "Homestyle stuffed paratha served with fresh curd and pickle",      "Flour, Potato, Spices, Curd",               "img_aloo_paratha",  true));
        batch.set(db.collection("dishes").document("dish_2"),  new Dish("dish_2",  "cook_1", "Poha",               50, "Healthy",      "Breakfast", "Light and fluffy Indori poha topped with sev and lemon",           "Flattened Rice, Peanuts, Onion",            "img_poha",          true));
        batch.set(db.collection("dishes").document("dish_3"),  new Dish("dish_3",  "cook_1", "Masala Omelette",    60, "Protein",      "Breakfast", "Spicy masala omelette with onion and green chilli",                "Eggs, Onion, Green Chilli",                 "img_omelette",      true));
        batch.set(db.collection("dishes").document("dish_4"),  new Dish("dish_4",  "cook_1", "Veg Thali",         150, "North Indian", "Lunch",     "Complete meal with 2 sabzi, dal, roti, rice, and salad",           "Various Vegetables, Lentils, Flour",        "img_veg_thali",     true));
        batch.set(db.collection("dishes").document("dish_5"),  new Dish("dish_5",  "cook_1", "Paneer Butter Masala", 180, "North Indian", "Lunch",  "Rich and creamy paneer in buttery tomato gravy",                   "Paneer, Tomato, Cream, Butter",             "img_paneer",        true));
        batch.set(db.collection("dishes").document("dish_6"),  new Dish("dish_6",  "cook_1", "Rajma Chawal",      120, "Comfort Food", "Lunch",     "Classic Punjabi rajma with steamed basmati rice",                  "Kidney Beans, Rice, Onion, Tomato",         "img_rajma",         true));
        batch.set(db.collection("dishes").document("dish_7"),  new Dish("dish_7",  "cook_1", "Dal Tadka",         100, "Healthy",      "Dinner",    "Yellow dal with aromatic ghee tadka and cumin",                    "Lentils, Ghee, Cumin, Garlic",              "img_dal_tadka",     true));
        batch.set(db.collection("dishes").document("dish_8"),  new Dish("dish_8",  "cook_1", "Chhole Bhature",    130, "North Indian", "Dinner",    "Spicy chickpea curry served with fluffy bhature",                  "Chickpeas, Flour, Spices",                  "img_chhole_bhature",true));
        batch.set(db.collection("dishes").document("dish_9"),  new Dish("dish_9",  "cook_1", "Mix Veg Curry",     110, "North Indian", "Dinner",    "Seasonal mixed vegetables in a mild spiced gravy",                 "Carrot, Peas, Potato, Beans",               "img_mix_veg",       true));

        // ===== RAJ PATEL (cook_2) - Gujarati & South Indian =====
        batch.set(db.collection("dishes").document("dish_10"), new Dish("dish_10", "cook_2", "Masala Dosa",        90, "South Indian", "Breakfast", "Crispy golden dosa with potato filling, sambar and chutney",      "Rice Batter, Potato, Mustard Seeds",        "img_dosa",          true));
        batch.set(db.collection("dishes").document("dish_11"), new Dish("dish_11", "cook_2", "Idli Sambar",        70, "South Indian", "Breakfast", "Soft steamed idlis with hot sambar and coconut chutney",           "Rice, Urad Dal, Lentils",                   "img_idli",          true));
        batch.set(db.collection("dishes").document("dish_12"), new Dish("dish_12", "cook_2", "Dhokla",             60, "Gujarati",     "Breakfast", "Fluffy steamed dhokla with green chutney and tadka",              "Gram Flour, Yogurt, Mustard Seeds",         "img_dhokla",        true));
        batch.set(db.collection("dishes").document("dish_13"), new Dish("dish_13", "cook_2", "Gujarati Thali",    160, "Gujarati",     "Lunch",     "Traditional thali with dal, kadhi, sabzi, rotli, rice and sweet",  "Various",                                   "img_guj_thali",     true));
        batch.set(db.collection("dishes").document("dish_14"), new Dish("dish_14", "cook_2", "Undhiyu",           140, "Gujarati",     "Lunch",     "Classic Gujarati mixed vegetable dish slow-cooked with spices",    "Surti Papdi, Brinjal, Potato, Methi",       "img_undhiyu",       true));
        batch.set(db.collection("dishes").document("dish_15"), new Dish("dish_15", "cook_2", "Medu Vada",          80, "South Indian", "Lunch",     "Crispy urad dal vada served with sambar and chutney",              "Urad Dal, Curry Leaves, Ginger",            "img_vada",          true));
        batch.set(db.collection("dishes").document("dish_16"), new Dish("dish_16", "cook_2", "Khichdi Kadhi",     110, "Comfort Food", "Dinner",    "Light moong dal khichdi with creamy Gujarati kadhi",               "Rice, Moong Dal, Curd, Besan",              "img_khichdi",       true));
        batch.set(db.collection("dishes").document("dish_17"), new Dish("dish_17", "cook_2", "Pav Bhaji",         100, "Street Food",  "Dinner",    "Buttery mashed vegetable bhaji with toasted pav buns",             "Mixed Vegetables, Butter, Pav",             "img_pav_bhaji",     true));
        batch.set(db.collection("dishes").document("dish_18"), new Dish("dish_18", "cook_2", "Bisibele Bath",     120, "South Indian", "Dinner",    "Spicy Karnataka-style rice with lentils and vegetables",           "Rice, Toor Dal, Tamarind, Vegetables",      "img_bisibele",      true));

        // ===== ANITA VERMA (cook_3) - Healthy Meals =====
        batch.set(db.collection("dishes").document("dish_19"), new Dish("dish_19", "cook_3", "Oats Upma",          70, "Healthy",      "Breakfast", "Nutritious oats cooked with vegetables and mild spices",           "Oats, Carrot, Peas, Mustard Seeds",         "img_oats",          true));
        batch.set(db.collection("dishes").document("dish_20"), new Dish("dish_20", "cook_3", "Sprouts Salad",      60, "Healthy",      "Breakfast", "Fresh moong sprouts tossed with lemon, onion and coriander",       "Moong Sprouts, Lemon, Onion",               "img_sprouts",       true));
        batch.set(db.collection("dishes").document("dish_21"), new Dish("dish_21", "cook_3", "Ragi Dosa",          80, "Healthy",      "Breakfast", "Iron-rich finger millet dosa with groundnut chutney",              "Ragi Flour, Rice Flour, Onion",             "img_ragi_dosa",     true));
        batch.set(db.collection("dishes").document("dish_22"), new Dish("dish_22", "cook_3", "Quinoa Bowl",       180, "Healthy",      "Lunch",     "Protein-packed quinoa with grilled veggies and hummus",            "Quinoa, Zucchini, Bell Pepper, Chickpeas",  "img_quinoa",        true));
        batch.set(db.collection("dishes").document("dish_23"), new Dish("dish_23", "cook_3", "Brown Rice Thali",  150, "Healthy",      "Lunch",     "Balanced meal with brown rice, dal, sabzi and raita",              "Brown Rice, Lentils, Vegetables, Curd",     "img_brown_rice",    true));
        batch.set(db.collection("dishes").document("dish_24"), new Dish("dish_24", "cook_3", "Palak Paneer",      160, "Healthy",      "Lunch",     "Creamy spinach curry with soft paneer cubes",                      "Spinach, Paneer, Cream, Garlic",            "img_palak_paneer",  true));
        batch.set(db.collection("dishes").document("dish_25"), new Dish("dish_25", "cook_3", "Grilled Chicken Salad", 200, "Protein",  "Dinner",    "Tender grilled chicken with fresh greens and vinaigrette",         "Chicken Breast, Lettuce, Tomato, Olive Oil","img_chicken_salad", true));
        batch.set(db.collection("dishes").document("dish_26"), new Dish("dish_26", "cook_3", "Mushroom Soup",      90, "Healthy",      "Dinner",    "Creamy mushroom soup with herbs and garlic bread",                 "Mushrooms, Cream, Garlic, Herbs",           "img_mushroom_soup", true));
        batch.set(db.collection("dishes").document("dish_27"), new Dish("dish_27", "cook_3", "Vegetable Stir Fry",120, "Healthy",      "Dinner",    "Crunchy seasonal vegetables tossed in soy and garlic sauce",       "Broccoli, Bell Pepper, Carrot, Soy Sauce",  "img_stir_fry",      true));

        // Commit all 27 dishes atomically, THEN fetch
        batch.commit().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "seedAllDishes: SUCCESS — All 27 dishes written to Firestore!");
            Toast.makeText(this, "Menu loaded successfully!", Toast.LENGTH_SHORT).show();
            fetchDishes();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "seedAllDishes: FAILED to write dishes", e);
            Toast.makeText(this, "Failed to seed dishes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void seedAllCooks() {
        WriteBatch batch = db.batch();
        batch.set(db.collection("cooks").document("cook_1"), new Cook("cook_1", "Priya Sharma",  "North Indian Cuisine",   "1.2 km", 4.9, 120, "cook_1", "Experienced home cook specializing in authentic North Indian flavors.", true, true));
        batch.set(db.collection("cooks").document("cook_2"), new Cook("cook_2", "Raj Patel",     "Gujarati & South Indian","0.6 km", 4.8, 85,  "cook_2", "Specialist in regional Indian delicacies.",                            true, true));
        batch.set(db.collection("cooks").document("cook_3"), new Cook("cook_3", "Anita Verma",   "Healthy Meals",          "1.5 km", 4.7, 200, "cook_3", "Focus on organic and balanced diet meals.",                            true, true));

        batch.commit().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "seedAllCooks: SUCCESS — All 3 cooks written!");
            fetchCooks();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "seedAllCooks: FAILED", e);
            Toast.makeText(this, "Failed to seed cooks: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
