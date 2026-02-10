// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin GST management display servlet following MVC architecture
// Uses UserDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import silvercare.dao.UserDAO;

@WebServlet("/AdminManageGST")
public class AdminManageGSTServlet extends HttpServlet {
    
    /**
     * Handle GET requests to display the GST management page
     * Loads current GST rate from database for display
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Fetch current GST rate from database via DAO
            UserDAO dao = new UserDAO();
            double currentGST = dao.getGSTRate();
            
            // 2. Set as attribute so the JSP can display it in the input box
            request.setAttribute("currentGST", currentGST);
            
            // 3. Forward to the JSP page
            request.getRequestDispatcher("/ASSIGNMENT/admin/manage_gst.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Redirect back with an error if the DB query fails
            response.sendRedirect("AdminDashboard?error=db_error");
        }
    }
}