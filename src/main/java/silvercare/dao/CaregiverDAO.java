// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for caregiver management operations
// Handles all database operations for caregivers and their service mappings

package silvercare.dao;

import com.silvercare.model.Caregiver;
import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaregiverDAO {
    
    /**
     * Get all caregivers with their mapped service IDs
     * @return List of Caregiver objects
     */
    public List<Caregiver> getAllCaregiversWithServices() throws SQLException {
        List<Caregiver> caregiverList = new ArrayList<>();
        
        String sql = "SELECT *, " +
                     "(SELECT STRING_AGG(serviceid::text, ',') " +
                     " FROM service_caregiver " +
                     " WHERE caregiverid = c.caregiverid) AS serviceIdList " +
                     "FROM caregiver c ORDER BY caregiverid DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Caregiver c = new Caregiver();
                c.setCaregiverId(rs.getInt("caregiverid"));
                c.setName(rs.getString("name"));
                c.setEmail(rs.getString("email"));
                c.setPhone(rs.getString("phone"));
                c.setSpecialty(rs.getString("specialty"));
                c.setBio(rs.getString("bio"));
                c.setImage(rs.getString("image"));
                c.setServiceIdList(rs.getString("serviceIdList"));
                caregiverList.add(c);
            }
        }
        
        return caregiverList;
    }
    
    /**
     * Add a new caregiver
     * @param name Full name
     * @param email Email address
     * @param phone Phone number
     * @param specialty Specialty/expertise
     * @param bio Biography/description
     * @param imagePath Path to profile image
     * @param username Login username
     * @param password Login password
     * @param serviceIds Array of service IDs to map
     * @return The generated caregiver ID
     */
    public int addCaregiver(String name, String email, String phone, String specialty, 
                           String bio, String imagePath, String username, String password, 
                           String[] serviceIds) throws SQLException {
        
        Connection conn = null;
        int caregiverId = -1;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String sql = "INSERT INTO caregiver (name, email, phone, specialty, bio, image, username, password, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setString(4, specialty);
                pstmt.setString(5, bio);
                pstmt.setString(6, imagePath.isEmpty() ? "ASSIGNMENT/Images/defaultImage.jpg" : imagePath);
                pstmt.setString(7, username);
                pstmt.setString(8, password);
                pstmt.setString(9, "active");
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    caregiverId = rs.getInt(1);
                    insertServiceMappings(conn, caregiverId, serviceIds);
                }
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return caregiverId;
    }
    
    /**
     * Update an existing caregiver
     * @param caregiverId Caregiver ID
     * @param name Full name
     * @param email Email address
     * @param phone Phone number
     * @param specialty Specialty/expertise
     * @param bio Biography/description
     * @param imagePath Path to profile image (empty string means no update)
     * @param serviceIds Array of service IDs to map
     * @return true if update successful
     */
    public boolean updateCaregiver(int caregiverId, String name, String email, String phone, 
                                   String specialty, String bio, String imagePath, 
                                   String[] serviceIds) throws SQLException {
        
        Connection conn = null;
        boolean success = false;
        
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            boolean hasNewImage = !imagePath.isEmpty();
            String sql = hasNewImage ? 
                "UPDATE caregiver SET name=?, email=?, phone=?, specialty=?, bio=?, image=? WHERE caregiverid=?" :
                "UPDATE caregiver SET name=?, email=?, phone=?, specialty=?, bio=? WHERE caregiverid=?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setString(2, email);
                pstmt.setString(3, phone);
                pstmt.setString(4, specialty);
                pstmt.setString(5, bio);
                if (hasNewImage) {
                    pstmt.setString(6, imagePath);
                    pstmt.setInt(7, caregiverId);
                } else {
                    pstmt.setInt(6, caregiverId);
                }
                pstmt.executeUpdate();
            }
            
            // Delete existing service mappings
            deleteServiceMappings(conn, caregiverId);
            
            // Insert new service mappings
            insertServiceMappings(conn, caregiverId, serviceIds);
            
            conn.commit();
            success = true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return success;
    }
    
    /**
     * Delete a caregiver and their service mappings
     * @param caregiverId Caregiver ID to delete
     * @return true if deletion successful
     */
    public boolean deleteCaregiver(int caregiverId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Delete service mappings first (foreign key constraint)
                deleteServiceMappings(conn, caregiverId);
                
                // Delete caregiver
                String sql = "DELETE FROM caregiver WHERE caregiverid=?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, caregiverId);
                    pstmt.executeUpdate();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    /**
     * Insert service mappings for a caregiver
     * @param conn Database connection
     * @param caregiverId Caregiver ID
     * @param serviceIds Array of service IDs
     */
    private void insertServiceMappings(Connection conn, int caregiverId, String[] serviceIds) throws SQLException {
        if (serviceIds != null && serviceIds.length > 0) {
            String sql = "INSERT INTO service_caregiver (caregiverid, serviceid) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (String sId : serviceIds) {
                    pstmt.setInt(1, caregiverId);
                    pstmt.setInt(2, Integer.parseInt(sId));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
        }
    }
    
    /**
     * Delete all service mappings for a caregiver
     * @param conn Database connection
     * @param caregiverId Caregiver ID
     */
    private void deleteServiceMappings(Connection conn, int caregiverId) throws SQLException {
        String sql = "DELETE FROM service_caregiver WHERE caregiverid=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverId);
            pstmt.executeUpdate();
        }
    }
}
