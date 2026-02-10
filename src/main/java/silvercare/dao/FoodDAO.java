// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for Food management operations
// Handles all database operations for food and their service mappings


package silvercare.dao;

import com.silvercare.model.FoodOption;
import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.*;

public class FoodDAO {
    
    // Get available food options for a service
    public List<FoodOption> getFoodOptionsByService(int serviceId) {
        List<FoodOption> options = new ArrayList<>();
        String sql = "SELECT * FROM service_food_options WHERE service_id = ? AND is_available = TRUE";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                options.add(new FoodOption(
                    rs.getInt("option_id"),
                    rs.getString("option_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching food options in FoodDAO: " + e.getMessage());
            e.printStackTrace();
        }
        return options;
    }
    
    // Get all food options including unavailable (for admin management)
    public List<Map<String, Object>> getAllFoodOptionsByService(int serviceId) {
        List<Map<String, Object>> options = new ArrayList<>();
        String sql = "SELECT option_id, option_name, calories, is_available FROM service_food_options WHERE service_id = ? ORDER BY option_name";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> option = new HashMap<>();
                option.put("optionId", rs.getInt("option_id"));
                option.put("optionName", rs.getString("option_name"));
                option.put("calories", rs.getInt("calories"));
                option.put("isAvailable", rs.getBoolean("is_available"));
                options.add(option);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all food options: " + e.getMessage());
            e.printStackTrace();
        }
        return options;
    }
    
    // Add new food option for a service
    public boolean addFoodOption(int serviceId, String optionName, int calories) {
        String sql = "INSERT INTO service_food_options (service_id, option_name, calories, is_available) VALUES (?, ?, ?, TRUE)";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            pstmt.setString(2, optionName);
            pstmt.setInt(3, calories);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding food option: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Update existing food option details
    public boolean updateFoodOption(int optionId, String optionName, int calories) {
        String sql = "UPDATE service_food_options SET option_name = ?, calories = ? WHERE option_id = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, optionName);
            pstmt.setInt(2, calories);
            pstmt.setInt(3, optionId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating food option: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Soft delete food option (set unavailable)
    public boolean deleteFoodOption(int optionId) {
        String sql = "UPDATE service_food_options SET is_available = FALSE WHERE option_id = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, optionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting food option: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Restore previously deleted food option
    public boolean restoreFoodOption(int optionId) {
        String sql = "UPDATE service_food_options SET is_available = TRUE WHERE option_id = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, optionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error restoring food option: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}