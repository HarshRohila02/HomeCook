package com.example.homecook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MySubscriptionAdapter extends RecyclerView.Adapter<MySubscriptionAdapter.SubViewHolder> {

    private Context context;
    private List<DocumentSnapshot> subscriptions;
    private SimpleDateFormat dateFormat;

    public MySubscriptionAdapter(Context context, List<DocumentSnapshot> subscriptions) {
        this.context = context;
        this.subscriptions = subscriptions;
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_subscription, parent, false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
        DocumentSnapshot doc = subscriptions.get(position);

        String cookId = doc.getString("cookId");
        String planName = doc.getString("planName");
        Double totalAmount = doc.getDouble("totalAmount");
        Boolean active = doc.getBoolean("active");
        Timestamp startTs = doc.getTimestamp("startDate");
        Timestamp endTs = doc.getTimestamp("validUntil");

        // Look up cook name from Firestore
        holder.tvCookName.setText("Loading...");
        if (cookId != null && !cookId.isEmpty()) {
            FirebaseFirestore.getInstance().collection("cooks").document(cookId)
                    .get()
                    .addOnSuccessListener(cookDoc -> {
                        if (cookDoc.exists()) {
                            String cookName = cookDoc.getString("name");
                            holder.tvCookName.setText(cookName != null ? cookName : cookId);
                        } else {
                            holder.tvCookName.setText(cookId);
                        }
                    })
                    .addOnFailureListener(e -> holder.tvCookName.setText(cookId));
        } else {
            holder.tvCookName.setText("Unknown Cook");
        }
        
        String priceText = totalAmount != null ? "₹" + totalAmount.intValue() : "";
        holder.tvPlanName.setText((planName != null ? planName : "Plan") + " — " + priceText);

        if (active != null && active) {
            holder.tvStatus.setText("Active");
        } else {
            holder.tvStatus.setText("Expired");
        }

        if (startTs != null) {
            holder.tvStartDate.setText("Start: " + dateFormat.format(startTs.toDate()));
        }
        if (endTs != null) {
            holder.tvEndDate.setText("End: " + dateFormat.format(endTs.toDate()));
            // Check if actually expired
            if (endTs.toDate().before(new Date())) {
                holder.tvStatus.setText("Expired");
            }
        }
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    static class SubViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvCookName, tvPlanName, tvStartDate, tvEndDate;

        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvSubStatus);
            tvCookName = itemView.findViewById(R.id.tvSubCookName);
            tvPlanName = itemView.findViewById(R.id.tvSubPlanName);
            tvStartDate = itemView.findViewById(R.id.tvSubStartDate);
            tvEndDate = itemView.findViewById(R.id.tvSubEndDate);
        }
    }
}
