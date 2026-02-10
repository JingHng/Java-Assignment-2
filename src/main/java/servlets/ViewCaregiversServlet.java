//Done by: Wan Jing Hng
//Date: 23/1/2026
//Description: Servlet configuration to allow customers view services that we offer (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import silvercare.dao.UserDAO; 
import com.silvercare.model.Caregiver;

@WebServlet("/ViewCaregivers")
public class ViewCaregiversServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            UserDAO dao = new UserDAO();
            // Ensure this method in UserDAO returns the List<Caregiver> with the new 'image' paths
            List<Caregiver> caregivers = dao.getAllCaregivers();
            
            request.setAttribute("caregiverList", caregivers);
            request.getRequestDispatcher("/ASSIGNMENT/caregivers.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp?error=loading_caregivers");
        }
    }
}