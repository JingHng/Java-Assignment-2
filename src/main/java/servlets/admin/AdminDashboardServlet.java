// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin dashboard servlet following MVC architecture
// Uses AdminDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import silvercare.dao.AdminDAO;
import java.io.IOException;
import java.util.*;

@WebServlet("/AdminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    private AdminDAO adminDAO = new AdminDAO();

    /**
     * Handle GET requests to display admin dashboard with statistics and analytics
     * Shows overview of system metrics and performance data
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Retrieve existing session (false = don't create new one)
        HttpSession session = request.getSession(false);
        
        // Security check: Verify admin role before showing dashboard
        if (session == null || !"admin".equalsIgnoreCase((String)session.getAttribute("userRole"))) {
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp");
            return;
        }

        try {
            // 1. Get dashboard statistics from DAO (service, caregiver, customer counts)
            Map<String, Integer> stats = adminDAO.getDashboardStats();

            // 2. Get analytics data from DAO for reporting
            // Popularity analytics: Top 3 most booked services
            List<Map<String, Object>> popTop = adminDAO.getTopServicesByPopularity(3);
            // Popularity analytics: Bottom 3 least booked services
            List<Map<String, Object>> popLow = adminDAO.getBottomServicesByPopularity(3);
            
            // Client analytics: Top 3 clients by total services booked
            List<Map<String, Object>> clientTop = adminDAO.getTopClients(3);
            // Client analytics: Bottom 3 clients by total services booked
            List<Map<String, Object>> clientLow = adminDAO.getBottomClients(3);
            
            // Revenue analytics: Top 3 services by revenue generation
            List<Map<String, Object>> revTop = adminDAO.getTopServicesByRevenue(3);
            // Revenue analytics: Bottom 3 services by revenue generation
            List<Map<String, Object>> revLow = adminDAO.getBottomServicesByRevenue(3);
            
            // 3. Set all attributes for JSP to display
            request.setAttribute("stats", stats);
            request.setAttribute("popTop", popTop);
            request.setAttribute("popLow", popLow);
            request.setAttribute("rateTop", clientTop);
            request.setAttribute("rateLow", clientLow);
            request.setAttribute("revTop", revTop);
            request.setAttribute("revLow", revLow);
            request.setAttribute("firstName", session.getAttribute("firstName"));

        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        request.getRequestDispatcher("/ASSIGNMENT/admin/admin_dashboard.jsp").forward(request, response);
    }
}