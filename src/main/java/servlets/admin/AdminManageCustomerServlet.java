// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin customer management servlet following MVC architecture
// Uses UserDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import com.silvercare.model.User;
import silvercare.dao.UserDAO;

@WebServlet("/AdminManageCustomers")
public class AdminManageCustomerServlet extends HttpServlet {

    /**
     * Handle GET requests to display customer list or delete a customer
     * Supports filtering by medical condition and postal code
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Determine the action to perform (default is list view)
        String action = request.getParameter("action");
        UserDAO dao = new UserDAO();

        try {
            // Handle delete action if requested
            if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                // Soft delete the user (sets status to inactive)
                dao.deleteUser(id);
                response.sendRedirect("AdminManageCustomers?message=User Deactivated");
                return;
            }

            // Default Action: Display customer list with optional filtering
            // Get filter parameters from request (can be null for no filtering)
            String cond = request.getParameter("conditionFilter");  // Medical condition filter
            String postal = request.getParameter("postalFilter");    // Postal code filter
            List<User> list = dao.searchUsers(cond, postal);
            
            // Pass customer list to JSP for rendering
            request.setAttribute("customerList", list);
            request.getRequestDispatcher("/ASSIGNMENT/admin/manage_customers.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("AdminDashboard?error=DBError");
        }
    }

    /**
     * Handle POST requests to update customer information
     * Processes edit form submissions and updates database
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Create User object and populate with form data
        User u = new User();
        u.setUserId(Integer.parseInt(request.getParameter("userId")));
        u.setFirstName(request.getParameter("firstName"));
        u.setLastName(request.getParameter("lastName"));
        u.setPhone(request.getParameter("phone"));
        u.setAddress(request.getParameter("address"));
        u.setPostalCode(request.getParameter("postalCode"));
        u.setMedicalCondition(request.getParameter("medicalCondition"));
        u.setEmergencyName(request.getParameter("emergencyName"));
        u.setEmergencyPhone(request.getParameter("emergencyPhone"));

        try {
            UserDAO dao = new UserDAO();
            // Update user in database (null = don't change password)
            if (dao.updateUser(u, null)) {
                response.sendRedirect("AdminManageCustomers?message=Update Successful");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}