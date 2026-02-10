//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for viewing cart - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.customer;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.silvercare.model.CartItem;
import silvercare.dao.CartDAO; 
import silvercare.dao.UserDAO; // Added this import

@WebServlet("/ViewCart")
public class ViewCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Retrieve the current session and check if the customer is logged in
        HttpSession session = request.getSession();
        
        
        Integer customerId = (Integer) session.getAttribute("customerid");
        
        if (customerId == null) {
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp");
            return;
        }

        // Initialize DAOs
        CartDAO cartDao = new CartDAO();
        UserDAO userDao = new UserDAO(); // Initialize UserDAO to get GST
        List<CartItem> cartItems = new ArrayList<>();
        
        // Initialize calculation variables
        double subtotal = 0;
        double gstRate = 0;
        double taxAmount = 0;
        double finalGrandTotal = 0;

        try {
            // Get the cart ID associated with this customer
            int cartId = cartDao.getCartIdByCustomer(customerId);
            
            // Fetch all items in the customer's cart
            if (cartId > 0) {
                cartItems = cartDao.getCartItems(cartId);
                
                // 1. Calculate the Subtotal
                for (CartItem item : cartItems) {
                    subtotal += (item.getPrice() * item.getDurationHours());
                }
                
                // 2. Fetch the dynamic GST Rate from system_settings
                gstRate = userDao.getGSTRate();
                
                // 3. Perform Tax Calculations
                taxAmount = subtotal * (gstRate / 100.0);
                finalGrandTotal = subtotal + taxAmount;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 4. Set attributes for the JSP (matching your variable names)
        request.setAttribute("cart", cartItems);
        request.setAttribute("subtotal", subtotal);
        request.setAttribute("gstRate", gstRate);
        request.setAttribute("taxAmount", taxAmount);
        request.setAttribute("totalAmount", finalGrandTotal); // Keep same name for simplicity
        
        request.getRequestDispatcher("/ASSIGNMENT/customer/view_cart.jsp").forward(request, response);
    }
}