// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for admin dashboard statistics and analytics
// Handles all database queries for admin dashboard metrics and reporting

package silvercare.dao;

import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.*;

public class AdminDAO {
    
    /**
     * Get count from a specific query
     * @param sql The SQL query to execute
     * @return The count result
     */
    public int getCount(String sql) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Get all dashboard statistics
     * @return Map containing all statistics
     */
    public Map<String, Integer> getDashboardStats() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stats.put("serviceCount", getCountFromStmt(stmt, "SELECT COUNT(*) FROM service"));
            stats.put("caregiverCount", getCountFromStmt(stmt, "SELECT COUNT(*) FROM caregiver"));
            stats.put("customerCount", getCountFromStmt(stmt, "SELECT COUNT(*) FROM customers"));
            stats.put("medicalCount", getCountFromStmt(stmt, 
                "SELECT COUNT(*) FROM customers WHERE medical_condition IS NOT NULL AND medical_condition != ''"));
        }
        
        return stats;
    }
    
    /**
     * Helper method to get count using existing statement
     */
    private int getCountFromStmt(Statement stmt, String sql) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
    
    /**
     * Get top services by popularity (booking count)
     * @param limit Number of results to return
     * @return List of service popularity data
     */
    public List<Map<String, Object>> getTopServicesByPopularity(int limit) throws SQLException {
        String sql = "SELECT s.servicename, COUNT(bd.serviceid) FROM booking_details bd " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "GROUP BY s.servicename ORDER BY 2 DESC LIMIT ?";
        return fetchReporting(sql, "count", limit);
    }
    
    /**
     * Get bottom services by popularity (booking count)
     * @param limit Number of results to return
     * @return List of service popularity data
     */
    public List<Map<String, Object>> getBottomServicesByPopularity(int limit) throws SQLException {
        String sql = "SELECT s.servicename, COUNT(bd.serviceid) FROM booking_details bd " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "GROUP BY s.servicename ORDER BY 2 ASC LIMIT ?";
        return fetchReporting(sql, "count", limit);
    }
    
    /**
     * Get top clients by service count
     * @param limit Number of results to return
     * @return List of client data
     */
    public List<Map<String, Object>> getTopClients(int limit) throws SQLException {
        String sql = "SELECT c.firstname || ' ' || c.lastname, COUNT(bd.detailid) " +
                     "FROM customers c JOIN bookings b ON c.customerid = b.customerid " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "GROUP BY c.customerid, c.firstname, c.lastname ORDER BY 2 DESC LIMIT ?";
        return fetchReporting(sql, "client", limit);
    }
    
    /**
     * Get bottom clients by service count
     * @param limit Number of results to return
     * @return List of client data
     */
    public List<Map<String, Object>> getBottomClients(int limit) throws SQLException {
        String sql = "SELECT c.firstname || ' ' || c.lastname, COUNT(bd.detailid) " +
                     "FROM customers c JOIN bookings b ON c.customerid = b.customerid " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "GROUP BY c.customerid, c.firstname, c.lastname ORDER BY 2 ASC LIMIT ?";
        return fetchReporting(sql, "client", limit);
    }
    
    /**
     * Get top services by revenue
     * @param limit Number of results to return
     * @return List of service revenue data
     */
    public List<Map<String, Object>> getTopServicesByRevenue(int limit) throws SQLException {
        String sql = "SELECT s.servicename, SUM(bd.subtotal) FROM booking_details bd " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "GROUP BY s.servicename ORDER BY 2 DESC LIMIT ?";
        return fetchReporting(sql, "revenue", limit);
    }
    
    /**
     * Get bottom services by revenue
     * @param limit Number of results to return
     * @return List of service revenue data
     */
    public List<Map<String, Object>> getBottomServicesByRevenue(int limit) throws SQLException {
        String sql = "SELECT s.servicename, SUM(bd.subtotal) FROM booking_details bd " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "GROUP BY s.servicename ORDER BY 2 ASC LIMIT ?";
        return fetchReporting(sql, "revenue", limit);
    }
    
    /**
     * Generic method for fetching reporting data
     * @param sql The SQL query to execute
     * @param type The type of report (count, client, revenue)
     * @param limit Number of results
     * @return List of report data
     */
    private List<Map<String, Object>> fetchReporting(String sql, String type, int limit) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", rs.getString(1));
                    double val = rs.getDouble(2);
                    
                    if (type.equals("count")) {
                        row.put("display", (int)val + " Bookings");
                    } else if (type.equals("client")) {
                        row.put("display", (int)val + " Services");
                    } else if (type.equals("revenue")) {
                        row.put("display", String.format("$%.2f", val));
                    }
                    list.add(row);
                }
            }
        }
        
        return list;
    }
}
