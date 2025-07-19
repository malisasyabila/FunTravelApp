package com.example.funtravelapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ReviewRateActivity extends AppCompatActivity {

    private LinearLayout reviewContainer;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_rate);

        reviewContainer = findViewById(R.id.review_container);
        btnBack = findViewById(R.id.btn_back_review);

        btnBack.setOnClickListener(v -> finish());

        loadReviews(); // Initial load
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReviews(); // Refresh on resume
    }

    private void loadReviews() {
        // Remove all views except the title (first child)
        int childCount = reviewContainer.getChildCount();
        if (childCount > 1) {
            reviewContainer.removeViews(1, childCount - 1);
        }

        List<ReviewModel> reviews = ReviewStorage.getInstance(this).getReviews();

        if (reviews == null || reviews.isEmpty()) {
            TextView noReviewsMsg = new TextView(this);
            noReviewsMsg.setText("No reviews submitted yet.");
            noReviewsMsg.setTextSize(18);
            noReviewsMsg.setPadding(20, 20, 20, 20);
            reviewContainer.addView(noReviewsMsg);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (ReviewModel review : reviews) {
            try {
                // Inflate review card layout
                View cardView = inflater.inflate(R.layout.item_review_card, reviewContainer, false);

                // Set image
                ImageView img = cardView.findViewById(R.id.review_image);
                String imageUriStr = review.getImageUriString();
                if (imageUriStr != null && !imageUriStr.isEmpty()) {
                    Uri uri = Uri.parse(imageUriStr);
                    img.setImageURI(uri);
                    img.setVisibility(View.VISIBLE);
                } else {
                    img.setImageResource(android.R.color.darker_gray);
                }

                // Set review details
                TextView destinationText = cardView.findViewById(R.id.review_destination);
                TextView commentText = cardView.findViewById(R.id.review_comment);
                RatingBar ratingBar = cardView.findViewById(R.id.review_rating_bar);
                TextView dateTimeText = cardView.findViewById(R.id.review_date);

                destinationText.setText("Destination: " + review.getDestinationName());
                commentText.setText(review.getComment());
                ratingBar.setRating(review.getRating());
                dateTimeText.setText(review.getDateTime());

                // Handle delete button
                ImageButton deleteBtn = cardView.findViewById(R.id.history_delete_button);
                deleteBtn.setOnClickListener(v -> {
                    showDeleteConfirmation(review);
                });

                // Add card to container
                reviewContainer.addView(cardView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showDeleteConfirmation(ReviewModel review) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ReviewStorage.getInstance(this).removeReview(review);
                    loadReviews(); // Refresh after deletion
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
