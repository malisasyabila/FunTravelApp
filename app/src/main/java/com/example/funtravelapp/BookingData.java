package com.example.funtravelapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookingData {
    public static List<BookingItem> bookingList = new ArrayList<>();
    private static final String PREF_NAME = "BookingPrefs";
    private static final String KEY_BOOKINGS = "Bookings";

    public static void saveBookings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(bookingList);
        editor.putString(KEY_BOOKINGS, json);
        editor.apply();
    }

    public static void loadBookings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_BOOKINGS, null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<BookingItem>>() {}.getType();
            bookingList = gson.fromJson(json, type);
        } else {
            bookingList = new ArrayList<>();
        }
    }
}
