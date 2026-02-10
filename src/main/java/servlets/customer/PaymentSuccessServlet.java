// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for handling successful Stripe payments
// Verifies payment, moves cart items to bookings, and clears cart


package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.silvercare.model.CartItem;
import silvercare.dao.CartDAO;
import silvercare.dao.BookingDAO;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;

@WebServlet("/PaymentSuccess")
public class PaymentSuccessServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    	
        // Retrieve Stripe session ID and customer ID from session
        String sessionId = request.getParameter("session_id");
        HttpSession session = request.getSession();
        Integer customerID = (Integer) session.getAttribute("customerid");
        String contextPath = request.getContextPath();

        try {
            // Initialize Stripe API
            Stripe.apiKey = "sk_test_51Swbk7AHESYbnKUym327Rn6yx8wG1SzG68BAU3sfVrCoWGHujnuRJsbl3Z4g9dOXusbYqj1T1C8Eco7RYQzsyMP600GVF2uYsR";
           
            // Retrieve Stripe checkout session to confirm payment
            Session stripeSession = Session.retrieve(sessionId);

            if ("paid".equals(stripeSession.getPaymentStatus())) {
                //Payment confirmed! Now move data to Bookings table
                CartDAO cartDao = new CartDAO();
                BookingDAO bookingDao = new BookingDAO();

                int cartId = cartDao.getCartIdByCustomer(customerID);
                List<CartItem> cartItems = cartDao.getCartItems(cartId);

                // Save cart items as bookings in database
                boolean success = bookingDao.processCheckout(customerID, cartItems);
                
                
                // Redirect to dashboard with success message
                if (success) {
                    cartDao.clearCart(cartId);
                    response.sendRedirect(contextPath + "/Dashboard?message=" +
                        URLEncoder.encode("Payment successful & Booking confirmed!", StandardCharsets.UTF_8));
                } else {
                    response.sendRedirect(contextPath + "/ViewCart?message=Error+Saving+Booking");
                }
            } else {
                response.sendRedirect(contextPath + "/ViewCart?message=Payment+Not+Verified");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/ViewCart?message=System+Error");
        }
    }
}