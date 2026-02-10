// Done By: Wan Jing Hng
// Description: Model (Value Bean) - Updated for Assignment 2 Requirements
// Date: 1 Feb 2026 

package com.silvercare.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int userId; 
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String language;
    
    // NEW Fields for CRUD & Reporting requirements
    private String postalCode;
    private String medicalCondition;
    private String emergencyName;
    private String emergencyPhone;

    public User() {}

    // Existing Getters/Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    // New Getters/Setters for Assignment 2
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getMedicalCondition() { return medicalCondition; }
    public void setMedicalCondition(String medicalCondition) { this.medicalCondition = medicalCondition; }
    public String getEmergencyName() { return emergencyName; }
    public void setEmergencyName(String emergencyName) { this.emergencyName = emergencyName; }
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
}