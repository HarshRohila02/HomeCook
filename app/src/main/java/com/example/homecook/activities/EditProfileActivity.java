package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etFoodPref, etAllergies, etHealthGoals;
    private Button btnSave;
    private ImageView ivBack;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        // Initialize Views
        etName = findViewById(R.id.etEditName);
        etEmail = findViewById(R.id.etEditEmail);
        etPhone = findViewById(R.id.etEditPhone);
        etFoodPref = findViewById(R.id.etEditFoodPref);
        etAllergies = findViewById(R.id.etEditAllergies);
        etHealthGoals = findViewById(R.id.etEditHealthGoals);
        btnSave = findViewById(R.id.btnSaveProfile);
        ivBack = findViewById(R.id.ivBackEdit);

        ivBack.setOnClickListener(v -> finish());

        loadUserData();

        btnSave.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadUserData() {
        if (userId == null) return;

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc != null && doc.exists()) {
                    etName.setText(doc.getString("name"));
                    etEmail.setText(doc.getString("email"));
                    etPhone.setText(doc.getString("phone"));
                    etFoodPref.setText(doc.getString("foodPreference"));
                    etAllergies.setText(doc.getString("allergies"));
                    etHealthGoals.setText(doc.getString("healthGoals"));
                }
            }
        });
    }

    private void saveProfileChanges() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String foodPref = etFoodPref.getText().toString().trim();
        String allergies = etAllergies.getText().toString().trim();
        String healthGoals = etHealthGoals.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("phone", phone);
        userUpdates.put("foodPreference", foodPref);
        userUpdates.put("allergies", allergies);
        userUpdates.put("healthGoals", healthGoals);

        db.collection("users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
