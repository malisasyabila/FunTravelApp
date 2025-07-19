package com.example.funtravelapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RateBookingActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;

    private ImageView selectedImage;
    private EditText commentInput;
    private RatingBar ratingBar;
    private Button btnUploadGallery, btnOpenCamera, btnSubmit;

    private Uri imageUri = null; // Store image URI
    private String destinationName = "Unknown Destination";

    // Handle camera result
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null && result.getData().getExtras() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        if (bitmap != null) {
                            Uri savedUri = saveImageToCache(bitmap);
                            if (savedUri != null) {
                                imageUri = savedUri;
                                selectedImage.setImageURI(imageUri);
                            } else {
                                Toast.makeText(this, "Failed to save camera image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });

    // Handle gallery result
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        Uri savedUri = copyGalleryImageToCache(selectedUri);
                        if (savedUri != null) {
                            imageUri = savedUri;
                            selectedImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(this, "Failed to save selected image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    // Save captured bitmap to cache
    private Uri saveImageToCache(Bitmap bitmap) {
        try {
            File cachePath = new File(getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            File file = new File(cachePath, "camera_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Copy gallery image to cache
    private Uri copyGalleryImageToCache(Uri sourceUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            File cachePath = new File(getCacheDir(), "images");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }

            File file = new File(cachePath, "gallery_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_booking);

        // Get destination name from intent
        if (getIntent() != null && getIntent().hasExtra("destination")) {
            destinationName = getIntent().getStringExtra("destination");
        }

        // UI components
        selectedImage = findViewById(R.id.captured_image);
        commentInput = findViewById(R.id.comment_input);
        ratingBar = findViewById(R.id.rating_bar);
        btnUploadGallery = findViewById(R.id.btn_gallery);
        btnOpenCamera = findViewById(R.id.btn_camera);
        btnSubmit = findViewById(R.id.btn_submit_rating);
        Button backBtn = findViewById(R.id.btn_back_rating);

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Open gallery
        btnUploadGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });

        // Open camera (check permission)
        btnOpenCamera.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        // Submit review
        btnSubmit.setOnClickListener(v -> {
            String comment = commentInput.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (comment.isEmpty() || rating == 0.0f || imageUri == null) {
                Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDateTime = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
            ReviewModel reviewModel = new ReviewModel(destinationName, imageUri, comment, rating, currentDateTime);
            ReviewStorage.getInstance(this).addReview(reviewModel);

            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RateBookingActivity.this, ReviewRateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
