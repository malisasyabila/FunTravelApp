package com.example.funtravelapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BookingHistoryActivity extends AppCompatActivity {

    private ArrayList<BookingItem> bookingList;
    private BookingHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        BookingData.loadBookings(getApplicationContext()); // Load from SharedPreferences

        RecyclerView rv = findViewById(R.id.booking_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingHistoryAdapter(BookingData.bookingList, new BookingHistoryAdapter.OnItemClickListener() {
            @Override
            public void onCancel(int position) {
                BookingItem b = BookingData.bookingList.get(position);
                BookingData.bookingList.remove(position);
                BookingData.saveBookings(getApplicationContext()); // Save after deletion
                adapter.notifyItemRemoved(position);
                Toast.makeText(BookingHistoryActivity.this, "Canceled: " + b.getDestination(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onView(int position) {
                BookingItem b = BookingData.bookingList.get(position);
                String detail = "Destination: " + b.getDestination() + "\n"
                        + "Name: " + b.getUserName() + "\n"
                        + "People: " + b.getPeople() + "\n"
                        + "Total Price: RM" + String.format("%.2f", b.getTotalPrice()) + "\n"
                        + "Visit Date: " + b.getVisitDate() + "\n"
                        + "Booking successfully!"; 

                if (!b.getNotes().isEmpty()) {
                    detail += "\nNotes: " + b.getNotes();
                }

                new androidx.appcompat.app.AlertDialog.Builder(BookingHistoryActivity.this)
                        .setTitle("Booking Details")
                        .setMessage(detail)
                        .setPositiveButton("OK", null)
                        .setNegativeButton("Download", (dialog, which) -> {
                            // ✅ Call method to handle file saving
                            downloadBookingDetails(b);
                        })
                        .show();
            }

            @Override
            public void onDelete(int position) {
                BookingItem item = BookingData.bookingList.get(position);
                new AlertDialog.Builder(BookingHistoryActivity.this)
                        .setTitle("Delete Booking")
                        .setMessage("Are you sure you want to delete booking for " + item.getDestination() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            BookingData.bookingList.remove(position);
                            BookingData.saveBookings(getApplicationContext());
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(BookingHistoryActivity.this, "Deleted: " + item.getDestination(), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            private void downloadBookingDetails(BookingItem b) {
                String detail = "----- Booking Receipt -----\n\n"
                        + "Destination: " + b.getDestination() + "\n"
                        + "Name: " + b.getUserName() + "\n"
                        + "People: " + b.getPeople() + "\n"
                        + "Total Price: RM" + String.format("%.2f", b.getTotalPrice()) + "\n"
                        + "Visit Date: " + b.getVisitDate() + "\n"
                        + "Booking successfully!\n";

                if (!b.getNotes().isEmpty()) {
                    detail += "Notes: " + b.getNotes() + "\n";
                }

                // HTML format for printing
                String htmlContent = "<html><body style='text-align:center; font-family:sans-serif;'>"
                        + "<h2 style='font-size:26px;'>Booking Details</h2>"
                        + "<pre style='text-align:left; display:inline-block; font-size:20px;'>"
                        + detail
                        + "</pre>"
                        + "</body></html>";

                // Create WebView dynamically
                WebView webView = new WebView(BookingHistoryActivity.this);
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Booking_Receipt");
                        printManager.print("Booking Receipt", printAdapter, new PrintAttributes.Builder().build());

                        // ✅ Close after printing
                        Toast.makeText(BookingHistoryActivity.this, "Download started", Toast.LENGTH_SHORT).show();
                    }
                });

                // Load content for printing
                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
            }
        });

        rv.setAdapter(adapter);
    }
}
