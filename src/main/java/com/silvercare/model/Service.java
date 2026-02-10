//Done By: Wan Jing Hng
//Description: Model (Value Bean) - To represent a Service Object in our system
//Date: 6 Jan 2025 - Updated for Assignment 2 


package com.silvercare.model;

import java.io.Serializable;


public class Service implements Serializable {
    private int serviceId;
    private String serviceName;
    private String description;
    private double price;
    private int categoryId;
    private String imageLocation;
    private String categoryName; 

    public Service() {}

    // Getters and Setters
    public int getServiceId() { 
    	return serviceId; 
    	}
    
    public void setServiceId(int serviceId) {
    	this.serviceId = serviceId; 
    	}

   
    public String getServiceName() { 
    	return serviceName; 
    	}
    
    
    public void setServiceName(String serviceName) { 
    	this.serviceName = serviceName; 
    	}

   
    public String getDescription() {
    	return description; 
    	}
    
    public void setDescription(String description) { 
    	this.description = description; 
    	}

    public double getPrice() { 
    	return price; 
    	}
    
    public void setPrice(double price) {
    	this.price = price; 
    	}

    public int getCategoryId() { 
    	return categoryId; 
    	}
    
    public void setCategoryId(int categoryId) { 
    	this.categoryId = categoryId; 
    	}

    public String getImageLocation() {
    	return imageLocation; 
    	}
    
    public void setImageLocation(String imageLocation) { 
    	this.imageLocation = imageLocation; 
    	}

    public String getCategoryName() {
    	return categoryName; 
    	}
    
    public void setCategoryName(String categoryName) { 
    	this.categoryName = categoryName; 
    	}
    
}