//Done by: Wan Jing Hng
//Date: 22/1/2026
//Description: Servlet configuration for Registration (Controller Logic)
//We use a servlet so that Java logic is not mixed with HTML (JSP)


package servlets;

import silvercare.dao.UserDAO;
import com.silvercare.model.User; // Import Value Bean
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    public RegisterServlet() {
        super();
    }

    // Handles GET requests (e.g. user opens registration page via URL)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	// Redirects user to the registration JSP page
        response.sendRedirect("ASSIGNMENT/register.jsp");
    }

    
    // Handles POST requests when registration form is submitted
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Create a User object to store form input data
        User userBean = new User();
        
        // Capture personal details from the registration form
        userBean.setFirstName(request.getParameter("firstName"));
        userBean.setLastName(request.getParameter("lastName"));
        userBean.setEmail(request.getParameter("email"));
        userBean.setPhone(request.getParameter("phone"));
        userBean.setAddress(request.getParameter("address"));
        userBean.setLanguage(request.getParameter("language"));
        userBean.setPassword(request.getParameter("password"));
        
        // Capture healthcare and emergency contact information
        userBean.setPostalCode(request.getParameter("postalCode"));
        userBean.setMedicalCondition(request.getParameter("medicalCondition"));
        userBean.setEmergencyName(request.getParameter("emergencyName"));
        userBean.setEmergencyPhone(request.getParameter("emergencyPhone"));

        try {
            // Create DAO instance to perform database operations
            UserDAO dao = new UserDAO();
            
            // Attempt to register the customer in the database
            if (dao.registerCustomer(userBean)) {
                String msg = "Registration Success! Please log in.";
                response.sendRedirect("ASSIGNMENT/login.jsp?message=" + URLEncoder.encode(msg, "UTF-8"));
            } else {
                response.sendRedirect("ASSIGNMENT/register.jsp?message=Error: Registration failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ASSIGNMENT/register.jsp?message=DB Error: " + URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}