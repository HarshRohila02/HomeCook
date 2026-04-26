package com.example.homecook.activities.placeholders;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;

public class DishDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);
        
        String dishId = getIntent().getStringExtra("dishId");
        TextView tvTitle = findViewById(R.id.tvDishNameDetail);
        if (tvTitle != null) {
            tvTitle.setText("Dish Details Placeholder\nDish ID: " + dishId);
        }
    }
}