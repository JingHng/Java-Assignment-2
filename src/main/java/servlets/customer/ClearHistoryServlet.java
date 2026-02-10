// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for clearing customer browsing history
// Deletes the history cookie for the logged-in customer

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/ClearHistory")
public class ClearHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Handle history clearing via POST request
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");
        
        // 1. Session Check
        if (customerId == null) {
            String errorMsg = URLEncoder.encode("You must be logged in to clear history.", "UTF-8");
            response.sendRedirect("view_history.jsp?error=" + errorMsg);
            return;
        }

        String userCookieName = "SilverCare_History" + customerId;
        String message;

        try {
            // 2. Cookie Deletion Logic
            Cookie tombstone = new Cookie(userCookieName, "");
            tombstone.setMaxAge(0); // Tell browser to delete immediately
            tombstone.setPath("/"); 
            response.addCookie(tombstone);

            message = URLEncoder.encode("Your browsing history has been cleared.", "UTF-8");
        } catch (Exception e) {
            message = URLEncoder.encode("Error: Could not clear history.", "UTF-8");
        }

        // 3. Redirect back to view
        response.sendRedirect("view_history.jsp?message=" + message);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Prevent accidental GET requests from clearing history
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method required.");
    }
}