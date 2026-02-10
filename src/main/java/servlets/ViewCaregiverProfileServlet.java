//Done by: Wan Jing Hng
//Date: 23/1/2026
//Description: Servlet configuration to allow customers view services that we offer (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.silvercare.model.Caregiver;
import silvercare.dao.UserDAO;

@WebServlet("/ViewCaregiverProfile")
public class ViewCaregiverProfileServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    try {
	        int cgId = Integer.parseInt(request.getParameter("id"));
	        UserDAO dao = new UserDAO();
	        
	        Caregiver caregiver = dao.getCaregiverById(cgId);
	        List<String> services = dao.getServicesByCaregiverId(cgId);
	        
	        request.setAttribute("cg", caregiver);
	        request.setAttribute("services", services); // Pass the list here
	        
	        request.getRequestDispatcher("/ASSIGNMENT/caregiver_profile.jsp").forward(request, response);
	    } catch (Exception e) {
	        response.sendRedirect("ViewCaregivers");
	    }
	}
}