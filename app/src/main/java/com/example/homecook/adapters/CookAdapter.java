package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.homecook.activities.CookProfileActivity;
import com.example.homecook.models.Cook;
import java.util.List;

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.CookViewHolder> {

    private Context context;
    private List<Cook> cookList;

    public CookAdapter(Context context, List<Cook> cookList) {
        this.context = context;
        this.cookList = cookList;
    }

    @NonNull
    @Override
    public CookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cook, parent, false);
        return new CookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CookViewHolder holder, int position) {
        Cook cook = cookList.get(position);
        holder.tvCookName.setText(cook.getName());
        holder.tvCuisine.setText(cook.getCuisine());
        holder.tvDistance.setText(cook.getDistance());
        holder.tvRating.setText("★ " + cook.getRating());

        String imgName = cook.getImageName();
        int resId = imgName != null ? context.getResources().getIdentifier(imgName, "drawable", context.getPackageName()) : 0;
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivCook);
        } else {
            Log.w("CookAdapter", "Image NOT found for cook '" + cook.getName() + "', imageName='" + imgName + "'");
            holder.ivCook.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CookProfileActivity.class);
            intent.putExtra("cookId", cook.getCookId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cookList.size();
    }

    public static class CookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCook;
        TextView tvCookName, tvCuisine, tvDistance, tvRating;

        public CookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCook = itemView.findViewById(R.id.ivCook);
            tvCookName = itemView.findViewById(R.id.tvCookName);
            tvCuisine = itemView.findViewById(R.id.tvCuisine);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}