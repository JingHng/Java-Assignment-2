// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for adding items (services/meals) to customer cart
// Handles both caregiver-based services and meal deliveries with proper validation

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import silvercare.dao.CartDAO;

@WebServlet("/AddToCartDB")
public class AddToCartDBServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String CART_SESSION_KEY = "cartId";

    // Handle adding items to cart via POST request
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        
        // Security check: Ensure customer is logged in
        if (session.getAttribute("customerid") == null) {
            String errorMsg = URLEncoder.encode("You must log in to add items to your cart.", "UTF-8");
            response.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=" + errorMsg);
            return;
        }
        
        int currentCustomerId = (Integer) session.getAttribute("customerid");
        Integer cartId = (Integer) session.getAttribute(CART_SESSION_KEY);
        
        try {
            // Extract form parameters
            int serviceId = Integer.parseInt(request.getParameter("serviceID"));
            
            // --- UPDATED CAREGIVER HANDLING ---
            String caregiverParam = request.getParameter("caregiverID");
            Integer caregiverId = null; // Default to null for Meal Deliveries
            
            if (caregiverParam != null && !caregiverParam.trim().isEmpty() && !caregiverParam.equals("0")) {
                caregiverId = Integer.parseInt(caregiverParam);
            }
            // ----------------------------------
            
            String serviceDateStr = request.getParameter("booking_date");
            String startTimeStr = request.getParameter("start_time");
            
            // Use 1.0 as default duration if field is empty (common for meals)
            String durStr = request.getParameter("duration_hours");
            double durationHours = (durStr != null && !durStr.isEmpty()) ? Double.parseDouble(durStr) : 1.0;
            
            String specialRequests = request.getParameter("specialRequests");
            double pricePerUnit = Double.parseDouble(request.getParameter("price"));
            
            String selectedMeal = request.getParameter("selectedFoodItem"); 
            String dietaryNotes = request.getParameter("dietaryNotes");
            
            double subtotal = pricePerUnit * durationHours;
            
            CartDAO cartDAO = new CartDAO();
            
            // Get or create cart for the customer
            if (cartId == null || cartId <= 0) {
                cartId = cartDAO.getOrCreateCart(currentCustomerId);
                session.setAttribute(CART_SESSION_KEY, cartId);
            }
            
            // Add item to cart - caregiverId can be null for meal deliveries
            boolean success = cartDAO.addItemToCart(
                cartId, 
                serviceId, 
                caregiverId, 
                serviceDateStr, 
                startTimeStr,
                durationHours, 
                specialRequests, 
                pricePerUnit, 
                subtotal,
                selectedMeal,
                dietaryNotes 
            );
            
            if (success) {
                String successMsg = URLEncoder.encode("Item successfully added to your cart!", "UTF-8");
                response.sendRedirect(contextPath + "/ViewCart?message=" + successMsg);
            } else {
                throw new Exception("Could not add item. If this is a service, the caregiver may be unavailable.");
            }
            
        } catch (NumberFormatException e) {
            String errorMsg = URLEncoder.encode("Invalid input format. Please check your selection.", "UTF-8");
            response.sendRedirect(contextPath + "/ViewServices?error=" + errorMsg);
            
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Error adding to cart: " + e.getMessage(), "UTF-8");
            response.sendRedirect(contextPath + "/ViewServices?error=" + errorMsg);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/ViewServices");
    }
}