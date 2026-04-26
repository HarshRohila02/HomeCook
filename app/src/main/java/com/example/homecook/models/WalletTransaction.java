package com.example.homecook.models;

public class WalletTransaction {
    private String transactionId;
    private String title;
    private double amount;
    private String type; // "CREDIT" or "DEBIT"
    private long date;

    public WalletTransaction() {}

    public WalletTransaction(String transactionId, String title, double amount, String type, long date) {
        this.transactionId = transactionId;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
}
