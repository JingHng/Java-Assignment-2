// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for caregiver actions (Check-in / Check-out)
// Updates live status of a booking and records actual check-in/check-out timestamps


package servlets.caregiver;

import com.silvercare.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/CaregiverAction")
public class CaregiverActionServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Retrieve the action parameter to determine check-in or check-out
        String action = request.getParameter("action"); 
        
        // Retrieve the booking ID to apply the action to
        String bookingId = request.getParameter("bookingId");
        String sql = "";
        String statusLabel = "";

        // Determine which SQL statement to use based on action
        if ("checkin".equals(action)) {
            sql = "UPDATE bookings SET live_status = 'In Progress', actual_check_in = CURRENT_TIMESTAMP WHERE bookingid = ?";
            statusLabel = "Checked In";
        } else if ("checkout".equals(action)) {
            sql = "UPDATE bookings SET live_status = 'Completed', actual_check_out = CURRENT_TIMESTAMP WHERE bookingid = ?";
            statusLabel = "Checked Out";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the booking ID in the prepared statement
            pstmt.setInt(1, Integer.parseInt(bookingId));
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                // Success message helps verify the action worked
                response.sendRedirect("CaregiverDashboard?message=Successfully " + statusLabel);
            } else {
                response.sendRedirect("CaregiverDashboard?error=Update failed - Booking not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("CaregiverDashboard?error=Database error");
        }
    }
}