package com.example.homecook.models;

public class CartItem {
    private String dishId;
    private String cookId;
    private String name;
    private double price;
    private int quantity;
    private String imageName;
    private String category;

    public CartItem() {
    }

    public CartItem(String dishId, String cookId, String name, double price, int quantity, String imageName, String category) {
        this.dishId = dishId;
        this.cookId = cookId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageName = imageName;
        this.category = category;
    }

    public String getDishId() { return dishId; }
    public void setDishId(String dishId) { this.dishId = dishId; }

    public String getCookId() { return cookId; }
    public void setCookId(String cookId) { this.cookId = cookId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}