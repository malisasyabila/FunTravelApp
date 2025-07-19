package com.example.funtravelapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class InformationBooking extends AppCompatActivity {

    private ImageView bookingImage;
    private EditText inputName, inputEmail, inputPeople, inputDate, inputNotes;
    private Button confirmBookingButton;
    private int imageResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_information);

        // Link views to XML
        bookingImage = findViewById(R.id.booking_image);
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPeople = findViewById(R.id.input_people);
        inputDate = findViewById(R.id.input_date);
        inputNotes = findViewById(R.id.input_notes);
        confirmBookingButton = findViewById(R.id.confirm_booking_button);

        // Get image resource ID passed from BookingActivity
        imageResId = getIntent().getIntExtra("destination_image", R.drawable.bgapp);
        bookingImage.setImageResource(imageResId);

        // Show Date Picker on inputDate click
        inputDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    InformationBooking.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                        inputDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Get other data
        String destinationName = getIntent().getStringExtra("destination_name");
        String destinationLocation = getIntent().getStringExtra("destination_location");
        String destinationPrice = getIntent().getStringExtra("destination_price");
        float destinationRating = getIntent().getFloatExtra("destination_rating", 0f);

        // On Confirm Booking
        confirmBookingButton.setOnClickListener(v -> {
            String fullName = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String peopleStr = inputPeople.getText().toString().trim();
            String date = inputDate.getText().toString().trim();
            String notes = inputNotes.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || peopleStr.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int numPeople;
            try {
                numPeople = Integer.parseInt(peopleStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number of people!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                String priceStr = destinationPrice.replace("RM", "").trim();
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format!", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Create booking with imageResId included
            BookingItem booking = new BookingItem(destinationName, fullName, numPeople, price, date, notes, imageResId);
            BookingData.bookingList.add(booking);
            BookingData.saveBookings(getApplicationContext());

            // ✅ Show confirmation
            String summary = "Booking Successful!\n"
                    + "Destination: " + destinationName + "\n"
                    + "Location: " + destinationLocation + "\n"
                    + "Price: " + destinationPrice + "\n"
                    + "Rating: " + destinationRating + " ★\n"
                    + "Name: " + fullName + "\n"
                    + "Email: " + email + "\n"
                    + "People: " + numPeople + "\n"
                    + "Visit Date: " + date;

            if (!notes.isEmpty()) {
                summary += "\nNotes: " + notes;
            }

            Toast toast = Toast.makeText(this, summary, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
            toast.show();

            finish(); // Close the activity
        });
    }
}
