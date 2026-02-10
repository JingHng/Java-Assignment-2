// Done by: Wan Jing Hng
// Date: 30/1/2026
// Description: Servlet for editing customer bookings
// Loads booking details, checks availability, and prevents editing of completed sessions

package servlets.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import silvercare.dao.BookingDAO;
import silvercare.dao.ServiceDAO;

@WebServlet("/EditBooking")
public class EditBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Load booking details for editing
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerid");
        
        // Switch to detailID to uniquely identify the specific service in the cart
        String dIdStr = request.getParameter("detailID");

        // 1. Security & Validation
        if (customerId == null || dIdStr == null || dIdStr.trim().isEmpty() || dIdStr.equals("null")) {
            response.sendRedirect("ManageBookings?message=Invalid%20Service%20ID");
            return;
        }

        try {
            int dId = Integer.parseInt(dIdStr);
            BookingDAO bDao = new BookingDAO();
            ServiceDAO sDao = new ServiceDAO();

            // 2. Fetch current item details using Detail ID
            Map<String, Object> currentBooking = bDao.getBookingByDetailId(dId, customerId);
            if (currentBooking == null) {
                response.sendRedirect("ManageBookings?message=Service not found");
                return;
            }
            
            // 2.1 Check if session is completed (for non-food services)
            Integer categoryId = (Integer) currentBooking.get("categoryId");
            String liveStatus = (String) currentBooking.get("liveStatus");
            
            // Debug logging
            System.out.println("Customer EditBooking - detailId: " + dId + ", categoryId: " + categoryId + ", liveStatus: " + liveStatus);
            
            if (categoryId != null && categoryId != 4 && liveStatus != null && liveStatus.equals("Completed")) {
                response.sendRedirect("ManageBookings?message=Cannot%20modify%20completed%20session");
                return;
            }

            // 3. Handle Availability parameters
            String selCgId = request.getParameter("caregiverID_select");
            String selDate = request.getParameter("booking_date_select");
            
            // Default to current values if not provided via "Check Availability" click
            if (selCgId == null || selCgId.trim().isEmpty()) {
                Object cgIdObj = currentBooking.get("caregiverId");
                selCgId = (cgIdObj != null && !cgIdObj.toString().equals("0")) ? cgIdObj.toString() : "-1";
            }

            if (selDate == null) {
                Object dateObj = currentBooking.get("serviceDate"); 
                selDate = (dateObj != null) ? dateObj.toString() : "";
            }

            // 4. Get list of busy periods for the selected caregiver
            List<Map<String, String>> bookedPeriods;
            if (!selDate.isEmpty() && !selCgId.equals("-1")) {
                // Pass dId here so the DAO knows to ignore THIS specific record when checking for conflicts
                bookedPeriods = bDao.getBookedPeriodsByDetail(Integer.parseInt(selCgId), selDate, dId);
            } else {
                bookedPeriods = new java.util.ArrayList<>();
            }
            
            int serviceId = (Integer) currentBooking.get("serviceId");
            List<Map<String, Object>> qualifiedCaregivers = sDao.getCaregiversByService(serviceId);

            // 5. Set attributes for JSP
            request.setAttribute("booking", currentBooking);
            request.setAttribute("bookedPeriods", bookedPeriods);
            request.setAttribute("qualifiedCaregivers", qualifiedCaregivers);
            
            // 6. Forward
            request.getRequestDispatcher("/ASSIGNMENT/customer/edit_bookings_profile.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ManageBookings?message=Error: " + java.net.URLEncoder.encode(e.getMessage(), "UTF-8"));
        }
    }
}