package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.models.Dish;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class DishDetailActivity extends AppCompatActivity {

    private String dishId, cookId;
    private FirebaseFirestore db;
    private String userId;
    
    private ImageView ivDishDetail, ivBack;
    private TextView tvName, tvPrice, tvCategory, tvMealType, tvDesc, tvIngredients, tvQuantity;
    private Button btnMinus, btnPlus, btnAddToCart;
    
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_detail);

        dishId = getIntent().getStringExtra("dishId");
        cookId = getIntent().getStringExtra("cookId");

        if (dishId == null || dishId.isEmpty()) {
            Toast.makeText(this, "Error: Dish data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getUid();
        }

        // Initialize Views
        ivDishDetail = findViewById(R.id.ivDishDetail);
        ivBack = findViewById(R.id.ivBackDish);
        tvName = findViewById(R.id.tvDishNameDetail);
        tvPrice = findViewById(R.id.tvDishPriceDetail);
        tvCategory = findViewById(R.id.tvDishCategoryDetail);
        tvMealType = findViewById(R.id.tvMealTypeDetail);
        tvDesc = findViewById(R.id.tvDishDescDetail);
        tvIngredients = findViewById(R.id.tvIngredientsDetail);
        tvQuantity = findViewById(R.id.tvQuantity);
        
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCartDetail);

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        loadDishDetails();

        // Quantity Logic
        if (btnPlus != null) {
            btnPlus.setOnClickListener(v -> {
                quantity++;
                if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
            });
        }

        if (btnMinus != null) {
            btnMinus.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
                }
            });
        }

        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> addToCart());
        }
    }

    private void loadDishDetails() {
        db.collection("dishes").document(dishId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Dish dish = doc.toObject(Dish.class);
                if (dish != null) {
                    if (tvName != null) tvName.setText(dish.getName());
                    if (tvPrice != null) tvPrice.setText("₹" + (int) dish.getPrice());
                    if (tvCategory != null) tvCategory.setText(dish.getCategory());
                    if (tvMealType != null) tvMealType.setText(dish.getMealType());
                    if (tvDesc != null) tvDesc.setText(dish.getDescription());
                    if (tvIngredients != null) tvIngredients.setText(dish.getIngredients());

                    if (ivDishDetail != null && dish.getImageName() != null) {
                        int resId = getResources().getIdentifier(dish.getImageName(), "drawable", getPackageName());
                        Glide.with(this)
                             .load(resId != 0 ? resId : R.drawable.ic_launcher_background)
                             .placeholder(R.drawable.ic_launcher_background)
                             .error(R.drawable.ic_launcher_background)
                             .into(ivDishDetail);
                    }
                }
            } else {
                Toast.makeText(this, "Dish details not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void addToCart() {
        if (userId == null) {
            Toast.makeText(this, "Please login to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tvName == null || tvPrice == null) return;

        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("dishId", dishId);
        cartItem.put("cookId", cookId != null ? cookId : "N/A");
        cartItem.put("name", tvName.getText().toString());
        
        try {
            String priceStr = tvPrice.getText().toString().replace("₹", "");
            cartItem.put("price", Double.parseDouble(priceStr));
        } catch (Exception e) {
            cartItem.put("price", 0.0);
        }
        
        cartItem.put("quantity", quantity);
        cartItem.put("addedAt", System.currentTimeMillis());

        db.collection("cart").document(userId).collection("items")
                .document(dishId)
                .set(cartItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Added to cart successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error adding to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
