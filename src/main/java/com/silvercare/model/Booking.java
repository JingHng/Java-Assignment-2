//Done By: Wan Jing Hng
//Description: Java Class file for Bookings
//Date: 22/1/2026
//Stores all the necessary items for Bookings


package com.silvercare.model;

import java.sql.Date; 

public class Booking {
    private int bookingId;
    private Date bookingDate;
    private double totalAmount;
    private String status;
    private String customerName;
    private String customerPhone; 
    private String serviceName;
    private String liveStatus;

    // --- Getters and Setters ---

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getLiveStatus() { return liveStatus; }
    public void setLiveStatus(String liveStatus) { this.liveStatus = liveStatus; }
}