package com.example.funtravelapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start scan immediately without showing any layout
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a QR or Barcode");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.setCaptureActivity(CaptureAct.class); // optional
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String scannedContent = result.getContents();

                // If it's a link, try open it
                if (scannedContent.startsWith("http://") || scannedContent.startsWith("https://")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scannedContent));
                    try {
                        startActivity(browserIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, "No browser found to open the link", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // For other content, show in Toast or handle differently
                    Toast.makeText(this, "Scanned: " + scannedContent, Toast.LENGTH_LONG).show();
                }

                finish(); // close activity
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
