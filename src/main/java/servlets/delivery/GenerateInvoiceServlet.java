//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for Generating Invoice - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.delivery;

import java.io.IOException;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebServlet("/GenerateInvoice")
public class GenerateInvoiceServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // --- 1. Retrieve booking ID from request parameter ---
        String bookingId = request.getParameter("bookingId");
        
        // --- 2. Initialize JAX-RS client to call external delivery service ---
        // Path matches  Spring Boot @PostMapping("/invoice/{id}")
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8081/delivery-ws/api/delivery/invoice/" + bookingId);
        
        
        try {
            // --- 3. Send POST request to generate invoice ---
            Response wsResponse = target.request(MediaType.APPLICATION_JSON).post(Entity.json(""));
            
            // --- 4. Handle response ---
            if (wsResponse.getStatus() == Response.Status.CREATED.getStatusCode() || 
                wsResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                
                // Read the invoice data returned by Spring Boot
                Map<String, Object> invoiceData = wsResponse.readEntity(new GenericType<Map<String, Object>>() {});
                
                // Pass invoice data to the JSP
                request.setAttribute("invoice", invoiceData);
                request.getRequestDispatcher("/ASSIGNMENT/delivery/view_invoice.jsp").forward(request, response);
                
            } else {
                // If the REST API returns error status
                response.sendRedirect("ManageDelivery?error=InvoiceCreationFailed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ManageDelivery?error=ConnectionError");
        } finally {
            client.close();
        }
    }
}