package com.example.homecook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.homecook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviewsActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etReviewComment;
    private Button btnSubmitReview;
    private View btnUploadPhoto;
    private ImageView ivBackReview;

    private String orderId, cookId;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get data from intent
        orderId = getIntent().getStringExtra("orderId");
        cookId = getIntent().getStringExtra("cookId");

        // Initialize views
        ratingBar = findViewById(R.id.ratingBar);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto);
        ivBackReview = findViewById(R.id.ivBackReview);

        ivBackReview.setOnClickListener(v -> finish());

        btnUploadPhoto.setOnClickListener(v -> {
            Toast.makeText(this, "Photo upload feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String comment = etReviewComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please provide a star rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "guest";
        String reviewId = UUID.randomUUID().toString();

        Map<String, Object> review = new HashMap<>();
        review.put("reviewId", reviewId);
        review.put("userId", userId);
        review.put("orderId", orderId != null ? orderId : "N/A");
        review.put("cookId", cookId != null ? cookId : "N/A");
        review.put("rating", rating);
        review.put("comment", comment);
        review.put("createdAt", System.currentTimeMillis());

        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Submitting...");

        db.collection("reviews").document(reviewId)
                .set(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReviewsActivity.this, "Review submitted! Thank you.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReviewsActivity.this, OrdersActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitReview.setEnabled(true);
                    btnSubmitReview.setText("Submit Review");
                    Toast.makeText(ReviewsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
