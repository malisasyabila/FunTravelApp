package com.example.funtravelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {

    private Button backButton;
    private TextView contactUsLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us); // Make sure your XML filename is correct

        // Initialize UI elements
        backButton = findViewById(R.id.btn_back_about);
        contactUsLink = findViewById(R.id.contact_us_link);

        // Back button returns to previous activity
        backButton.setOnClickListener(view -> finish());

        // Contact Us opens dialog with options
        contactUsLink.setOnClickListener(view -> showContactOptions());
    }

    // Method to show contact options in a dialog
    private void showContactOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Contact Us");
        builder.setItems(new CharSequence[]{
                        "Phone Call",
                        "Send WhatsApp",
                        "Visit Website",
                        "Send Email"},
                (dialog, which) -> {
                    switch (which) {
                        case 0: // Phone Call
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:+60135350396"));
                            startActivity(callIntent);
                            break;

                        case 1: // WhatsApp
                            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                            whatsappIntent.setData(Uri.parse("https://wa.link/16oiet"));
                            startActivity(whatsappIntent);
                            break;

                        case 2: // Website
                            Intent webIntent = new Intent(Intent.ACTION_VIEW);
                            webIntent.setData(Uri.parse("https://www.funtravel.com"));
                            startActivity(webIntent);
                            break;

                        case 3: // Email
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:funtravelapp@example.com"));
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FunTravelApp Inquiry");
                            startActivity(emailIntent);
                            break;
                    }
                });
        builder.show();
    }
}
