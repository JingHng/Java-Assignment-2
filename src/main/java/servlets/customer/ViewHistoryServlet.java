//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for viewing history - Proper MVC Controller
// Fetches services and testimonials data from database and forwards to View


package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import silvercare.dao.BookingDAO;
import com.silvercare.model.Service;

@WebServlet("/ViewHistory")
public class ViewHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // ---  Verify session and customer login ---
    	// false = don't create a new session
        HttpSession session = request.getSession(false);
        
        
        if (session == null || session.getAttribute("customerid") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // ---  Retrieve customer ID from session ---
        int customerId = (Integer) session.getAttribute("customerid");

        
        try {
        	
            // --- 3. Interact with DAO (Model) to fetch booking history ---
            silvercare.dao.BookingDAO bookingDAO = new silvercare.dao.BookingDAO();
            
            // Returns a List of Maps with each booking's details
            List<Map<String, Object>> history = bookingDAO.getPastBookings(customerId);
            
            // --- 4. Set history as request attribute for the View ---
            request.setAttribute("historyBookings", history);
            
            // --- 5. Forward to JSP for rendering (View) ---
            request.getRequestDispatcher("/ASSIGNMENT/customer/view_history.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
