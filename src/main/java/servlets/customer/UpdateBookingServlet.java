// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for updating a customer's booking - Proper MVC Controller
// Handles booking updates, validates input, updates database via BookingDAO, and forwards response to the appropriate view


package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import silvercare.dao.BookingDAO;

@WebServlet("/UpdateBooking")
public class UpdateBookingServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    	// Retrieve the current session and ensure the customer is logged in
    	HttpSession session = request.getSession();
        if (session.getAttribute("customerid") == null) {
            response.sendRedirect("login.jsp?message=Please log in.");
            return;
        }

        try {
            // Retrieve form parameters from the request
            String dIdStr = request.getParameter("detailID");
            String cgIdStr = request.getParameter("caregiverID");
            String date = request.getParameter("booking_date");
            String time = request.getParameter("start_time");
            String durStr = request.getParameter("duration");

            // Safety Check: Ensure no empty strings reach parseInt
            if (dIdStr == null || cgIdStr == null || time == null || dIdStr.isEmpty() || cgIdStr.isEmpty()) {
                throw new Exception("Missing required booking information.");
            }

            // Convert numeric values from String to proper types
            int dId = Integer.parseInt(dIdStr);
            int cgId = Integer.parseInt(cgIdStr);
            double duration = Double.parseDouble(durStr);

            
            // Call DAO to update booking details in the database
            BookingDAO dao = new BookingDAO();
            boolean success = dao.updateBookingDetails(dId, cgId, date, time, duration);

            if (success) {
            	response.sendRedirect("Dashboard?message=" + URLEncoder.encode("Booking updated successfully!", "UTF-8"));
            	} else {
            		response.sendRedirect("Dashboards?message=" + URLEncoder.encode("No changes were made to your booking.", "UTF-8"));
            		}

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode(e.getMessage(), "UTF-8");
            response.sendRedirect("EditBooking?detailID=" + request.getParameter("detailID") + "&message=" + errorMsg);
        }
    }
}