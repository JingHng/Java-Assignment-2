//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for homepage (index.jsp) - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import com.silvercare.util.DBConnection;

@WebServlet("/Home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for the homepage
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Initialize DB resources
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        // Lists to hold data for the JSP
        List<Map<String, Object>> servicesList = new ArrayList<>();
        List<Map<String, Object>> testimonialsList = new ArrayList<>();
        
        try {
            // Get database connection
            conn = DBConnection.getConnection();
            
            // --- 1. Fetch Featured Services (up to 6 for homepage) ---
            String serviceSql = "SELECT serviceID, serviceName, description, price, imageLocation " +
                              "FROM service ORDER BY serviceID LIMIT 6";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(serviceSql);
            
            // Iterate over result set and store in a map
            while (rs.next()) {
                Map<String, Object> service = new HashMap<>();
                service.put("id", rs.getInt("serviceID"));
                service.put("name", rs.getString("serviceName"));
                service.put("description", rs.getString("description"));
                service.put("price", rs.getString("price"));
                service.put("image", rs.getString("imageLocation"));
                servicesList.add(service);
            }
            
            // Close previous result set and statement
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            
            // --- 2. Fetch Testimonials (if testimonials table exists) ---
            try {
                String testimonialSql = "SELECT customerName, rating, comment, created_at " +
                                      "FROM feedback ORDER BY created_at DESC LIMIT 5";
                stmt = conn.createStatement();
                rs = stmt.executeQuery(testimonialSql);
                
                while (rs.next()) {
                    Map<String, Object> testimonial = new HashMap<>();
                    testimonial.put("customerName", rs.getString("customerName"));
                    testimonial.put("rating", rs.getInt("rating"));
                    testimonial.put("comment", rs.getString("comment"));
                    testimonial.put("date", rs.getTimestamp("created_at"));
                    testimonialsList.add(testimonial);
                }
            } catch (SQLException e) {
                // Testimonials table might not exist, continue without them
                System.out.println("Note: Could not load testimonials (table may not exist)");
            }
            
            // --- 3. Set data as request attributes for JSP ---
            request.setAttribute("servicesList", servicesList);
            request.setAttribute("testimonialsList", testimonialsList);
            
            // --- 4. Forward to View (index.jsp) ---
            request.getRequestDispatcher("/ASSIGNMENT/index.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database error in HomeServlet: " + e.getMessage());
            
            // Set empty lists to prevent JSP errors
            request.setAttribute("servicesList", new ArrayList<>());
            request.setAttribute("testimonialsList", new ArrayList<>());
            request.getRequestDispatcher("/ASSIGNMENT/index.jsp").forward(request, response);
            
        } finally {
            // Clean up database resources
            if (rs != null) try { rs.close(); } catch (SQLException ignore) {}
            if (stmt != null) try { stmt.close(); } catch (SQLException ignore) {}
            DBConnection.closeConnection(conn);
        }
    }
    
    /**
     * Redirects POST requests to GET
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
