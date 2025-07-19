package com.example.funtravelapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class RateBookingActivity extends AppCompatActivity {

    private ImageView selectedImage;
    private EditText commentInput;
    private RatingBar ratingBar;
    private Button btnUploadGallery, btnOpenCamera, btnSubmit;

    private Bitmap imageBitmap = null;
    private Uri imageUri = null;

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    selectedImage.setImageBitmap(imageBitmap);
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    selectedImage.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_booking);

        Button backBtn = findViewById(R.id.btn_back_rating);
        backBtn.setOnClickListener(v -> finish());

        selectedImage = findViewById(R.id.captured_image);
        commentInput = findViewById(R.id.comment_input);
        ratingBar = findViewById(R.id.rating_bar);
        btnUploadGallery = findViewById(R.id.btn_gallery);
        btnOpenCamera = findViewById(R.id.btn_camera);
        btnSubmit = findViewById(R.id.btn_submit_rating);

        btnUploadGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });

        btnOpenCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        });

        btnSubmit.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (comment.isEmpty() || rating == 0.0f || (imageBitmap == null && imageUri == null)) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Simpan/hantar maklumat ke server/database jika ada
                Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();
                finish(); // Tutup activity
            }
        });
    }
}

