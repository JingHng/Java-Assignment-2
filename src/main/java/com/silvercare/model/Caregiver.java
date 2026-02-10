//Done By: Wan Jing Hng
//Description: Java Class file for Caregiver
//Date: 22/1/2026
//Stores all the necessary items for caregivers


package com.silvercare.model;

public class Caregiver extends User{
    private int caregiverId;
    private String name, email, phone, specialty, bio, image;
    private String serviceIdList;
    private String username;
    private String password;

    // Constructors, Getters, and Setters
    public Caregiver() {}
    public int getCaregiverId() { 
    	return caregiverId; 
    	}
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    
    public void setCaregiverId(int caregiverId) { 
    	this.caregiverId = caregiverId; 
    	}
    
    public String getName() { 
    	return name; 
    	}
    
    public void setName(String name) { 
    	this.name = name; 
    	}
    
    public String getServiceIdList() {
        return serviceIdList;
    }

    public void setServiceIdList(String serviceIdList) {
        this.serviceIdList = serviceIdList;
    }
    public String getEmail() { 
    	return email; 
    	}
    
    public void setEmail(String email) { 
    	this.email = email; 
    	}
    
    public String getPhone() { 
    	return phone; 
    	}
    
    public void setPhone(String phone) {
    	this.phone = phone; 
    	}
    
    public String getSpecialty() { 
    	return specialty; 
    	}
    
    public void setSpecialty(String specialty) { 
    	this.specialty = specialty; 
    	}
    
    public String getBio() { 
    	return bio; 
    	}
    
    public void setBio(String bio) { 
    	this.bio = bio; 
    	}
    
    public String getImage() {
    	return image; 
    	}
    
    public void setImage(String image) {
    	this.image = image; 
    	}
    
}