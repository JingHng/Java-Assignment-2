//Done By: Wan Jing Hng
//Description: Java Class file for DeliveryOrder
//Date: 22/1/2026
//Stores all the necessary items for DeliveryOrder


package com.silvercare.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliveryOrder {
    private int bookingId;
    
    private String deliveryStatus;
    
    private String meal;
    private String customer;
    private String address;

    public DeliveryOrder() {}

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getAddress() { 
        return address; 
    }

    public void setAddress(String address) { 
        this.address = address; 
    }
    public String getMeal() { return meal; }
    public void setMeal(String meal) { this.meal = meal; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
}