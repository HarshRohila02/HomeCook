package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.activities.placeholders.CookProfileActivity;
import com.example.homecook.models.Cook;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class CookListAdapter extends RecyclerView.Adapter<CookListAdapter.CookViewHolder> {

    private Context context;
    private List<Cook> cookList;

    public CookListAdapter(Context context, List<Cook> cookList) {
        this.context = context;
        this.cookList = cookList;
    }

    @NonNull
    @Override
    public CookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cook_large, parent, false);
        return new CookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CookViewHolder holder, int position) {
        Cook cook = cookList.get(position);
        holder.tvCookName.setText(cook.getName());
        holder.tvCuisine.setText(cook.getCuisine());
        holder.tvRating.setText("★ " + cook.getRating());
        holder.tvReviews.setText("(" + cook.getReviewsCount() + " reviews)");
        holder.tvDistance.setText(cook.getDistance());
        
        holder.tvStatus.setText(cook.isAvailable() ? "Available Now" : "Currently Offline");
        holder.tvStatus.setTextColor(cook.isAvailable() ? 
                context.getResources().getColor(android.R.color.holo_green_dark) : 
                context.getResources().getColor(android.R.color.darker_gray));

        // Load image using Glide
        int resId = context.getResources().getIdentifier(cook.getImageName(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivCook);
        } else {
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
        CircleImageView ivCook;
        TextView tvCookName, tvCuisine, tvRating, tvReviews, tvDistance, tvStatus;

        public CookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCook = itemView.findViewById(R.id.ivCookLarge);
            tvCookName = itemView.findViewById(R.id.tvCookNameLarge);
            tvCuisine = itemView.findViewById(R.id.tvCuisineLarge);
            tvRating = itemView.findViewById(R.id.tvRatingLarge);
            tvReviews = itemView.findViewById(R.id.tvReviewsLarge);
            tvDistance = itemView.findViewById(R.id.tvDistanceLarge);
            tvStatus = itemView.findViewById(R.id.tvStatusLarge);
        }
    }
}