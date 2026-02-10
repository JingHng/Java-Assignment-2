// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin customer report servlet following MVC architecture
// Uses UserDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.silvercare.model.User;
import silvercare.dao.UserDAO;

@WebServlet("/AdminReport")
public class AdminReportServlet extends HttpServlet {
    /**
     * Handle GET requests to generate customer reports with filtering
     * Supports filtering by medical condition and postal code
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Extract filter parameters (can be null for no filtering)
        String conditionFilter = request.getParameter("conditionFilter");
        String postalFilter = request.getParameter("postalFilter");
        
        try {
            UserDAO dao = new UserDAO();
            // Search users based on filter criteria
            List<User> userList = dao.searchUsers(conditionFilter, postalFilter);
            
            request.setAttribute("userList", userList);
            request.getRequestDispatcher("/ASSIGNMENT/admin/admin_report.jsp").forward(request, response);            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Dashboard?error=ReportError");
        }
    }
}