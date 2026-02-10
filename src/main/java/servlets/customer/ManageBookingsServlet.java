// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for managing customer bookings
// Retrieves all bookings for the logged-in customer and forwards to the view



package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import silvercare.dao.BookingDAO;

//@WebServlet("/ManageBookings")
public class ManageBookingsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");

        if (customerId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            BookingDAO dao = new BookingDAO();
            // Using the specialized Manage method to get correct detailIDs
            List<Map<String, Object>> bookings = dao.getManageBookings(customerId);
            
            request.setAttribute("bookings", bookings); 
            request.getRequestDispatcher("/ASSIGNMENT/customer/manage_bookings_profile.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}