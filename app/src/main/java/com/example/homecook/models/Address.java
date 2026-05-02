package com.example.homecook.models;

import com.google.firebase.firestore.PropertyName;

public class Address {
    private String addressId;
    private String label;
    private String fullAddress;
    private String phone;
    private boolean isDefault;

    public Address() {}

    public Address(String addressId, String label, String fullAddress, String phone, boolean isDefault) {
        this.addressId = addressId;
        this.label = label;
        this.fullAddress = fullAddress;
        this.phone = phone;
        this.isDefault = isDefault;
    }

    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @PropertyName("isDefault")
    public boolean isDefault() { return isDefault; }

    @PropertyName("isDefault")
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
}
