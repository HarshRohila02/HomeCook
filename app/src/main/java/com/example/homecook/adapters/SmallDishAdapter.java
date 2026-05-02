package com.example.homecook.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.models.Dish;
import java.util.List;

public class SmallDishAdapter extends RecyclerView.Adapter<SmallDishAdapter.SmallDishViewHolder> {

    private Context context;
    private List<Dish> dishList;

    public SmallDishAdapter(Context context, List<Dish> dishList) {
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public SmallDishViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_small_dish, parent, false);
        return new SmallDishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SmallDishViewHolder holder, int position) {
        Dish dish = dishList.get(position);
        holder.tvName.setText(dish.getName());
        holder.tvPrice.setText("₹" + (int) dish.getPrice());

        String imgName = dish.getImageName();
        int resId = imgName != null ? context.getResources().getIdentifier(imgName, "drawable", context.getPackageName()) : 0;
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivDish);
        } else {
            Log.w("SmallDishAdapter", "Image NOT found for dish '" + dish.getName() + "', imageName='" + imgName + "'");
            holder.ivDish.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return dishList.size();
    }

    public static class SmallDishViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDish;
        TextView tvName, tvPrice;

        public SmallDishViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDish = itemView.findViewById(R.id.ivSmallDish);
            tvName = itemView.findViewById(R.id.tvSmallDishName);
            tvPrice = itemView.findViewById(R.id.tvSmallDishPrice);
        }
    }
}