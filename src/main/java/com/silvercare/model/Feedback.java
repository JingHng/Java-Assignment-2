//Done By: Wan Jing Hng
//Description: Java Class file for Feedback
//Date: 22/1/2026
//Stores all the necessary items for Feedback


package com.silvercare.model;

import java.io.Serializable;
import java.util.Date;

public class Feedback implements Serializable {
    private int feedbackId;
    private int bookingId;
    private String customerName; 
    private String serviceName;  
    private int rating;
    private String comments;
    private Date feedbackDate;
    private int serviceId;
    private Date serviceDate; 
    private String adminReply;

    
    public Feedback() {}

    // Getters and Setters
    public String getAdminReply() {
        return adminReply;
    }



    public void setAdminReply(String adminReply) {
        this.adminReply = adminReply;
    }
    

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Date getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(Date feedbackDate) { this.feedbackDate = feedbackDate; }
    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }
}