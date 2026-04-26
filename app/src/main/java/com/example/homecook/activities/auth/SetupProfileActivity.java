package com.example.homecook.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SetupProfileActivity extends AppCompatActivity {

    private Button btnVeg, btnNonVeg, btnVegetarian, btnVegan, btnSave;
    private EditText etAllergies, etHealthGoals;
    private TextView tvSkip;
    private String selectedPreference = "Veg"; // Default
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        btnVeg = findViewById(R.id.btnVeg);
        btnNonVeg = findViewById(R.id.btnNonVeg);
        btnVegetarian = findViewById(R.id.btnVegetarian);
        btnVegan = findViewById(R.id.btnVegan);
        btnSave = findViewById(R.id.btnSave);
        etAllergies = findViewById(R.id.etAllergies);
        etHealthGoals = findViewById(R.id.etHealthGoals);
        tvSkip = findViewById(R.id.tvSkip);

        // Preference Selection
        btnVeg.setOnClickListener(v -> selectPreference("Veg", btnVeg));
        btnNonVeg.setOnClickListener(v -> selectPreference("Non-Veg", btnNonVeg));
        btnVegetarian.setOnClickListener(v -> selectPreference("Vegetarian", btnVegetarian));
        btnVegan.setOnClickListener(v -> selectPreference("Vegan", btnVegan));

        // Initial selection
        selectPreference("Veg", btnVeg);

        btnSave.setOnClickListener(v -> saveProfileData());
        tvSkip.setOnClickListener(v -> openHome());
    }

    private void selectPreference(String pref, Button btn) {
        selectedPreference = pref;
        
        // Reset all buttons to unselected
        btnVeg.setBackgroundResource(R.drawable.bg_chip_unselected);
        btnNonVeg.setBackgroundResource(R.drawable.bg_chip_unselected);
        btnVegetarian.setBackgroundResource(R.drawable.bg_chip_unselected);
        btnVegan.setBackgroundResource(R.drawable.bg_chip_unselected);
        
        // Highlight selected
        btn.setBackgroundResource(R.drawable.bg_chip_selected);
    }

    private void saveProfileData() {
        String allergies = etAllergies.getText().toString().trim();
        String healthGoals = etHealthGoals.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put("foodPreference", selectedPreference);
        data.put("allergies", allergies);
        data.put("healthGoals", healthGoals);
        data.put("profileSetupCompleted", true);

        db.collection("users").document(userId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                    openHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}