//Done by: Wan Jing Hng
//Date: 22/1/2026
//Description: Servlet configuration for Customer and Admin Login (Controller Logic)
//We use a servlet so that Java logic is not mixed with HTML (JSP)


package servlets;

import silvercare.dao.UserDAO;
import com.silvercare.model.User; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	
    // Ensures servlet compatibility during deployment
    private static final long serialVersionUID = 1L;

    // Handles login form submission
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
    	
        String contextPath = request.getContextPath();
        String redirectPage = contextPath + "/ASSIGNMENT/login.jsp"; 
        
        // Retrieve login credentials from form input
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password"); 
        String userRole = request.getParameter("userRole"); 
        
        // Validate that all required fields are filled
        if (loginId == null || loginId.isEmpty() || password == null || password.isEmpty() || userRole == null) {
            String errorMessage = "Login failed: All fields are required.";
            response.sendRedirect(redirectPage + "?message=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        try {
        	
            // Create DAO instance to authenticate user
            UserDAO dao = new UserDAO();
            
            // Verify login credentials against the database
            User user = dao.authenticate(loginId, password, userRole);

            
            if (user != null) {
                // Clear any old session leftovers
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate(); 
                }
                
                // Establish a fresh secure session
                session = request.getSession(true);
                session.setMaxInactiveInterval(3600); // 1 hour
                
                // Set Universal Attributes
                session.setAttribute("user", user);
                session.setAttribute("isLoggedIn", true);
                session.setAttribute("userRole", userRole);
                session.setAttribute("firstName", user.getFirstName());
                session.setAttribute("userEmail", user.getEmail());

                // Retrieve or initialize active session tracking map
                ServletContext application = getServletContext();
                Map<String, Map<String, Object>> activeSessions = 
                    (Map<String, Map<String, Object>>) application.getAttribute("activeSessions");
                
                if (activeSessions == null) {
                    activeSessions = new ConcurrentHashMap<>();
                    application.setAttribute("activeSessions", activeSessions);
                }
                
                // Store basic session metadata for monitoring
                Map<String, Object> sessionInfo = new HashMap<>();
                sessionInfo.put("sessionId", session.getId());
                sessionInfo.put("username", user.getFirstName());
                sessionInfo.put("loginTime", new java.util.Date());
                activeSessions.put(session.getId(), sessionInfo);

                // --- ROLE-BASED REDIRECT LOGIC ---
                if ("admin".equalsIgnoreCase(userRole)) {
                    session.setAttribute("adminUserId", loginId); 
                    response.sendRedirect(contextPath + "/AdminDashboard"); 

                } else if ("caregiver".equalsIgnoreCase(userRole)) {
                    // Set caregiver specific ID for visit tracking
                    session.setAttribute("caregiverId", user.getUserId());
                    String welcome = "Welcome, Caregiver " + user.getFirstName() + "!";
                    response.sendRedirect(request.getContextPath() + "/CaregiverDashboard");
                } else {
                    // Customer Role
                    session.setAttribute("customerid", user.getUserId()); 
                    String welcome = "Welcome back, " + user.getFirstName() + "!";
                    response.sendRedirect(contextPath + "/Dashboard?message=" + URLEncoder.encode(welcome, "UTF-8")); 
                }
                return;
                
            } else {
                String errorMessage = "Invalid " + userRole + " credentials.";
                response.sendRedirect(redirectPage + "?message=" + URLEncoder.encode(errorMessage, "UTF-8"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(redirectPage + "?message=" + URLEncoder.encode("DB Error", "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(redirectPage + "?message=" + URLEncoder.encode("System Error", "UTF-8"));
        }
    }

    // Redirects GET requests to the login page
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
        response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp");
    }
}