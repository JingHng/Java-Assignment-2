// Done by: Wan Jing Hng
// Date: 1 Feb 2026
// Description: Updated Servlet to capture healthcare, postal code, and emergency contact data.

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import com.silvercare.model.User;
import silvercare.dao.UserDAO;

@WebServlet("/ProcessEditProfile")
public class ProcessEditProfileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Safety check for session
        if (session == null || session.getAttribute("customerid") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Integer customerId = (Integer) session.getAttribute("customerid");

        try {
            // 1. Get ALL parameters from the form (Standard + New)
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String newPassword = request.getParameter("newpassword");
            
            // NEW FIELDS FOR ASSIGNMENT 2
            String postalCode = request.getParameter("postalCode");
            String medicalCondition = request.getParameter("medicalCondition"); // The joined string from JS
            String emergencyName = request.getParameter("emergencyName");
            String emergencyPhone = request.getParameter("emergencyPhone");

            // 2. Populate the User object completely
            User user = new User();
            user.setUserId(customerId);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setAddress(address);
            
            // Map new fields to the object
            user.setPostalCode(postalCode);
            user.setMedicalCondition(medicalCondition);
            user.setEmergencyName(emergencyName);
            user.setEmergencyPhone(emergencyPhone);

            // 3. Call DAO to update
            UserDAO dao = new UserDAO();
            boolean success = dao.updateUser(user, newPassword);

            if (success) {
                // Update session if name changed
                session.setAttribute("firstname", firstName);
                String successMsg = URLEncoder.encode("Profile updated successfully!", "UTF-8");
                response.sendRedirect("EditProfile?message=" + successMsg);
            } else {
                String failMsg = URLEncoder.encode("Update failed. Please try again.", "UTF-8");
                response.sendRedirect("EditProfile?message=" + failMsg);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Encode the error message to handle spaces/special chars in URL
            String errorMsg = URLEncoder.encode("Error: " + e.getMessage(), "UTF-8");
            response.sendRedirect("EditProfile?message=" + errorMsg);
        }
    }
}