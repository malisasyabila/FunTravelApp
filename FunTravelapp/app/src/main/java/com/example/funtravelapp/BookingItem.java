package com.example.funtravelapp;

public class BookingItem {
    private String destination;
    private String userName;
    private int people;
    private double pricePerPerson;
    private String visitDate;
    private String notes;
    private int imageResId; // ✅ Gambar

    public BookingItem(String destination, String userName, int people, double pricePerPerson, String visitDate, String notes, int imageResId) {
        this.destination = destination;
        this.userName = userName;
        this.people = people;
        this.pricePerPerson = pricePerPerson;
        this.visitDate = visitDate;
        this.notes = notes;
        this.imageResId = imageResId;
    }

    public String getDestination() { return destination; }
    public String getUserName() { return userName; }
    public int getPeople() { return people; }
    public double getPricePerPerson() { return pricePerPerson; }
    public String getVisitDate() { return visitDate; }
    public String getNotes() { return notes; }
    public double getTotalPrice() { return people * pricePerPerson; }
    public int getImageResId() { return imageResId; }
}
