package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.activities.DishDetailActivity;
import com.example.homecook.models.Dish;
import java.util.List;

public class SearchDishAdapter extends RecyclerView.Adapter<SearchDishAdapter.ViewHolder> {

    private Context context;
    private List<Dish> dishList;

    public SearchDishAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_dish, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvDishName.setText(dish.getName());
        holder.tvCategory.setText(dish.getCategory());
        holder.tvPrice.setText("₹" + (int) dish.getPrice());

        int resId = context.getResources().getIdentifier(dish.getImageName(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivDish);
        } else {
            holder.ivDish.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DishDetailActivity.class);
            intent.putExtra("dishId", dish.getDishId());
            intent.putExtra("cookId", dish.getCookId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDish;
        TextView tvDishName, tvCategory, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDish = itemView.findViewById(R.id.ivDish);
            tvDishName = itemView.findViewById(R.id.tvDishName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
