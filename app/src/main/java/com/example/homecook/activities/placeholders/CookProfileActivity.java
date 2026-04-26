package com.example.homecook.activities.placeholders;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;

public class CookProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook_profile);

        String cookId = getIntent().getStringExtra("cookId");
        TextView tvTitle = findViewById(R.id.tvCookNameProfile);
        if (tvTitle != null) {
            tvTitle.setText("Cook Profile Placeholder\nCook ID: " + cookId);
        }
    }
}