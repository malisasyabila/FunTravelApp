package com.example.funtravelapp;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;
import java.util.Objects;

public class ReviewModel implements Serializable {
    private String destinationName;
    private String imageUriString;
    private String comment;
    private float rating;
    private String dateTime;

    public ReviewModel(String destinationName, Uri imageUri, String comment, float rating, String dateTime) {
        this.destinationName = destinationName;
        this.imageUriString = imageUri != null ? imageUri.toString() : null;
        this.comment = comment;
        this.rating = rating;
        this.dateTime = dateTime;
    }

    public String getDestinationName() { return destinationName; }
    public String getImageUriString() { return imageUriString; }
    public String getComment() { return comment; }
    public float getRating() { return rating; }
    public String getDateTime() { return dateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewModel)) return false;

        ReviewModel that = (ReviewModel) o;
        return Float.compare(that.rating, rating) == 0 &&
                Objects.equals(destinationName, that.destinationName) &&
                Objects.equals(imageUriString, that.imageUriString) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destinationName, imageUriString, comment, rating, dateTime);
    }
}

