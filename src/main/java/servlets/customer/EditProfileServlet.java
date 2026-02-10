// Done by: Wan Jing Hng
// Date: 1/2/2026
// Description: Fixed NPE on session access and updated for healthcare fields

package servlets.customer;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.silvercare.model.User;
import silvercare.dao.UserDAO;

@WebServlet("/EditProfile")
public class EditProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // FIX: Check if session exists AT ALL before getting attributes
        if (session == null || session.getAttribute("customerid") == null) {
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp?message=Please login to access profile");
            return;
        }

        Integer customerId = (Integer) session.getAttribute("customerid"); 

        try {
            UserDAO dao = new UserDAO();
            User user = dao.getUserById(customerId);  

            if (user != null) {
                request.setAttribute("userProfile", user); 
                request.getRequestDispatcher("/ASSIGNMENT/customer/edit_profile.jsp").forward(request, response);
            } else {
                response.sendRedirect("dashboard.jsp?error=profile_not_found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?error=load_failed");
        }
    }
}