// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin user editing servlet following MVC architecture
// Uses UserDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import com.silvercare.model.User;
import silvercare.dao.UserDAO;

@WebServlet("/EditUser")
public class AdminEditUserServlet extends HttpServlet {
    
    /**
     * Handle GET requests to display user edit form
     * Loads user data from database for editing
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        try {
            UserDAO dao = new UserDAO();
            // Fetch user details from database
            User user = dao.getUserById(userId);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/ASSIGNMENT/admin/admin_edit_user.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("AdminReport?error=fetch_failed");
        }
    }

    /**
     * Handle POST requests to process user updates
     * Validates and saves updated user information to database
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Populate user object with form data
        User user = new User();
        user.setUserId(Integer.parseInt(request.getParameter("userId")));
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setPhone(request.getParameter("phone"));
        user.setAddress(request.getParameter("address"));
        user.setPostalCode(request.getParameter("postalCode"));
        user.setMedicalCondition(request.getParameter("medicalCondition"));
        user.setEmergencyName(request.getParameter("emergencyName"));
        user.setEmergencyPhone(request.getParameter("emergencyPhone"));

        try {
            UserDAO dao = new UserDAO();
            // Update user in database (null = don't change password)
            if (dao.updateUser(user, null)) {
                response.sendRedirect("AdminManageCustomers?success=updated");
            } else {
                response.sendRedirect("EditUser?id=" + user.getUserId() + "&error=update_failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("AdminManageCustomers?error=db_error");
        }
    }
}