package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.activities.DishDetailActivity;
import com.example.homecook.activities.SubscriptionPlansActivity;
import com.example.homecook.models.Dish;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuDishAdapter extends RecyclerView.Adapter<MenuDishAdapter.MenuViewHolder> {

    private Context context;
    private List<Dish> dishList;
    private FirebaseFirestore db;
    private String userId;

    public MenuDishAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_dish, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvName.setText(dish.getName());
        holder.tvCategory.setText(dish.getCategory() + " • " + dish.getMealType());
        holder.tvDesc.setText(dish.getDescription());
        holder.tvPrice.setText("₹" + (int) dish.getPrice());

        int resId = context.getResources().getIdentifier(dish.getImageName(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivDish);
        }

        holder.btnAdd.setOnClickListener(v -> addToCart(dish));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DishDetailActivity.class);
            intent.putExtra("dishId", dish.getDishId());
            intent.putExtra("cookId", dish.getCookId());
            context.startActivity(intent);
        });
    }

    private void addToCart(Dish dish) {
        if (userId == null) return;

        // Phase 4D: Check for active subscription before adding to cart
        db.collection("subscriptions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("cookId", dish.getCookId())
                .whereEqualTo("active", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Active subscription found, add to cart
                        performAddToCart(dish);
                    } else {
                        // No active subscription, show alert
                        showSubscriptionAlert(dish.getCookId());
                    }
                });
    }

    private void performAddToCart(Dish dish) {
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("dishId", dish.getDishId());
        cartItem.put("cookId", dish.getCookId());
        cartItem.put("name", dish.getName());
        cartItem.put("price", dish.getPrice());
        cartItem.put("quantity", 1);
        cartItem.put("imageName", dish.getImageName());
        cartItem.put("category", dish.getCategory());
        cartItem.put("addedAt", System.currentTimeMillis());

        db.collection("cart").document(userId).collection("items")
                .document(dish.getDishId())
                .set(cartItem)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, dish.getName() + " added to cart", Toast.LENGTH_SHORT).show());
    }

    private void showSubscriptionAlert(String cookId) {
        new AlertDialog.Builder(context)
                .setTitle("Subscription Required")
                .setMessage("Please subscribe to this cook before placing orders.")
                .setPositiveButton("Get Subscription", (dialog, which) -> {
                    Intent intent = new Intent(context, SubscriptionPlansActivity.class);
                    intent.putExtra("cookId", cookId);
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDish;
        TextView tvName, tvCategory, tvDesc, tvPrice;
        Button btnAdd;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDish = itemView.findViewById(R.id.ivMenuDish);
            tvName = itemView.findViewById(R.id.tvMenuDishName);
            tvCategory = itemView.findViewById(R.id.tvMenuDishCategory);
            tvDesc = itemView.findViewById(R.id.tvMenuDishDesc);
            tvPrice = itemView.findViewById(R.id.tvMenuDishPrice);
            btnAdd = itemView.findViewById(R.id.btnAddDish);
        }
    }
}