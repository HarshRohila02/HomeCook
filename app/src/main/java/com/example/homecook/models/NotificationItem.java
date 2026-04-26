package com.example.homecook.models;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private long timestamp;
    private boolean read;

    public NotificationItem() {}

    public NotificationItem(String id, String title, String message, long timestamp, boolean read) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
