package com.example.funtravelapp;

public class Destination {
    private String name;
    private String location;
    private String price;
    private float rating;
    private int imageResId;

    public Destination(String name, String location, String price, float rating, int imageResId) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getImageResId() {
        return imageResId;
    }
}
