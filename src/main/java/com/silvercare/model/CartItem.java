//Done By: Wan Jing Hng
//Description: Java Class file for CartItems
//Date: 22/1/2026
//Stores all the necessary items for CartItems


package com.silvercare.model;

import java.io.Serializable;
import java.util.Date;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int serviceId;
    private int caregiverId;
    private String serviceName;
    private String category;
    private double price;
    private Date serviceDate;   
    private String startTime;
    private double durationHours;
    private String specialRequests;
    private int cartItemId;
    private String caregiverName;
    private String selectedMeal;
    private String dietaryNotes;

    public CartItem() {}

    public CartItem(int serviceId, int caregiverId, String serviceName, String category, 
                    double price, Date serviceDate, String startTime, double durationHours, String specialRequests) {
        this.serviceId = serviceId;
        this.caregiverId = caregiverId;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
        this.serviceDate = serviceDate;
        this.startTime = startTime;
        this.durationHours = durationHours;
        this.specialRequests = specialRequests;
    }

    // --- Critical Logic Method for JSP ---
    public double getCost() {
        return this.price * this.durationHours;
    }

    public String getSelectedMeal() {
        return selectedMeal;
    }

    public void setSelectedMeal(String selectedMeal) {
        this.selectedMeal = selectedMeal;
    }

    public String getDietaryNotes() {
        return dietaryNotes;
    }

    public void setDietaryNotes(String dietaryNotes) {
        this.dietaryNotes = dietaryNotes;
    }
    
    // --- Standard Getters and Setters (Match these to JSP) ---
    
    public int getServiceID() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
    
    public int getCaregiverId() { return caregiverId; }
    public void setCaregiverId(int caregiverId) { this.caregiverId = caregiverId; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public Date getServiceDate() { return serviceDate; }
    public void setServiceDate(Date serviceDate) { this.serviceDate = serviceDate; }
    
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    
    public double getDurationHours() { return durationHours; }
    public void setDurationHours(double durationHours) { this.durationHours = durationHours; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public int getCaregiverID() {
        return this.caregiverId;
    }

    public java.util.Date getBookingDate() {
        return this.serviceDate; 
    }
    
    public int getCartItemId() {
        return cartItemId;
    }
    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }
    
    public String getCaregiverName() { return caregiverName; }
    public void setCaregiverName(String caregiverName) { this.caregiverName = caregiverName; }
}