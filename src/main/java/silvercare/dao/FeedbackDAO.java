// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for Feedback management operations
// Handles all database operations for feedback and their service mappings


package silvercare.dao;

import com.silvercare.util.DBConnection;
import com.silvercare.model.Feedback;
import java.sql.*;
import java.util.*;

public class FeedbackDAO {

    // Get all feedback with optional rating filter
    public List<Feedback> getAllFeedback(String minRating) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT f.*, c.firstName, c.lastName, s.serviceName " +
            "FROM feedback f " +
            "LEFT JOIN customers c ON f.customerid = c.customerid " + 
            "LEFT JOIN service s ON f.serviceid = s.serviceid " +
            "WHERE 1=1 "
        );

        if (minRating != null && !minRating.trim().isEmpty()) {
            sql.append("AND f.rating >= ? ");
        }
        sql.append("ORDER BY f.feedbackdate DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            if (minRating != null && !minRating.trim().isEmpty()) {
                pstmt.setInt(1, Integer.parseInt(minRating));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback f = new Feedback();
                    f.setFeedbackId(rs.getInt("feedbackid"));
                    f.setRating(rs.getInt("rating"));
                    f.setComments(rs.getString("comments"));
                    f.setFeedbackDate(rs.getTimestamp("feedbackdate"));
                    f.setBookingId(rs.getInt("bookingid"));
                    f.setServiceId(rs.getInt("serviceid"));
                    f.setCustomerName(rs.getString("firstName") + " " + rs.getString("lastName"));
                    f.setServiceName(rs.getString("serviceName"));
                    f.setAdminReply(rs.getString("admin_reply"));
                    list.add(f);
                }
            }
        }
        return list;
    }

    // Get bookings eligible for feedback (past date, no existing feedback)
    public List<Map<String, Object>> getEligibleBookings(int customerId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        // Query completed bookings without feedback
        String sql = "SELECT b.bookingid, bd.serviceid, s.servicename, bd.service_date " +
                "FROM bookings b " +
                "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                "JOIN service s ON bd.serviceid = s.serviceid " +
                "WHERE b.customerid = ? " +
                "AND b.status IN ('Confirmed', 'Completed') " + // Confirmed or completed bookings
                "AND bd.service_date < CURRENT_DATE " +         // Date has passed
                "AND b.bookingid NOT IN (SELECT bookingid FROM feedback)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookingId", rs.getInt("bookingid"));
                    row.put("serviceId", rs.getInt("serviceid"));
                    row.put("serviceName", rs.getString("servicename"));
                    row.put("serviceDate", rs.getTimestamp("service_date")); 
                    list.add(row);
                }
            }
        }
        return list;
    }

    // Get feedback submitted by a customer
    public List<Feedback> getSubmittedFeedback(int customerid) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        // Get all feedback with admin replies
        String sql = "SELECT f.*, s.servicename FROM feedback f " +
                     "JOIN service s ON f.serviceid = s.serviceid " + 
                     "WHERE f.customerid = ? ORDER BY f.feedbackdate DESC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerid);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Feedback f = new Feedback();
                    f.setFeedbackId(rs.getInt("feedbackid"));
                    f.setRating(rs.getInt("rating"));
                    f.setComments(rs.getString("comments"));
                    f.setServiceName(rs.getString("servicename"));
                    f.setFeedbackDate(rs.getTimestamp("feedbackdate"));                     
                    f.setAdminReply(rs.getString("admin_reply")); 
                    
                    list.add(f);
                }
            }
        }
        return list;
    }

    // Insert new feedback for a booking
    public boolean insertFeedback(int bookingId, int customerId, int rating, String comments, int caregiverId, int serviceId) throws SQLException {
        String sql = "INSERT INTO feedback (bookingid, customerid, rating, comments, feedbackdate, caregiverid, serviceid) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comments);
            // Handle optional caregiver
            if (caregiverId > 0) pstmt.setInt(5, caregiverId); 
            else pstmt.setNull(5, Types.INTEGER);
            pstmt.setInt(6, serviceId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Delete feedback by ID
    public boolean deleteFeedback(int feedbackId) throws SQLException {
        String sql = "DELETE FROM feedback WHERE feedbackid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feedbackId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Update admin reply for feedback
    public boolean updateAdminReply(int feedbackId, String reply) throws SQLException {
        String sql = "UPDATE feedback SET admin_reply = ? WHERE feedbackid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, reply);
            pstmt.setInt(2, feedbackId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Get eligible booking by specific booking ID (for direct feedback)
    public List<Map<String, Object>> getEligibleBookingById(int customerId, int bookingId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        // Check if booking is eligible (not cancelled, no existing feedback)
        String sql = "SELECT b.bookingid, bd.serviceid, s.servicename " +
                     "FROM bookings b " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "WHERE b.customerid = ? AND b.bookingid = ? " +
                     "AND b.status != 'Cancelled' " +
                     "AND (SELECT COUNT(*) FROM feedback f WHERE f.bookingid = b.bookingid) = 0";

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, bookingId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingId", rs.getInt("bookingid"));
                    map.put("serviceId", rs.getInt("serviceid"));
                    map.put("serviceName", rs.getString("servicename"));
                    list.add(map);
                }
            }
        }
        return list;
    }
    
    // Save feedback from customer
    public boolean saveFeedback(int bookingId, int customerId, int serviceId, int rating, String comments) throws SQLException {
        String sql = "INSERT INTO feedback (bookingid, customerid, serviceid, rating, comments, feedbackdate) VALUES (?, ?, ?, ?, ?, NOW())";
        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, customerId);
            pstmt.setInt(3, serviceId);
            pstmt.setInt(4, rating);
            pstmt.setString(5, comments);
            return pstmt.executeUpdate() > 0;
        }
    }
}