package com.example.funtravelapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewStorage {
    private static ReviewStorage instance;
    private static final String PREF_NAME = "review_storage";
    private static final String KEY_REVIEWS = "reviews";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    private ReviewStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static ReviewStorage getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewStorage(context);
        }
        return instance;
    }

    public void addReview(ReviewModel review) {
        List<ReviewModel> reviews = getReviews();
        reviews.add(review);
        saveReviews(reviews);
    }

    public void removeReview(ReviewModel target) {
        List<ReviewModel> reviews = getReviews();
        reviews.remove(target); // make sure equals() works properly
        saveReviews(reviews);
    }

    public List<ReviewModel> getReviews() {
        try {
            String json = sharedPreferences.getString(KEY_REVIEWS, null);
            Type type = new TypeToken<List<ReviewModel>>() {}.getType();
            List<ReviewModel> reviews = gson.fromJson(json, type);
            return reviews != null ? reviews : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            // Clear corrupted data
            return new ArrayList<>();
        }
    }

    private void saveReviews(List<ReviewModel> reviews) {
        String json = gson.toJson(reviews);
        sharedPreferences.edit().putString(KEY_REVIEWS, json).apply();
    }
}
