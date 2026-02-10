//Done by: Wan Jing Hng
//Date: 23/1/2026
//Description: Servlet configuration for customer choosing a specific time when booking services  (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import silvercare.dao.ServiceDAO;

@WebServlet("/CheckAvailability")
public class CheckAvailabilityServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));
            String date = request.getParameter("date");

            ServiceDAO dao = new ServiceDAO();
            List<String> bookedSlots = dao.getBookedSlots(serviceId, date);
            
            // Join list into "08:00,13:00,14:00"
            String output = String.join(",", bookedSlots);
            
            response.setContentType("text/plain");
            response.getWriter().write(output);
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}