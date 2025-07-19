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

import androidx.appcompat.app.AppCompatActivity;

public class BookingPrintActivity extends AppCompatActivity {

    private WebView webView;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String details = getIntent().getStringExtra("booking_details");

        if (details != null) {
            String htmlContent = "<html><body style='text-align:center; font-family:sans-serif; font-size:18px;'>"
                    + "<h2 style='font-size:26px;'>Booking Details</h2>"
                    + "<pre style='text-align:left; display:inline-block; font-size:18px;'>"
                    + details
                    + "</pre>"
                    + "</body></html>";

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    createWebPrintJob(view); // ✅ Auto print
                }
            });

            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } else {
            Toast.makeText(this, "No booking data available.", Toast.LENGTH_SHORT).show();
        }

        // Close button back to history
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookingPrintActivity.this, BookingHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Booking_Receipt");
        printManager.print("Booking Receipt", printAdapter, new PrintAttributes.Builder().build());
    }
}
