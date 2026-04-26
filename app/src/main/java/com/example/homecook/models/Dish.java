package com.example.homecook.models;

public class Dish {
    private String dishId;
    private String cookId;
    private String name;
    private double price;
    private String category;
    private String mealType;
    private String description;
    private String ingredients;
    private String imageName;
    private boolean available;

    // Empty constructor for Firestore
    public Dish() {
    }

    // Full constructor
    public Dish(String dishId, String cookId, String name, double price, String category, String mealType, String description, String ingredients, String imageName, boolean available) {
        this.dishId = dishId;
        this.cookId = cookId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.mealType = mealType;
        this.description = description;
        this.ingredients = ingredients;
        this.imageName = imageName;
        this.available = available;
    }

    // Getters and Setters
    public String getDishId() { return dishId; }
    public void setDishId(String dishId) { this.dishId = dishId; }

    public String getCookId() { return cookId; }
    public void setCookId(String cookId) { this.cookId = cookId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
