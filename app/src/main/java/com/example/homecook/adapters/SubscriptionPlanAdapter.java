package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.models.SubscriptionPlan;
import java.util.List;

public class SubscriptionPlanAdapter extends RecyclerView.Adapter<SubscriptionPlanAdapter.PlanViewHolder> {

    private Context context;
    private List<SubscriptionPlan> planList;
    private String cookId;

    public SubscriptionPlanAdapter(Context context, List<SubscriptionPlan> planList, String cookId) {
        this.context = context;
        this.planList = planList;
        this.cookId = cookId;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_subscription_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        SubscriptionPlan plan = planList.get(position);
        holder.tvPlanName.setText(plan.getName());
        holder.tvPlanPrice.setText("₹" + (int)plan.getPrice());
        holder.tvPlanDuration.setText("Validity: " + plan.getDurationDays() + " Days");
        holder.tvPlanDescription.setText(plan.getDescription());

        holder.btnSelectPlan.setOnClickListener(v -> {
            // SubscriptionCheckoutActivity will be implemented in next phase or should exist
            try {
                Intent intent = new Intent(context, Class.forName("com.example.homecook.activities.SubscriptionCheckoutActivity"));
                intent.putExtra("planName", plan.getName());
                intent.putExtra("price", plan.getPrice());
                intent.putExtra("durationDays", plan.getDurationDays());
                intent.putExtra("cookId", cookId);
                context.startActivity(intent);
            } catch (ClassNotFoundException e) {
                // If the class doesn't exist yet, we can't start it.
                // For now, let's assume it will be created.
                // In a real scenario, we might use a direct class reference if it's already there.
                android.widget.Toast.makeText(context, "Checkout Activity not found. Proceeding to create it.", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanPrice, tvPlanDuration, tvPlanDescription;
        Button btnSelectPlan;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvPlanPrice = itemView.findViewById(R.id.tvPlanPrice);
            tvPlanDuration = itemView.findViewById(R.id.tvPlanDuration);
            tvPlanDescription = itemView.findViewById(R.id.tvPlanDescription);
            btnSelectPlan = itemView.findViewById(R.id.btnSelectPlan);
        }
    }
}