package com.example.homecook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.models.Address;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<Address> addressList;
    private OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.tvLabel.setText(address.getLabel().toUpperCase());
        holder.tvFullAddress.setText(address.getFullAddress());
        holder.tvPhone.setText("Phone: " + address.getPhone());

        if (address.isDefault()) {
            holder.tvDefaultBadge.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefaultBadge.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        holder.ivDelete.setOnClickListener(v -> listener.onDelete(address));
        holder.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(address));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvFullAddress, tvPhone, tvDefaultBadge;
        ImageView ivDelete;
        Button btnSetDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvAddressLabel);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            tvPhone = itemView.findViewById(R.id.tvAddressPhone);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            ivDelete = itemView.findViewById(R.id.ivDeleteAddress);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}
