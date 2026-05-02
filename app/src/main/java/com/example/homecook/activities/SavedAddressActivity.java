package com.example.homecook.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.R;
import com.example.homecook.adapters.AddressAdapter;
import com.example.homecook.models.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SavedAddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionListener {

    private EditText etFullAddress, etPhone;
    private RadioGroup rgLabel;
    private Button btnAdd;
    private ImageView ivBack;
    private RecyclerView rvAddresses;
    
    private AddressAdapter adapter;
    private List<Address> addressList;
    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_address);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getUid();

        etFullAddress = findViewById(R.id.etFullAddress);
        etPhone = findViewById(R.id.etAddressPhone);
        rgLabel = findViewById(R.id.rgAddressLabel);
        btnAdd = findViewById(R.id.btnAddAddress);
        ivBack = findViewById(R.id.ivBackAddress);
        rvAddresses = findViewById(R.id.rvAddresses);

        ivBack.setOnClickListener(v -> finish());

        addressList = new ArrayList<>();
        adapter = new AddressAdapter(this, addressList, this);
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rvAddresses.setAdapter(adapter);

        loadAddresses();

        btnAdd.setOnClickListener(v -> saveNewAddress());
    }

    private void loadAddresses() {
        if (userId == null) return;

        db.collection("users").document(userId).collection("addresses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addressList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            addressList.add(doc.toObject(Address.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void saveNewAddress() {
        String fullAddress = etFullAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        
        if (fullAddress.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgLabel.getCheckedRadioButtonId();
        RadioButton rb = findViewById(selectedId);
        String label = rb.getText().toString();

        String addressId = UUID.randomUUID().toString();
        boolean isDefault = addressList.isEmpty(); // Make first address default

        Address newAddress = new Address(addressId, label, fullAddress, phone, isDefault);

        db.collection("users").document(userId).collection("addresses")
                .document(addressId)
                .set(newAddress)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show();
                    if (isDefault) {
                        updateDefaultInProfile(newAddress);
                    }
                    etFullAddress.setText("");
                    etPhone.setText("");
                    loadAddresses();
                });
    }

    @Override
    public void onDelete(Address address) {
        db.collection("users").document(userId).collection("addresses")
                .document(address.getAddressId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Address deleted", Toast.LENGTH_SHORT).show();
                    if (address.isDefault() && addressList.size() > 1) {
                        // Logic to pick another default could be added here
                    }
                    loadAddresses();
                });
    }

    @Override
    public void onSetDefault(Address address) {
        // 1. Unset existing default in Firestore
        for (Address a : addressList) {
            if (a.isDefault()) {
                db.collection("users").document(userId).collection("addresses")
                        .document(a.getAddressId()).update("isDefault", false);
            }
        }

        // 2. Set new default
        db.collection("users").document(userId).collection("addresses")
                .document(address.getAddressId())
                .update("isDefault", true)
                .addOnSuccessListener(aVoid -> {
                    updateDefaultInProfile(address);
                    loadAddresses();
                });
    }

    private void updateDefaultInProfile(Address address) {
        java.util.Map<String, Object> updates = new java.util.HashMap<>();
        updates.put("defaultAddress", address.getFullAddress());
        updates.put("defaultAddressLabel", address.getLabel());
        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Default address updated in main profile
                });
    }
}
