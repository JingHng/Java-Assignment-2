// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for confirming meal delivery receipt
// Updates delivery status to 'Received by Customer' when customer confirms receipt

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ConfirmReceipt")
public class ConfirmReceiptServlet extends HttpServlet {
    
    // Handle receipt confirmation via POST
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        silvercare.dao.BookingDAO dao = new silvercare.dao.BookingDAO();
        
        try {
            // Update delivery status in database
            boolean success = dao.updateDeliveryStatus(bookingId, "Received by Customer");
            
            if (success) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("text/plain");
                response.getWriter().write("Receipt confirmed successfully");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("text/plain");
                response.getWriter().write("Failed to update status");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/plain");
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}