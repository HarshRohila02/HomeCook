package com.example.homecook.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private String foodPreference;
    private String allergies;
    private String healthGoals;
    private boolean profileSetupCompleted;
    private long createdAt;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public String getFoodPreference() { return foodPreference; }
    public void setFoodPreference(String foodPreference) { this.foodPreference = foodPreference; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getHealthGoals() { return healthGoals; }
    public void setHealthGoals(String healthGoals) { this.healthGoals = healthGoals; }
    public boolean isProfileSetupCompleted() { return profileSetupCompleted; }
    public void setProfileSetupCompleted(boolean profileSetupCompleted) { this.profileSetupCompleted = profileSetupCompleted; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}