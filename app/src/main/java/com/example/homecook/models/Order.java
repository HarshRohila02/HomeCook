package com.example.homecook.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order {
    private String orderId;
    private String userId;
    private List<Map<String, Object>> items;
    private double subtotal;
    private double deliveryFee;
    private double totalAmount;
    private String deliveryType;
    private String address;
    private String paymentMethod;
    private String status;
    private Date createdAt;
    private String estimatedDelivery;

    public Order() {
    }

    public Order(String orderId, String userId, List<Map<String, Object>> items, double subtotal, double deliveryFee, double totalAmount, String deliveryType, String address, String paymentMethod, String status, Date createdAt, String estimatedDelivery) {
        this.orderId = orderId;
        this.userId = userId;
        this.items = items;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.totalAmount = totalAmount;
        this.deliveryType = deliveryType;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createdAt = createdAt;
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getDeliveryType() { return deliveryType; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}