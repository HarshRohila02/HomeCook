package com.example.homecook.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.models.NotificationItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<NotificationItem> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    public NotificationAdapter(Context context, List<NotificationItem> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationItem item = notificationList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
        holder.tvTime.setText(sdf.format(new Date(item.getTimestamp())));

        if (item.isRead()) {
            holder.viewUnread.setVisibility(View.GONE);
            holder.tvTitle.setTypeface(null, Typeface.NORMAL);
            holder.tvMessage.setTypeface(null, Typeface.NORMAL);
        } else {
            holder.viewUnread.setVisibility(View.VISIBLE);
            holder.tvTitle.setTypeface(null, Typeface.BOLD);
            holder.tvMessage.setTypeface(null, Typeface.BOLD);
        }

        holder.layout.setOnClickListener(v -> listener.onNotificationClick(item));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        View viewUnread;
        LinearLayout layout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            viewUnread = itemView.findViewById(R.id.viewUnreadDot);
            layout = itemView.findViewById(R.id.llNotificationItem);
        }
    }
}
