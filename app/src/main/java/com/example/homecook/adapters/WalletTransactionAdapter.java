package com.example.homecook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.models.WalletTransaction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalletTransactionAdapter extends RecyclerView.Adapter<WalletTransactionAdapter.TransactionViewHolder> {

    private Context context;
    private List<WalletTransaction> transactionList;

    public WalletTransactionAdapter(Context context, List<WalletTransaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wallet_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        WalletTransaction transaction = transactionList.get(position);
        
        holder.tvTitle.setText(transaction.getTitle());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(transaction.getDate())));

        if ("CREDIT".equals(transaction.getType())) {
            holder.tvAmount.setText("+₹" + (int)transaction.getAmount());
            holder.tvAmount.setTextColor(Color.parseColor("#4CAF50"));
            holder.ivType.setImageResource(android.R.drawable.ic_menu_add);
            holder.ivType.setColorFilter(Color.parseColor("#4CAF50"));
        } else {
            holder.tvAmount.setText("-₹" + (int)transaction.getAmount());
            holder.tvAmount.setTextColor(Color.parseColor("#F44336"));
            holder.ivType.setImageResource(android.R.drawable.ic_delete);
            holder.ivType.setColorFilter(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvAmount;
        ImageView ivType;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTransactionTitle);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
            ivType = itemView.findViewById(R.id.ivTransactionType);
        }
    }
}
