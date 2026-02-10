//Done by: Wan Jing Hng
//Date: 22/1/2026
//Description: Servlet configuration for Customer viewing their cart (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets.customer;
import silvercare.dao.CartDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

@WebServlet("/RemoveCartItem")
public class RemoveCartItemServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");

        //Security Check
        if (customerId == null) {
            String errorMsg = URLEncoder.encode("Session expired. Please login again.", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp?message=" + errorMsg);
            return;
        }

        try {
            //Get Parameter
            String itemIDParam = request.getParameter("itemID");
            if (itemIDParam == null) throw new Exception("Missing Item ID.");
            int cartItemId = Integer.parseInt(itemIDParam);

            //Call DAO to get cart ID from database (like ViewCartServlet does)
            CartDAO dao = new CartDAO();
            int cartId = dao.getCartIdByCustomer(customerId);
            boolean success = dao.removeCartItem(cartItemId, cartId);

            //Redirect with message
            String msg = success ? "Item removed successfully." : "Item not found.";
            response.sendRedirect("ViewCart?message=" + URLEncoder.encode(msg, "UTF-8"));

        } catch (Exception e) {
            response.sendRedirect("ViewCart?message=" + URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
        }
    }
}