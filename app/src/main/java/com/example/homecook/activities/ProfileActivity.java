package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.example.homecook.activities.auth.LoginActivity;
// These now reference the real, fully-implemented activities (not placeholders)
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone;
    private ImageView ivProfilePic;
    private LinearLayout btnEditProfile, btnSavedAddress, btnOrders, btnSubscription, 
            btnNotifications, btnHelpSupport, btnSettings, btnLogout;
    private BottomNavigationView bottomNavigationView;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        if (mAuth.getCurrentUser() != null) {
            userId = mAuth.getUid();
        } else {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Initialize Views
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvPhone = findViewById(R.id.tvProfilePhone);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSavedAddress = findViewById(R.id.btnSavedAddress);
        btnOrders = findViewById(R.id.btnOrders);
        btnSubscription = findViewById(R.id.btnSubscription);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnHelpSupport = findViewById(R.id.btnHelpSupport);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        loadUserData();

        // Menu Actions
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        btnSavedAddress.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SavedAddressActivity.class)));
        btnOrders.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, OrdersActivity.class)));
        btnSubscription.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MySubscriptionsActivity.class)));
        btnNotifications.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class)));
        btnHelpSupport.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, HelpSupportActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, SettingsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            navigateToLogin();
        });

        // Bottom Nav setup
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_profile) return true;
                
                Intent intent = null;
                if (id == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (id == R.id.nav_cooks) {
                    intent = new Intent(this, CookListActivity.class);
                } else if (id == R.id.nav_cart) {
                    intent = new Intent(this, CartActivity.class);
                } else if (id == R.id.nav_orders) {
                    intent = new Intent(this, OrdersActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        if (userId == null) return;

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String name = document.getString("name");
                    String email = document.getString("email");
                    String phone = document.getString("phone");

                    if (tvName != null) tvName.setText(name != null ? name : "No Name");
                    if (tvEmail != null) tvEmail.setText(email != null ? email : "No Email");
                    if (tvPhone != null) tvPhone.setText(phone != null ? phone : "No Phone");

                    // Load profile photo
                    if (ivProfilePic != null) {
                        int profileResId = getResources().getIdentifier("profile", "drawable", getPackageName());
                        if (profileResId != 0) {
                            ivProfilePic.setImageResource(profileResId);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "User profile not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
