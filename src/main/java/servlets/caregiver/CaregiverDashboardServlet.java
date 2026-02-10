// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for caregiver dashboard
// Displays all upcoming assigned bookings/visits for a caregiver


package servlets.caregiver;

import com.silvercare.util.DBConnection;
import com.silvercare.model.Booking; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CaregiverDashboard")
public class CaregiverDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
    	// Retrieve the session and check if caregiver is logged in
    	HttpSession session = request.getSession();
        Integer caregiverId = (Integer) session.getAttribute("caregiverId");

        if (caregiverId == null) {
            response.sendRedirect("../../login.jsp?message=Please login first");
            return;
        }
        
        // List to hold the upcoming visits assigned to this caregiver
        List<Booking> assignedVisits = new ArrayList<>();

        // SQL to fetch booking details including service name, customer info, and booking status
        String sql = "SELECT b.bookingid, b.status, b.live_status, bd.service_date, " +
                "s.servicename, c.firstname as customerName, c.phone as customerPhone " + 
                "FROM bookings b " +
                "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                "JOIN service s ON bd.serviceid = s.serviceid " +
                "JOIN customers c ON b.customerid = c.customerid " +
                "WHERE bd.caregiverid = ? " +
                "ORDER BY bd.service_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Set the caregiver ID parameter in the SQL
            pstmt.setInt(1, caregiverId);
            ResultSet rs = pstmt.executeQuery();

            // Loop through the results and map to Booking objects
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("bookingid"));
                
                String mainStatus = rs.getString("status");      
                String liveStatus = rs.getString("live_status"); 
                
                // If the booking is confirmed but live_status isn't set, show "Scheduled"
                if ("Confirmed".equalsIgnoreCase(mainStatus) && 
                   (liveStatus == null || liveStatus.isEmpty() || "Pending".equalsIgnoreCase(liveStatus))) {
                    b.setLiveStatus("Scheduled");
                } else {
                    b.setLiveStatus(liveStatus != null ? liveStatus : "Scheduled");
                }

                b.setBookingDate(rs.getDate("service_date")); 
                b.setServiceName(rs.getString("servicename"));
                b.setCustomerName(rs.getString("customerName"));
                b.setCustomerPhone(rs.getString("customerPhone"));
                assignedVisits.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.setAttribute("assignedVisits", assignedVisits);
        request.getRequestDispatcher("/ASSIGNMENT/caregiver/caregiver_dashboard.jsp").forward(request, response);
    }
}