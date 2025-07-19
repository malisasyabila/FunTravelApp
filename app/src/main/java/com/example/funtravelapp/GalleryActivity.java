package com.example.funtravelapp;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    private GridView galleryGridView;
    private ImageAdapter imageAdapter;
    private ArrayList<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        galleryGridView = findViewById(R.id.gallery_grid);
        imagePaths = fetchGalleryImages();

        if (imagePaths.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        }

        imageAdapter = new ImageAdapter(this, imagePaths);
        galleryGridView.setAdapter(imageAdapter);

        // Long click to delete image
        galleryGridView.setOnItemLongClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            String imagePath = (String) imageAdapter.getItem(position);
            showDeleteConfirmation(imagePath, position);
            return true;
        });
    }

    private ArrayList<String> fetchGalleryImages() {
        ArrayList<String> images = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            while (cursor.moveToNext()) {
                images.add(cursor.getString(columnIndex));
            }
            cursor.close();
        }

        return images;
    }

    private void showDeleteConfirmation(String imagePath, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage(imagePath, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage(String imagePath, int position) {
        ContentResolver contentResolver = getContentResolver();
        Uri imageUri = getImageContentUri(imagePath);

        if (imageUri != null) {
            int rowsDeleted = contentResolver.delete(imageUri, null, null);
            if (rowsDeleted > 0) {
                imageAdapter.remove(position);
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid image URI", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageContentUri(String imagePath) {
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=?",
                new String[]{imagePath},
                null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
        } else {
            return null;
        }
    }
}