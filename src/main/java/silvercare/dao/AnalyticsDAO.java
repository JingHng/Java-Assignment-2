// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for service analytics and reporting
// Handles database queries for generating charts and analytics data

package silvercare.dao;

import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.*;

public class AnalyticsDAO {
    
    /**
     * Get service booking analytics with optional date filtering
     * @param startDate Start date filter (can be null)
     * @param endDate End date filter (can be null)
     * @return Map of service names to booking counts
     */
    public Map<String, Integer> getServiceBookingAnalytics(String startDate, String endDate) throws SQLException {
        Map<String, Integer> analytics = new LinkedHashMap<>();
        
        // Build dynamic SQL with date filtering
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT s.servicename, COUNT(bd.bookingid) as total ");
        sql.append("FROM service s ");
        sql.append("LEFT JOIN booking_details bd ON s.serviceid = bd.serviceid ");
        sql.append("WHERE 1=1 ");
        
        // Add date filters if provided
        if (startDate != null && !startDate.isEmpty()) {
            sql.append("AND bd.service_date >= ?::date ");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append("AND bd.service_date <= ?::date ");
        }
        
        sql.append("GROUP BY s.servicename ORDER BY total DESC");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // Set parameters dynamically
            int paramIdx = 1;
            if (startDate != null && !startDate.isEmpty()) {
                pstmt.setString(paramIdx++, startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                pstmt.setString(paramIdx++, endDate);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String serviceName = rs.getString("servicename");
                    int total = rs.getInt("total");
                    analytics.put(serviceName, total);
                }
            }
        }
        
        return analytics;
    }
}
