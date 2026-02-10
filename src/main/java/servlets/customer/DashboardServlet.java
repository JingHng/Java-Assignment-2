// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for customer dashboard
// Displays upcoming bookings and supports AJAX polling for live status updates

package servlets.customer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import silvercare.dao.BookingDAO;

@WebServlet("/Dashboard")
public class DashboardServlet extends HttpServlet {
    
    // Handle dashboard requests - both regular page loads and AJAX status checks
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");

        if (customerId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            BookingDAO bookingDAO = new BookingDAO();
            // Using the specialized Dashboard method
            List<Map<String, Object>> upcoming = bookingDAO.getDashboardBookings(customerId);
            
            // Check if this is an AJAX request for status updates
            String isAjax = request.getParameter("ajax");
            if ("true".equals(isAjax)) {
                // Return JSON for AJAX polling
                response.setContentType("application/json");
                StringBuilder json = new StringBuilder("[");
               
                // Build JSON array for the upcoming bookings
                for (int i = 0; i < upcoming.size(); i++) {
                    Map<String, Object> b = upcoming.get(i);
                    json.append("{");
                    json.append("\"bookingId\":").append(b.get("bookingId")).append(","); 
                    json.append("\"liveStatus\":\"").append(b.get("liveStatus") != null ? b.get("liveStatus") : "Scheduled").append("\",");
                    json.append("\"deliveryStatus\":\"").append(b.get("deliveryStatus") != null ? b.get("deliveryStatus") : "").append("\"");
                    json.append("}");
                    if (i < upcoming.size() - 1) json.append(",");
                }
                json.append("]");
                response.getWriter().write(json.toString());
                return; 
            }

            request.setAttribute("bookingsList", upcoming);         
            request.getRequestDispatcher("/ASSIGNMENT/customer/dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500);
        }
    }
}