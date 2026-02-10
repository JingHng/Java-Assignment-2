//Done By: Wan Jing Hng
//Description: Java Class file for FoodOptions
//Date: 22/1/2026
//Stores all the necessary items for FoodOptions


package com.silvercare.model;


public class FoodOption {
    private int optionId;
    private int serviceId;
    private String optionName;
    private int calories;

    public FoodOption(int optionId, String optionName) {
        this.optionId = optionId;
        this.optionName = optionName;
    }
    // Getters and Setters
    public String getOptionName() { return optionName; }
    public int getOptionId() { return optionId; }
}