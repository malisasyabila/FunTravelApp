package com.example.funtravelapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;

public class CameraActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnTakePhoto;
    private Bitmap capturedBitmap;

    ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        imageView = findViewById(R.id.captured_image);
        btnTakePhoto = findViewById(R.id.btn_take_photo);

        // Prepare camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        capturedBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(capturedBitmap);
                        askToSaveImage(); // Prompt to save
                    }
                }
        );

        btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                cameraLauncher.launch(takePictureIntent);
            }
        });
    }

    private void askToSaveImage() {
        new AlertDialog.Builder(this)
                .setTitle("Sava Picture")
                .setMessage("Do you want to save the picture to the gallery?")
                .setPositiveButton("Yes", (dialog, which) -> saveImageToGallery())
                .setNegativeButton("No", null)
                .show();
    }

    private void saveImageToGallery() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "FunTravel_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Toast.makeText(this, "Picture saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save picture", Toast.LENGTH_SHORT).show();
        }
    }
}
