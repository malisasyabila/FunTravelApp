package com.example.funtravelapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private ImageView bookingImage;
    private TextView bookingName, bookingLocation, bookingRating, bookingDescription, bookingPrice;
    private Button confirmBookingButton;

    private float ratingValue;
    private int imageResId;
    private String name, location, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        // Link Views
        bookingImage = findViewById(R.id.booking_image);
        bookingName = findViewById(R.id.booking_name);
        bookingLocation = findViewById(R.id.booking_location);
        bookingRating = findViewById(R.id.booking_rating);
        bookingDescription = findViewById(R.id.booking_description);
        bookingPrice = findViewById(R.id.booking_price);
        confirmBookingButton = findViewById(R.id.confirm_booking_button);

        // Receive data from MainDashboardActivity
        name = getIntent().getStringExtra("destination_name");
        location = getIntent().getStringExtra("destination_location");
        price = getIntent().getStringExtra("destination_price");
        ratingValue = getIntent().getFloatExtra("destination_rating", 0f);
        imageResId = getIntent().getIntExtra("destination_image", R.drawable.bgapp);

        // Set Description (can customize more)
        String description;
        if ("Icity".equalsIgnoreCase(name)) {
            description = "Enjoy amazing lights, snow walk, and water rides in I-City, Shah Alam. Great for families and night photography!";
        } else {
            description = "Explore this amazing destination and enjoy unforgettable travel experiences.";
        }

        // Populate Views
        bookingImage.setImageResource(imageResId);
        bookingName.setText(name);
        bookingLocation.setText(location);
        bookingRating.setText(ratingValue + " ★");
        bookingPrice.setText(price + " / Person");
        bookingDescription.setText(description);

        // Link to InformationBooking
        confirmBookingButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, InformationBooking.class);
            intent.putExtra("destination_name", name);
            intent.putExtra("destination_location", location);
            intent.putExtra("destination_price", price);
            intent.putExtra("destination_rating", ratingValue);
            intent.putExtra("destination_image", imageResId);
            startActivity(intent);
        });
    }
}
