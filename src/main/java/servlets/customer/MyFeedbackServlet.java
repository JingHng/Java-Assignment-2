// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for displaying the customer's submitted feedback history
// Retrieves all feedback submitted by the logged-in customer and forwards to the view


package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import silvercare.dao.FeedbackDAO;

@WebServlet("/MyFeedback")
public class MyFeedbackServlet extends HttpServlet {
	
	
    private FeedbackDAO feedbackDAO = new FeedbackDAO();
    
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
    	
    	
        // Get the current session and customer ID
        HttpSession session = req.getSession(false);
        Integer customerId = (Integer) (session != null ? session.getAttribute("customerid") : null);
        
        // Redirect to login page if customer is not logged in
        if (customerId == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?message=Please login");
            return;
        }

        try {
            // Fetch all feedback submitted by this customer
            List<com.silvercare.model.Feedback> submitted = feedbackDAO.getSubmittedFeedback(customerId);
            
            // Set the feedback list as a request attribute for the JSP
            req.setAttribute("submittedFeedback", submitted);
            req.getRequestDispatcher("/ASSIGNMENT/customer/feedbackPage.jsp").forward(req, resp);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Failed to load feedback history.");
            req.getRequestDispatcher("/ASSIGNMENT/customer/feedbackPage.jsp").forward(req, resp);
        }
    }
}