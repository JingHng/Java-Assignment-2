//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for Canceling Orders - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.delivery;

import java.io.IOException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;

@WebServlet("/CancelOrder")
public class CancelOrderServlet extends HttpServlet {
    // Changed from doGet to doPost
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
        // --- 1. Retrieve the delivery order ID from request parameter ---
    	String id = request.getParameter("id");
    	
        // --- 2. Initialize JAX-RS client to communicate with external delivery service ---
        Client client = ClientBuilder.newClient();
        
        // Construct the target URL for cancelling order
        WebTarget target = client.target("http://localhost:8081/delivery-ws/api/delivery/cancel/" + id);
        
        // --- 3. Send DELETE request to external service ---
        target.request().delete();
        
        // --- 4. Close client to release resources ---
        client.close();
        
        // --- 5. Redirect back to delivery management page with success message ---
        response.sendRedirect("ManageDelivery?msg=Deleted");
    }
}