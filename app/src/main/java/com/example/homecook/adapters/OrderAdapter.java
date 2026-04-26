package com.example.homecook.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.activities.OrderTrackingActivity;
import com.example.homecook.models.Order;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.tvOrderId.setText("Order #" + order.getOrderId().substring(0, Math.min(order.getOrderId().length(), 12)));
        holder.tvStatus.setText(order.getStatus());
        holder.tvTotal.setText("₹" + (int) order.getTotalAmount());
        
        if (order.getCreatedAt() != null) {
            holder.tvDate.setText(sdf.format(order.getCreatedAt()));
        }

        // Items summary
        if (order.getItems() != null) {
            int itemCount = 0;
            for (Map<String, Object> item : order.getItems()) {
                Object qty = item.get("quantity");
                if (qty instanceof Long) itemCount += (Long) qty;
                else if (qty instanceof Integer) itemCount += (Integer) qty;
            }
            holder.tvItemsSummary.setText(itemCount + " Items");
        }

        // Highlight status
        if ("Delivered".equals(order.getStatus())) {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.btnTrack.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.btnTrack.setVisibility(View.VISIBLE);
        }

        holder.btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            context.startActivity(intent);
        });

        holder.btnReorder.setOnClickListener(v -> {
            Toast.makeText(context, "Reorder feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvStatus, tvItemsSummary, tvTotal;
        Button btnReorder, btnTrack;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderItemId);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvItemsSummary = itemView.findViewById(R.id.tvOrderItemsSummary);
            tvTotal = itemView.findViewById(R.id.tvOrderTotalAmount);
            btnReorder = itemView.findViewById(R.id.btnReorder);
            btnTrack = itemView.findViewById(R.id.btnTrackOrder);
        }
    }
}