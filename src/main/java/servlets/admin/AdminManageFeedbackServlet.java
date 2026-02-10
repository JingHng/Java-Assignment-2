// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin feedback management servlet following MVC architecture
// Uses FeedbackDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import com.silvercare.model.Feedback;
import silvercare.dao.FeedbackDAO;

@WebServlet("/AdminManageFeedback")
public class AdminManageFeedbackServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    /**
     * Handle GET requests to display all customer feedback with optional filtering
     * Supports filtering by minimum rating (e.g., show only 4+ star reviews)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Get optional minimum rating filter
            String minRating = request.getParameter("minRating");
            // Fetch feedback from database with optional filter
            List<Feedback> feedbackList = feedbackDAO.getAllFeedback(minRating);
            
            request.setAttribute("feedbackList", feedbackList);
            request.setAttribute("currentFilter", minRating);
            
            request.getRequestDispatcher("/ASSIGNMENT/admin/manage_feedback.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error retrieving feedback.");
        }
    }

    /**
     * Handle POST requests to reply to feedback or delete feedback entries
     * Allows admin to respond to customer reviews or remove inappropriate content
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        try {
            if ("reply".equals(action)) {
                // REPLY ACTION: Save admin's response to customer feedback
                int fId = Integer.parseInt(request.getParameter("feedbackId"));
                String replyText = request.getParameter("adminReply");
                
                if (feedbackDAO.updateAdminReply(fId, replyText)) {
                    response.sendRedirect("AdminManageFeedback?message=Reply saved successfully.");
                } else {
                    response.sendRedirect("AdminManageFeedback?error=Failed to save reply.");
                }

            } else if ("delete".equals(action)) {
                // DELETE ACTION: Remove feedback from database
                int fId = Integer.parseInt(request.getParameter("feedbackId"));
                if (feedbackDAO.deleteFeedback(fId)) {
                    response.sendRedirect("AdminManageFeedback?message=Feedback deleted successfully.");
                } else {
                    response.sendRedirect("AdminManageFeedback?error=Could not delete feedback.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("AdminManageFeedback?error=Operation failed.");
        }
    }
}