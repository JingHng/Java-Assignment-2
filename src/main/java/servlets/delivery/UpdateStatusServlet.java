//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for Managing PUT - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.delivery;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.Response;

@WebServlet("/UpdateDeliveryStatus")
public class UpdateStatusServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // This handles the form submission - now calls REST API PUT endpoint
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	// --- 1. Retrieve parameters from request ---
    	String id = request.getParameter("id");
        String status = request.getParameter("status");
        String source = request.getParameter("source"); 
        
        // --- 2. Initialize JAX-RS client ---
        Client client = ClientBuilder.newClient();
        
        try {
            // Call PUT endpoint: /api/delivery/status/{id}?status={status}
            WebTarget target = client.target("http://localhost:8081/delivery-ws/api/delivery/status/" + id)
                    .queryParam("status", status);
            
            Response apiResponse = target.request().put(Entity.text(""));
            boolean success = (apiResponse.getStatus() == 200);
            apiResponse.close();
            
            // --- 5. Handle response based on source ---
            if (success) {
                // If called from dashboard via AJAX, send success response
                if ("dashboard".equals(source)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Status updated successfully");
                } else {
                    // Otherwise redirect to manage delivery page
                    response.sendRedirect("ManageDelivery?msg=StatusUpdated");
                }
            } else {
                if ("dashboard".equals(source)) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Update failed");
                } else {
                    response.sendRedirect("ManageDelivery?error=UpdateFailed");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if ("dashboard".equals(source)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error: " + e.getMessage());
            } else {
                response.sendRedirect("ManageDelivery?error=Exception:" + e.getMessage());
            }
        } finally {
            client.close();
        }
    }

    // This handles manual URL entry or links
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}