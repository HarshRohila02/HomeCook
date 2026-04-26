package com.example.homecook.models;

import java.io.Serializable;

public class SubscriptionPlan implements Serializable {
    private String name;
    private double price;
    private int durationDays;
    private String description;

    public SubscriptionPlan(String name, double price, int durationDays, String description) {
        this.name = name;
        this.price = price;
        this.durationDays = durationDays;
        this.description = description;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getDurationDays() { return durationDays; }
    public String getDescription() { return description; }
}