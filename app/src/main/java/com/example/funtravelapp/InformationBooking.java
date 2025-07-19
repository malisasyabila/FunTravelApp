package com.example.funtravelapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class InformationBooking extends AppCompatActivity {

    private ImageView bookingImage;
    private EditText inputName, inputEmail, inputPeople, inputDate, inputNotes;
    private Button confirmBookingButton, backBtn;
    private int imageResId;

    private FirebaseAuth auth; // Firebase Authentication (for uid)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_information);

        // ---- Back button (if exists) ----
        backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        // ---- Firebase Auth (anonymous sign-in if not authenticated) ----
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously()
                    .addOnSuccessListener(r -> { /* Signed in anonymously */ })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Auth failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        // ---- Link views ----
        bookingImage = findViewById(R.id.booking_image);
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPeople = findViewById(R.id.input_people);
        inputDate = findViewById(R.id.input_date);
        inputNotes = findViewById(R.id.input_notes);
        confirmBookingButton = findViewById(R.id.confirm_booking_button);

        // ---- Get data from previous Activity ----
        imageResId = getIntent().getIntExtra("destination_image", R.drawable.bgapp);
        bookingImage.setImageResource(imageResId);

        String destinationName = getIntent().getStringExtra("destination_name");
        String destinationLocation = getIntent().getStringExtra("destination_location");
        String destinationPrice = getIntent().getStringExtra("destination_price");
        float destinationRating = getIntent().getFloatExtra("destination_rating", 0f);

        // ---- Date picker for visit date ----
        inputDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    InformationBooking.this,
                    (view, y, m, d) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", d, (m + 1), y);
                        inputDate.setText(selectedDate);
                    },
                    year, month, day
            );
            dialog.show();
        });

        // ---- Confirm Booking Button ----
        confirmBookingButton.setOnClickListener(v -> {
            String fullName = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String peopleStr = inputPeople.getText().toString().trim();
            String date = inputDate.getText().toString().trim();
            String notes = inputNotes.getText().toString().trim();

            // Basic validation
            if (fullName.isEmpty() || email.isEmpty() || peopleStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int numPeople;
            try {
                numPeople = Integer.parseInt(peopleStr);
                if (numPeople <= 0) {
                    Toast.makeText(this, "People must be > 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of people!", Toast.LENGTH_SHORT).show();
                return;
            }

            double pricePerPerson;
            try {
                String priceStr = destinationPrice.replace("RM", "").trim();
                pricePerPerson = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format!", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalPrice = pricePerPerson * numPeople;

            // ---- Prepare Firebase reference ----
            String uid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "guest";
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference bookingsRef = db.getReference("bookings").child(uid);

            // ---- Generate push key ----
            String bookingId = bookingsRef.push().getKey();
            if (bookingId == null) {
                Toast.makeText(this, "Failed to generate booking key", Toast.LENGTH_SHORT).show();
                return;
            }

            // ---- Convert resource ID to resource name ----
            String imageResName = getResources().getResourceEntryName(imageResId);

            // ---- Create booking object ----
            BookingItem booking = new BookingItem(
                    bookingId,
                    destinationName,
                    fullName,
                    email,
                    numPeople,
                    pricePerPerson,
                    totalPrice,
                    date,
                    notes,
                    imageResName,
                    System.currentTimeMillis()
            );

            // ---- Save to Firebase ----
            bookingsRef.child(bookingId).setValue(booking)
                    .addOnSuccessListener(unused -> {
                        // Show a formatted confirmation toast
                        String summary = "Booking Successful!\n"
                                + "Destination: " + destinationName + "\n"
                                + "Location: " + destinationLocation + "\n"
                                + "Price/person: RM" + String.format("%.2f", pricePerPerson) + "\n"
                                + "Total: RM" + String.format("%.2f", totalPrice) + "\n"
                                + "Rating: " + destinationRating + " ★\n"
                                + "Name: " + fullName + "\n"
                                + "Email: " + email + "\n"
                                + "People: " + numPeople + "\n"
                                + "Visit Date: " + date
                                + (notes.isEmpty() ? "" : "\nNotes: " + notes);

                        Toast toast = Toast.makeText(this, summary, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                        toast.show();

                        // Navigate to history screen, pass new booking ID (for highlight)
                        Intent intent = new Intent(InformationBooking.this, BookingHistoryActivity.class);
                        intent.putExtra("new_booking_id", bookingId);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
