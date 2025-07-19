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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Displays the user's booking history by reading from Firebase Realtime Database.
 * Provides actions: view details, download (print), cancel, delete, and highlight new booking.
 */
public class BookingHistoryActivity extends AppCompatActivity {

    // UI
    private RecyclerView recyclerView;
    private Button backBtn;

    // Data list bound to the adapter
    private final ArrayList<BookingItem> bookingList = new ArrayList<>();
    private BookingHistoryAdapter adapter;

    // Firebase
    private DatabaseReference bookingsRef;
    private ValueEventListener bookingsListener;

    // Optional: ID of a newly created booking to highlight
    private String highlightBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // Retrieve booking ID passed from InformationBooking (if any)
        highlightBookingId = getIntent().getStringExtra("new_booking_id");

        // Initialize UI components
        backBtn = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.booking_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with callbacks
        adapter = new BookingHistoryAdapter(bookingList, new BookingHistoryAdapter.OnItemClickListener() {
            @Override
            public void onCancel(int position) {
                // Treat "cancel" similar to delete but with different dialog wording
                deleteOrCancelBooking(position, true);
            }

            @Override
            public void onView(int position) {
                if (position < 0 || position >= bookingList.size()) return;
                showDetailDialog(bookingList.get(position));
            }

            @Override
            public void onDelete(int position) {
                deleteOrCancelBooking(position, false);
            }
        });
        recyclerView.setAdapter(adapter);

        // Handle back button tap -> return to MainDashboardActivity
        backBtn.setOnClickListener(v -> goBackToDashboard());

        // Start listening to Firebase bookings
        initFirebaseListener();
    }

    /**
     * Set up Firebase listener to retrieve bookings under /bookings/{uid}.
     */
    private void initFirebaseListener() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid;

        // Ensure user is authenticated (Anonymous Auth is acceptable)
        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
        } else {
            // If rules require auth != null and user is not signed in, this will fail.
            // You should ensure sign-in (anonymous) before opening this Activity.
            uid = "guest"; // Only valid if your rules allow guest access (NOT recommended for production).
        }

        bookingsRef = FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(uid);

        // Keep this node synced locally if offline persistence is enabled (MyApp with setPersistenceEnabled)
        bookingsRef.keepSynced(true);

        // Define the listener
        bookingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    BookingItem item = child.getValue(BookingItem.class);
                    if (item != null) {
                        bookingList.add(item);
                    }
                }

                // Sort newest first based on createdAt (descending)
                Collections.sort(bookingList, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));

                adapter.notifyDataSetChanged();

                // Highlight newly added booking (scroll to it and change background)
                if (highlightBookingId != null) {
                    int index = findBookingIndex(highlightBookingId);
                    if (index != -1) {
                        recyclerView.scrollToPosition(index);
                        adapter.setHighlightId(highlightBookingId);
                    }
                    highlightBookingId = null; // Only highlight once
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingHistoryActivity.this,
                        "Failed to load bookings: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        // Attach listener
        bookingsRef.addValueEventListener(bookingsListener);
    }

    /**
     * Show a dialog with full booking details and option to download/print.
     */
    private void showDetailDialog(BookingItem b) {
        String detail = "Destination: " + safe(b.getDestinationName()) + "\n"
                + "Name: " + safe(b.getUserName()) + "\n"
                + "Email: " + safe(b.getEmail()) + "\n"
                + "People: " + b.getPeople() + "\n"
                + "Price / Person: RM" + format2(b.getPricePerPerson()) + "\n"
                + "Total Price: RM" + format2(b.getTotalPrice()) + "\n"
                + "Visit Date: " + safe(b.getVisitDate()) + "\n"
                + "Booking successfully!";

        if (b.getNotes() != null && !b.getNotes().trim().isEmpty()) {
            detail += "\nNotes: " + b.getNotes();
        }

        new AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(detail)
                .setPositiveButton("Cancel", null)
                .setNegativeButton("Print", (dialog, which) -> downloadBookingDetails(b))
                .show();
    }

    /**
     * Generate a printable / savable HTML receipt using Android print framework.
     */
    private void downloadBookingDetails(BookingItem b) {
        String detail = "----- Booking Receipt -----\n\n"
                + "Destination: " + safe(b.getDestinationName()) + "\n"
                + "Name: " + safe(b.getUserName()) + "\n"
                + "Email: " + safe(b.getEmail()) + "\n"
                + "People: " + b.getPeople() + "\n"
                + "Price / Person: RM" + format2(b.getPricePerPerson()) + "\n"
                + "Total Price: RM" + format2(b.getTotalPrice()) + "\n"
                + "Visit Date: " + safe(b.getVisitDate()) + "\n"
                + "Booking successfully!\n";

        if (b.getNotes() != null && !b.getNotes().trim().isEmpty()) {
            detail += "Notes: " + b.getNotes() + "\n";
        }

        String htmlContent = "<html><body style='text-align:center; font-family:sans-serif;'>"
                + "<h2 style='font-size:26px;'>Booking Details</h2>"
                + "<pre style='text-align:left; display:inline-block; font-size:18px; line-height:1.3;'>"
                + detail
                + "</pre>"
                + "</body></html>";

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                PrintManager pm = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Booking_Receipt");
                pm.print("Booking Receipt", printAdapter, new PrintAttributes.Builder().build());
                Toast.makeText(BookingHistoryActivity.this, "Download started", Toast.LENGTH_SHORT).show();
            }
        });
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    /**
     * Delete or cancel a booking (confirmation dialog included).
     *
     * @param position Index in the adapter list
     * @param isCancel true = Cancel wording, false = Delete wording
     */
    private void deleteOrCancelBooking(int position, boolean isCancel) {
        if (position < 0 || position >= bookingList.size()) return;

        BookingItem item = bookingList.get(position);
        if (item.getBookingId() == null) {
            Toast.makeText(this, "No bookingId found for this item.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = isCancel ? "Cancel Booking" : "Delete Booking";
        String message = (isCancel ? "Cancel" : "Delete") + " booking for \"" + item.getDestinationName() + "\"?";

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("No", null)
                .setPositiveButton(isCancel ? "Cancel Booking" : "Delete", (dialog, which) -> {
                    bookingsRef.child(item.getBookingId()).removeValue()
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(this,
                                            (isCancel ? "Canceled: " : "Deleted: ") + item.getDestinationName(),
                                            Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .show();
    }

    /**
     * Locate the index of a booking by its bookingId.
     */
    private int findBookingIndex(String bookingId) {
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingId.equals(bookingList.get(i).getBookingId())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Utility: prevent null string outputs.
     */
    private String safe(String s) {
        return s == null ? "-" : s;
    }

    /**
     * Utility: format double with two decimals.
     */
    private String format2(double value) {
        return String.format("%.2f", value);
    }

    /**
     * Navigate back to MainDashboardActivity explicitly.
     */
    private void goBackToDashboard() {
        Intent intent = new Intent(BookingHistoryActivity.this, MainDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Maintain your original back navigation behavior
        goBackToDashboard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detach Firebase listener to avoid leaks
        if (bookingsRef != null && bookingsListener != null) {
            bookingsRef.removeEventListener(bookingsListener);
        }
    }
}
