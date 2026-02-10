//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for displaying all reviews - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import silvercare.dao.FeedbackDAO;
import com.silvercare.model.Feedback;

@WebServlet("/AllReviews")
public class AllReviewsServlet extends HttpServlet {
	
    // DAO instance for interacting with feedback table in the database
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Passing null to get all reviews regardless of rating
            List<Feedback> allReviews = feedbackDAO.getAllFeedback(null);
            
            // ---  Store reviews as request attribute for JSP ---
            req.setAttribute("allReviews", allReviews);
            
            // ---  Forward request to JSP for rendering ---
            req.getRequestDispatcher("/ASSIGNMENT/all_reviews.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500);
        }
    }
}