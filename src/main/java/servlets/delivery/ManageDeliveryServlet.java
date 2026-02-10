//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for Managing GET - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.delivery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.silvercare.model.DeliveryOrder;

@WebServlet("/ManageDelivery")
public class ManageDeliveryServlet extends HttpServlet {
	
    // Base URL for Delivery REST API
    private static final String REST_URL = "http://localhost:8081/delivery-ws/api/delivery";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // --- 1. Retrieve search parameter if present ---
        // Used to fetch a specific delivery order by ID
        String searchId = request.getParameter("searchId");
        
        // --- 2. Initialize JAX-RS client to call REST API ---
        Client client = ClientBuilder.newClient();
        // Initialize list to store orders
        List<DeliveryOrder> orderList = new ArrayList<>();

        
        try {
            if (searchId != null && !searchId.trim().isEmpty()) {
                // CONSUME: GET /orders/{id}
                WebTarget target = client.target(REST_URL + "/orders/" + searchId);
                Response resp = target.request(MediaType.APPLICATION_JSON).get();
                
                // Check for 200 OK
                if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
                	//Convert into deliveryOrder object to deserializes into Java Object
                    DeliveryOrder order = resp.readEntity(DeliveryOrder.class);
                    if (order != null) orderList.add(order);
                }
            } else {
                // CONSUME: GET /orders (Get All)
                WebTarget target = client.target(REST_URL + "/orders");
                Response resp = target.request(MediaType.APPLICATION_JSON).get();
                
                if (resp.getStatus() == Response.Status.OK.getStatusCode()) {
                    orderList = resp.readEntity(new GenericType<List<DeliveryOrder>>() {});
                }
            }
        } catch (Exception e) {
            request.setAttribute("err", "API Connection Failed: " + e.getMessage());
        } finally {
            client.close();
        }

        request.setAttribute("deliveryOrders", orderList);         
        request.getRequestDispatcher("/ASSIGNMENT/delivery/index_delivery.jsp").forward(request, response);
    }
}