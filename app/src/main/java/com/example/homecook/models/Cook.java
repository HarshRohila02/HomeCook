package com.example.homecook.models;

public class Cook {
    private String cookId;
    private String name;
    private String cuisine;
    private String distance;
    private double rating;
    private int reviewsCount;
    private String imageName;
    private String about;
    private boolean available;
    private boolean hygieneVerified;

    // Empty constructor for Firestore
    public Cook() {
    }

    // Full constructor
    public Cook(String cookId, String name, String cuisine, String distance, double rating, int reviewsCount, String imageName, String about, boolean available, boolean hygieneVerified) {
        this.cookId = cookId;
        this.name = name;
        this.cuisine = cuisine;
        this.distance = distance;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
        this.imageName = imageName;
        this.about = about;
        this.available = available;
        this.hygieneVerified = hygieneVerified;
    }

    // Getters and Setters
    public String getCookId() { return cookId; }
    public void setCookId(String cookId) { this.cookId = cookId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isHygieneVerified() { return hygieneVerified; }
    public void setHygieneVerified(boolean hygieneVerified) { this.hygieneVerified = hygieneVerified; }
}
