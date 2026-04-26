package com.example.homecook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.homecook.R;
import com.example.homecook.models.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartList;
    private FirebaseFirestore db;
    private String userId;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartList, CartUpdateListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuth.getInstance().getUid();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("₹" + (int) item.getPrice());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        int resId = context.getResources().getIdentifier(item.getImageName(), "drawable", context.getPackageName());
        if (resId != 0) {
            Glide.with(context).load(resId).placeholder(R.drawable.ic_launcher_background).into(holder.ivItem);
        }

        holder.ivPlus.setOnClickListener(v -> updateQuantity(item, 1));
        holder.ivMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                updateQuantity(item, -1);
            } else {
                deleteItem(item);
            }
        });
        holder.ivDelete.setOnClickListener(v -> deleteItem(item));
    }

    private void updateQuantity(CartItem item, int change) {
        int newQty = item.getQuantity() + change;
        db.collection("cart").document(userId).collection("items")
                .document(item.getDishId())
                .update("quantity", newQty)
                .addOnSuccessListener(aVoid -> {
                    item.setQuantity(newQty);
                    notifyDataSetChanged();
                    listener.onCartUpdated();
                });
    }

    private void deleteItem(CartItem item) {
        db.collection("cart").document(userId).collection("items")
                .document(item.getDishId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartList.remove(item);
                    notifyDataSetChanged();
                    listener.onCartUpdated();
                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivItem, ivPlus, ivMinus, ivDelete;
        TextView tvName, tvPrice, tvQty;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.ivCartItem);
            ivPlus = itemView.findViewById(R.id.ivPlusCart);
            ivMinus = itemView.findViewById(R.id.ivMinusCart);
            ivDelete = itemView.findViewById(R.id.ivDeleteCart);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQty = itemView.findViewById(R.id.tvCartItemQty);
        }
    }
}
