// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for submitting feedback - Proper MVC Controller
// Handles displaying the feedback form and saving customer feedback to the database

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import silvercare.dao.FeedbackDAO;
import java.util.*;

@WebServlet("/SubmitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {
	
	// DAO for interacting with feedback table
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    
    // Show the Form
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
    	
    	HttpSession session = req.getSession(false);
        Integer customerId = (Integer) (session != null ? session.getAttribute("customerid") : null);       
        String bookingIdStr = req.getParameter("bookingId");
        
        // Redirect to MyFeedback if customer not logged in or no bookingId provided
        if (customerId == null || bookingIdStr == null) {
            resp.sendRedirect("MyFeedback");
            return;
        }

        try {
            int bookingId = Integer.parseInt(bookingIdStr);
            
            // Fetch booking details to ensure it's eligible for feedback
            List<Map<String, Object>> details = feedbackDAO.getEligibleBookingById(customerId, bookingId);
            
            // Booking not found or already reviewed → redirect with error
            if (details.isEmpty()) {
                resp.sendRedirect("MyFeedback?error=Booking not found or already reviewed");
                return;
            }

            // Set booking info as request attribute for the JSP
            req.setAttribute("booking", details.get(0));
            req.getRequestDispatcher("/ASSIGNMENT/customer/submit_feedback.jsp").forward(req, resp);
        } catch (Exception e) {
            resp.sendRedirect("MyFeedback");
        }
    }

    // Process the Form
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        
        // Ensure the customer is logged in
        int customerId = (int) session.getAttribute("customerid");
        
        // Retrieve form parameters
        int bookingId = Integer.parseInt(req.getParameter("bookingId"));
        int serviceId = Integer.parseInt(req.getParameter("serviceId"));
        int rating = Integer.parseInt(req.getParameter("rating"));
        String comments = req.getParameter("comments");

        try {
        	
            // Save feedback via DAO
            boolean success = feedbackDAO.saveFeedback(bookingId, customerId, serviceId, rating, comments);
            
            if (success) {
                resp.sendRedirect("MyFeedback?message=Thank you for your feedback!");
            } else {
                resp.sendRedirect("SubmitFeedback?bookingId=" + bookingId + "&error=Failed to save");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("MyFeedback?error=Error saving feedback");
        }
    }
}