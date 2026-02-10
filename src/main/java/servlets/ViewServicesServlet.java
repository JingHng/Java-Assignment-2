//Done by: Wan Jing Hng
//Date: 23/1/2026
//Description: Servlet configuration to allow customers view services that we offer (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets;

import silvercare.dao.ServiceDAO;
import com.silvercare.model.Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/ViewServices")
public class ViewServicesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles the GET request when a user clicks 'Services' in the navigation.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Initializing the DAO to fetch database data
        ServiceDAO dao = new ServiceDAO();
        
        try {
            // 1. Fetch the list of services from the database
            List<Service> services = dao.getAllServices();

            // 2. Put the list into the request attribute. 
            // The name "serviceList" must match the items="${serviceList}" in services.jsp
            request.setAttribute("serviceList", services);

            // 3. Forward the request to the JSP. 
            request.getRequestDispatcher("ASSIGNMENT/services.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Log the error to the console for debugging
            System.err.println("Error loading services in ViewServicesServlet:");
            e.printStackTrace();
            
            // Redirect to a safe page with an error message
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/index.jsp?error=catalog_load_failed");
        }
    }

    /**
     * Redirects POST requests to GET (useful if a search form uses POST).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}