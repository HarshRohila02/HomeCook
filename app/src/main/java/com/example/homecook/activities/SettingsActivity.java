package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.example.homecook.R;
import com.example.homecook.activities.auth.LoginActivity;
import com.example.homecook.activities.auth.SetupProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    private RelativeLayout rlLanguage;
    private TextView tvChangePassword, tvManagePreferences, tvCurrentLanguage;
    private SwitchCompat switchNotifications, switchTheme;
    private Button btnSettingsLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.ivBackSettings).setOnClickListener(v -> finish());

        tvChangePassword = findViewById(R.id.tvChangePassword);
        tvManagePreferences = findViewById(R.id.tvManagePreferences);
        rlLanguage = findViewById(R.id.rlLanguage);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchTheme = findViewById(R.id.switchTheme);
        btnSettingsLogout = findViewById(R.id.btnSettingsLogout);

        // Change Password
        tvChangePassword.setOnClickListener(v -> {
            String email = mAuth.getCurrentUser().getEmail();
            if (email != null) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Reset email sent to: " + email, Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        // Manage Preferences
        tvManagePreferences.setOnClickListener(v -> {
            startActivity(new Intent(this, SetupProfileActivity.class));
        });

        // Language Selection
        rlLanguage.setOnClickListener(v -> showLanguageDialog());

        // Logout
        btnSettingsLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Notification Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Enabled" : "Disabled";
            Toast.makeText(this, "Notifications " + status, Toast.LENGTH_SHORT).show();
        });

        // Theme Switch Placeholder
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String theme = isChecked ? "Dark" : "Light";
            Toast.makeText(this, theme + " mode selected (Placeholder)", Toast.LENGTH_SHORT).show();
        });
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "Hindi"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");
        builder.setItems(languages, (dialog, which) -> {
            tvCurrentLanguage.setText(languages[which]);
            Toast.makeText(this, "Language changed to " + languages[which], Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }
}
