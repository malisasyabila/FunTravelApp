package com.example.funtravelapp;

/**
 * Model class representing a Booking stored in Firebase Realtime Database.
 * Fields must be public or have getters (we use private + getters).
 * Must include a NO-ARG constructor for Firebase deserialization.
 */
public class BookingItem {

    private String bookingId;        // Firebase push key
    private String destinationName;  // Destination name
    private String userName;         // Full name entered by user
    private String email;            // User email
    private int people;              // Number of people
    private double pricePerPerson;   // Unit price
    private double totalPrice;       // Calculated total price (people * pricePerPerson)
    private String visitDate;        // Date string (dd/MM/yyyy)
    private String notes;            // Optional notes
    private String imageResName;     // Drawable resource *name* (e.g. "pulau_langkawi")
    private long createdAt;          // Timestamp (System.currentTimeMillis)

    // ---- REQUIRED no-arg constructor for Firebase ----
    public BookingItem() {
    }

    // ---- Full constructor ----
    public BookingItem(String bookingId,
                       String destinationName,
                       String userName,
                       String email,
                       int people,
                       double pricePerPerson,
                       double totalPrice,
                       String visitDate,
                       String notes,
                       String imageResName,
                       long createdAt) {
        this.bookingId = bookingId;
        this.destinationName = destinationName;
        this.userName = userName;
        this.email = email;
        this.people = people;
        this.pricePerPerson = pricePerPerson;
        this.totalPrice = totalPrice;
        this.visitDate = visitDate;
        this.notes = notes;
        this.imageResName = imageResName;
        this.createdAt = createdAt;
    }

    // ---- Getters (Firebase uses these) ----
    public String getBookingId() { return bookingId; }
    public String getDestinationName() { return destinationName; }
    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public int getPeople() { return people; }
    public double getPricePerPerson() { return pricePerPerson; }
    public double getTotalPrice() { return totalPrice; }
    public String getVisitDate() { return visitDate; }
    public String getNotes() { return notes; }
    public String getImageResName() { return imageResName; }
    public long getCreatedAt() { return createdAt; }

    // (Optional) If you later need setters for updates, you can add them.
}
