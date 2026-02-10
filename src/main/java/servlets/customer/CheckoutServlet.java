// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for handling checkout process and Stripe payment integration
// Calculates totals with GST, creates Stripe checkout session, and redirects to payment

package servlets.customer;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.silvercare.model.CartItem;
import silvercare.dao.CartDAO;
import silvercare.dao.BookingDAO;
import silvercare.dao.UserDAO; // Added for GST retrieval

@WebServlet("/Checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Display checkout page with cart items and totals
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");
        
        if (customerId == null) {
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp");
            return;
        }

        try {
            CartDAO cartDao = new CartDAO();
            UserDAO userDao = new UserDAO();
            
            int cartId = cartDao.getCartIdByCustomer(customerId);
            List<CartItem> items = cartDao.getCartItems(cartId);

            if (items == null || items.isEmpty()) {
                response.sendRedirect("ViewCart?message=" + 
                    URLEncoder.encode("Your cart is empty. Please add services.", "UTF-8"));
                return;
            }

            // --- GST & TOTAL CALCULATIONS ---
            double subtotal = 0;
            for (CartItem item : items) {
                subtotal += (item.getPrice() * item.getDurationHours());
            }
            
            double gstRate = userDao.getGSTRate();
            double taxAmount = subtotal * (gstRate / 100.0);
            double totalAmount = subtotal + taxAmount;

            // Set attributes for the JSP
            request.setAttribute("checkoutItems", items);
            request.setAttribute("subtotal", subtotal);
            request.setAttribute("gstRate", gstRate);
            request.setAttribute("taxAmount", taxAmount);
            request.setAttribute("totalAmount", totalAmount);

            request.getRequestDispatcher("/ASSIGNMENT/customer/checkout.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("ViewCart?message=Error+retrieving+checkout+details");
        }
    }
    
    // Process payment by creating Stripe checkout session
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer customerID = (Integer) session.getAttribute("customerid");
        
        if (customerID == null) {
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp");
            return;
        }

        try {
            CartDAO cartDao = new CartDAO();
            UserDAO userDao = new UserDAO();
            
            int cartId = cartDao.getCartIdByCustomer(customerID);
            List<CartItem> cartItems = cartDao.getCartItems(cartId);

            if (cartItems == null || cartItems.isEmpty()) {
                response.sendRedirect("ViewCart?message=Cart+Empty");
                return;
            }

            // --- 1. Calculate Total for Stripe ---
            double subtotal = 0;
            for (CartItem item : cartItems) {
                subtotal += (item.getPrice() * item.getDurationHours());
            }
            double gstRate = userDao.getGSTRate();
            double totalAmount = subtotal + (subtotal * (gstRate / 100.0));
            
            // Stripe expects amounts in CENTS (Long)
            long amountInCents = Math.round(totalAmount * 100);

            // --- 2. Initialize Stripe ---
            com.stripe.Stripe.apiKey = "sk_test_51Swbk7AHESYbnKUym327Rn6yx8wG1SzG68BAU3sfVrCoWGHujnuRJsbl3Z4g9dOXusbYqj1T1C8Eco7RYQzsyMP600GVF2uYsR"; // Get this from Stripe Dashboard

            com.stripe.param.checkout.SessionCreateParams params =
                com.stripe.param.checkout.SessionCreateParams.builder()
                    .addPaymentMethodType(com.stripe.param.checkout.SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(com.stripe.param.checkout.SessionCreateParams.Mode.PAYMENT)
                    // Success URL passes the Session ID so we can verify the payment later
                    .setSuccessUrl("http://localhost:8080" + request.getContextPath() + "/PaymentSuccess?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl("http://localhost:8080" + request.getContextPath() + "/ViewCart")
                    .addLineItem(
                        com.stripe.param.checkout.SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("sgd") // Or your preferred currency
                                    .setUnitAmount(amountInCents)
                                    .setProductData(
                                        com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("SilverCare Solutions - Service Booking")
                                            .build())
                                    .build())
                            .build())
                    .build();

            com.stripe.model.checkout.Session stripeSession = com.stripe.model.checkout.Session.create(params);

            // --- 3. Redirect to Stripe ---
            response.sendRedirect(stripeSession.getUrl());

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ViewCart?message=Error+Initializing+Payment");
        }
    }
}