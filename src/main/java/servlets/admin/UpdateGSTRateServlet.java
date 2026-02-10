// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Update GST rate servlet following MVC architecture
// Uses UserDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import silvercare.dao.UserDAO;

@WebServlet("/UpdateGSTRate")
public class UpdateGSTRateServlet extends HttpServlet {

    /**
     * Handle POST requests to update the GST rate in the database
     * Validates and saves new GST percentage to system settings
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String rateStr = request.getParameter("gstRate");
        
        try {
            if (rateStr != null) {
                // Parse and validate new GST rate
                double newRate = Double.parseDouble(rateStr);
                
                // 1. Update the database via DAO
                UserDAO dao = new UserDAO();
                boolean success = dao.updateGSTRate(newRate);
                
                if (success) {
                    // 2. Redirect back to dashboard with a success message
                    response.sendRedirect(request.getContextPath() + "/AdminDashboard?success=gst_updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/AdminManageGST?error=update_failed");
                }
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/AdminManageGST?error=invalid_input");
        }
    }
}