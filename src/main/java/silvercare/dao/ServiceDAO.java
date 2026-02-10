//Done by: Wan Jing Hng 
//Date: 5/1/2026
//Description: Centralizes data access using the MVC pattern and Value Beans
//Handles all database operations related to services with transactional integrity.

package silvercare.dao;
import java.util.Map;           
import java.util.HashMap;       
import java.util.stream.Collectors;


import com.silvercare.model.Service;
import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    // Get all services with category information
    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT s.serviceid, s.servicename, s.description, s.price, s.categoryid, s.imagelocation, c.categoryname " +
                     "FROM service s LEFT JOIN service_category c ON s.categoryid = c.categoryid ORDER BY s.serviceid";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Service s = new Service();
                s.setServiceId(rs.getInt("serviceid"));
                s.setServiceName(rs.getString("servicename"));
                s.setDescription(rs.getString("description"));
                s.setPrice(rs.getDouble("price"));
                s.setCategoryId(rs.getInt("categoryid"));
                s.setImageLocation(rs.getString("imagelocation"));
                s.setCategoryName(rs.getString("categoryname"));
                services.add(s);
            }
        }
        return services;
    }

    // Get single service by ID with category
    public Service getServiceById(int id) throws SQLException {
        Service s = null;
        String sql = "SELECT s.*, c.categoryname FROM service s " +
                     "JOIN service_category c ON s.categoryid = c.categoryid " +
                     "WHERE s.serviceid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    s = new Service();
                    s.setServiceId(rs.getInt("serviceid"));
                    s.setServiceName(rs.getString("servicename"));
                    s.setDescription(rs.getString("description"));
                    s.setPrice(rs.getDouble("price"));
                    s.setCategoryId(rs.getInt("categoryid"));
                    s.setImageLocation(rs.getString("imagelocation"));
                    s.setCategoryName(rs.getString("categoryname"));
                }
            }
        }
        return s;
    }

    // Add new service with auto-assigned caregiver (transactional) - returns new service ID
    public int addService(Service s) throws SQLException {
        String insertSvc = "INSERT INTO service (servicename, description, price, categoryid, imagelocation) VALUES (?, ?, ?, ?, ?)";
        String rndCaregiver = "SELECT caregiverid FROM caregiver ORDER BY RANDOM() LIMIT 1";
        String assignCaregiver = "INSERT INTO service_caregiver (serviceid, caregiverid) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // Insert the new service
            int newId = 0;
            try (PreparedStatement pstmt = conn.prepareStatement(insertSvc, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, s.getServiceName());
                pstmt.setString(2, s.getDescription());
                pstmt.setDouble(3, s.getPrice());
                pstmt.setInt(4, s.getCategoryId());
                pstmt.setString(5, s.getImageLocation());
                pstmt.executeUpdate();

                ResultSet keys = pstmt.getGeneratedKeys();
                if (keys.next()) newId = keys.getInt(1);
            }

            // Auto-assign a random caregiver to the service
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(rndCaregiver)) {
                if (rs.next()) {
                    try (PreparedStatement pstmtAssign = conn.prepareStatement(assignCaregiver)) {
                        pstmtAssign.setInt(1, newId);
                        pstmtAssign.setInt(2, rs.getInt(1));
                        pstmtAssign.executeUpdate();
                    }
                }
            }

            conn.commit(); // COMMIT
            return newId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // ROLLBACK ON ERROR
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    // Update existing service details
    public boolean updateService(Service s) throws SQLException {
        String sql = "UPDATE service SET servicename=?, description=?, price=?, categoryid=?, imagelocation=? WHERE serviceid=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getServiceName());
            pstmt.setString(2, s.getDescription());
            pstmt.setDouble(3, s.getPrice());
            pstmt.setInt(4, s.getCategoryId());
            pstmt.setString(5, s.getImageLocation());
            pstmt.setInt(6, s.getServiceId());
            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete service with caregiver mapping cleanup (transactional)
    public boolean deleteService(int id) throws SQLException {
        String deleteJunction = "DELETE FROM service_caregiver WHERE serviceid = ?";
        String deleteFoodOptions = "DELETE FROM service_food_options WHERE service_id = ?";
        String deleteSvc = "DELETE FROM service WHERE serviceid = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // 1. Remove caregiver mappings
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteJunction)) {
                pstmt1.setInt(1, id);
                pstmt1.executeUpdate();
            }

            // 2. Remove food options
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteFoodOptions)) {
                pstmt2.setInt(1, id);
                pstmt2.executeUpdate();
            }

            // 3. Delete the service record
            int rowsDeleted = 0;
            try (PreparedStatement pstmt3 = conn.prepareStatement(deleteSvc)) {
                pstmt3.setInt(1, id);
                rowsDeleted = pstmt3.executeUpdate();
            }

            conn.commit();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    
    // Get time slots already booked for a service on a date
    public List<String> getBookedSlots(int serviceId, String date) {
        List<String> bookedSlots = new ArrayList<>();
        String sql = "SELECT bd.start_time FROM booking_details bd " +
                     "JOIN bookings b ON bd.bookingid = b.bookingid " +
                     "WHERE bd.serviceid = ? AND bd.service_date = CAST(? AS DATE) " +
                     "AND b.status != 'Cancelled'";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, serviceId);
            pstmt.setString(2, date);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Time startTime = rs.getTime("start_time");
                    if (startTime != null) {
                        // Format to HH:mm
                        bookedSlots.add(startTime.toString().substring(0, 5));
                    }
                }
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return bookedSlots;
    }

    // Get services by list of IDs
    public List<Service> getServicesByIds(List<Integer> ids) throws SQLException {
        List<Service> list = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return list;

        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT * FROM service WHERE serviceid IN (" + placeholders + ")";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < ids.size(); i++) {
                pstmt.setInt(i + 1, ids.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("serviceid"));
                    s.setServiceName(rs.getString("servicename"));
                    s.setPrice(rs.getDouble("price"));
                    s.setDescription(rs.getString("description"));
                    s.setImageLocation(rs.getString("imagelocation"));
                    list.add(s);
                }
            }
        }
        return list;
    }
    
    // Get caregivers who provide a specific service
    public List<Map<String, Object>> getCaregiversByService(int serviceId) throws SQLException {
        List<Map<String, Object>> caregivers = new ArrayList<>();
        String sql = "SELECT c.caregiverid, c.name, c.specialty FROM caregiver c " +
                     "JOIN service_caregiver sc ON c.caregiverid = sc.caregiverid " +
                     "WHERE sc.serviceid = ? AND c.status = 'active' ORDER BY c.name";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> cg = new HashMap<>();
                    cg.put("id", rs.getInt("caregiverid"));
                    cg.put("name", rs.getString("name"));
                    cg.put("specialty", rs.getString("specialty"));
                    caregivers.add(cg);
                }
            }
        }
        return caregivers;
    }
    
    // Get all service categories
    public List<Map<String, Object>> getAllCategories() throws SQLException {
        List<Map<String, Object>> categories = new ArrayList<>();
        String sql = "SELECT categoryid, categoryname FROM service_category ORDER BY categoryname";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("id", rs.getInt("categoryid"));
                cat.put("name", rs.getString("categoryname"));
                categories.add(cat);
            }
        }
        return categories;
    }
    
    // Get services excluding specific category (e.g., exclude meal delivery)
    public List<Service> getServicesExcludingCategory(int excludeCategoryId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT serviceid, servicename FROM service " +
                     "WHERE categoryid != ? " +
                     "ORDER BY servicename ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, excludeCategoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("serviceid"));
                    s.setServiceName(rs.getString("servicename"));
                    services.add(s);
                }
            }
        }
        return services;
    }
}