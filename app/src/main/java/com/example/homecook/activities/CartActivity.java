package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.homecook.HomeActivity;
import com.example.homecook.R;
import com.example.homecook.adapters.CartAdapter;
import com.example.homecook.models.CartItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartUpdateListener {

    private RecyclerView rvCart;
    private CartAdapter adapter;
    private List<CartItem> cartList;
    private FirebaseFirestore db;
    private String userId;

    private TextView tvSubtotal, tvTotal;
    private LinearLayout llEmptyCart;
    private NestedScrollView nsvContent;
    private Button btnCheckout, btnShopNow;
    private BottomNavigationView bottomNavigationView;

    private double subtotal = 0;
    private final int deliveryFee = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getUid();
        } else {
            Toast.makeText(this, "Please login to view your cart", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        rvCart = findViewById(R.id.rvCartItems);
        tvSubtotal = findViewById(R.id.tvCartSubtotal);
        tvTotal = findViewById(R.id.tvCartTotal);
        llEmptyCart = findViewById(R.id.llEmptyCart);
        nsvContent = findViewById(R.id.nsvCartContent);
        btnCheckout = findViewById(R.id.btnCartCheckout);
        btnShopNow = findViewById(R.id.btnShopNow);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        findViewById(R.id.ivBackCart).setOnClickListener(v -> finish());
        if (btnShopNow != null) {
            btnShopNow.setOnClickListener(v -> finish());
        }

        cartList = new ArrayList<>();
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartList, this);
        rvCart.setAdapter(adapter);

        // Bottom Nav setup (Phase 8A)
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_cart) return true;
                
                Intent intent = null;
                if (id == R.id.nav_home) {
                    intent = new Intent(this, HomeActivity.class);
                } else if (id == R.id.nav_cooks) {
                    intent = new Intent(this, CookListActivity.class);
                } else if (id == R.id.nav_orders) {
                    intent = new Intent(this, OrdersActivity.class);
                } else if (id == R.id.nav_profile) {
                    intent = new Intent(this, ProfileActivity.class);
                }

                if (intent != null) {
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
        }

        loadCartItems();

        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(this, Class.forName("com.example.homecook.activities.OrderCheckoutActivity"));
                    intent.putExtra("subtotal", subtotal);
                    intent.putExtra("deliveryFee", (double) deliveryFee);
                    intent.putExtra("totalAmount", subtotal + deliveryFee);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Toast.makeText(this, "Checkout process is currently unavailable.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadCartItems() {
        if (userId == null) return;

        db.collection("cart").document(userId).collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        cartList.clear();
                        subtotal = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CartItem item = document.toObject(CartItem.class);
                            if (item != null) {
                                cartList.add(item);
                                subtotal += (item.getPrice() * item.getQuantity());
                            }
                        }
                        updateUI();
                    } else {
                        Toast.makeText(this, "Failed to load cart items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI() {
        if (cartList.isEmpty()) {
            if (llEmptyCart != null) llEmptyCart.setVisibility(View.VISIBLE);
            if (nsvContent != null) nsvContent.setVisibility(View.GONE);
        } else {
            if (llEmptyCart != null) llEmptyCart.setVisibility(View.GONE);
            if (nsvContent != null) nsvContent.setVisibility(View.VISIBLE);
            
            if (tvSubtotal != null) tvSubtotal.setText("₹" + (int) subtotal);
            if (tvTotal != null) tvTotal.setText("₹" + (int) (subtotal + deliveryFee));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCartUpdated() {
        subtotal = 0;
        for (CartItem item : cartList) {
            subtotal += (item.getPrice() * item.getQuantity());
        }
        updateUI();
    }
}
