// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin bookings management servlet following MVC architecture
// Uses BookingDAO and ServiceDAO for all database operations

package servlets.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import silvercare.dao.BookingDAO;
import silvercare.dao.ServiceDAO;
import com.silvercare.model.Caregiver;

@WebServlet("/AdminManageBookings")
public class AdminManageBookingsServlet extends HttpServlet {
    private BookingDAO bookingDAO = new BookingDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();

    /**
     * Handle GET requests to display bookings list or view booking details
     * Supports filtering by status, date range, service, and customer name
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Determine action: 'view' for details, default is 'list'
        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            if ("view".equals(action)) {
                // VIEW ACTION: Show detailed information for a specific booking
                String idStr = request.getParameter("id");
                // Validate that ID parameter exists
                if (idStr == null || idStr.isEmpty()) {
                    response.sendRedirect("AdminManageBookings?error=Invalid ID");
                    return;
                }

                int id = Integer.parseInt(idStr);
                // Fetch booking details from database
                Map<String, Object> details = bookingDAO.getBookingDetailsById(id);
                
                if (details != null && !details.isEmpty()) {
                    // Add detailId to map for form submission
                    details.put("detailId", id);
                    
                    // Check if this session booking is already completed (cannot edit completed sessions)
                    Integer categoryId = (Integer) details.get("categoryId");
                    String liveStatus = (String) details.get("liveStatus");
                    
                    // Debug logging for troubleshooting
                    System.out.println("Admin View - detailId: " + id + ", categoryId: " + categoryId + ", liveStatus: " + liveStatus);
                    
                    // Determine if session is completed (Category 4 is meal delivery, others are services)
                    boolean isCompleted = categoryId != null && categoryId != 4 && liveStatus != null && liveStatus.equals("Completed");
                    request.setAttribute("isSessionCompleted", isCompleted);
                    
                    request.setAttribute("bookingDetails", details);
                    
                    // Fetch available caregivers for this service (for editing assignment)
                    Object svcIdObj = details.get("serviceId");
                    if (svcIdObj != null) {
                        int serviceId = (Integer) svcIdObj;
                        List<Caregiver> caregivers = bookingDAO.getCaregiversByServiceId(serviceId);
                        request.setAttribute("caregiverList", caregivers);
                    }
                }
                request.getRequestDispatcher("/ASSIGNMENT/admin/booking_details.jsp").forward(request, response);

            } else {
                // LIST ACTION: Display all bookings with filtering options
                // 1. Populate service dropdown for filtering
                request.setAttribute("services", serviceDAO.getAllServices());

                // 2. Capture all filter parameters from request
                String status = request.getParameter("statusFilter");        // Filter by status
                String start = request.getParameter("startDate");           // Filter by start date
                String end = request.getParameter("endDate");               // Filter by end date
                String svcId = request.getParameter("serviceId");           // Filter by service
                String custName = request.getParameter("customerSearch");   // Filter by customer name
  
                // 3. Get filtered booking results from database
                List<Map<String, Object>> bookings = bookingDAO.getAllBookings(status, start, end, svcId, custName);
                
                request.setAttribute("bookingsList", bookings);
                request.getRequestDispatcher("/ASSIGNMENT/admin/manage_bookings.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing booking: " + e.getMessage());
        }
    }

    /**
     * Handle POST requests to update booking details or status
     * Supports updating caregiver assignment, date/time, and booking status
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateDetails".equals(action)) {
            // UPDATE DETAILS ACTION: Modify booking caregiver, date, and time
            try {
                // Extract and validate all required form parameters
                String bookingIdStr = request.getParameter("bookingId");
                String detailIdStr = request.getParameter("detailId");
                String cgIdParam = request.getParameter("caregiverId");
                String sDate = request.getParameter("serviceDate");
                String sTime = request.getParameter("startTime");
                
                // Log parameters for debugging
                System.out.println("Raw form parameters - bookingId: '" + bookingIdStr + "', detailId: '" + detailIdStr + 
                                   "', caregiverId: '" + cgIdParam + "', date: '" + sDate + "', time: '" + sTime + "'");
                
                // Validate detailId exists (can be missing if form was disabled)
                if (detailIdStr == null || detailIdStr.trim().isEmpty()) {
                    System.out.println("ERROR: detailId is null or empty - likely submitted from disabled form");
                    response.sendRedirect("AdminManageBookings?error=Invalid%20request%20-%20session%20may%20be%20completed");
                    return;
                }
                
                int bId = Integer.parseInt(bookingIdStr);
                int detailId = Integer.parseInt(detailIdStr);
                
                // Verify session is not completed before allowing updates
                Map<String, Object> details = bookingDAO.getBookingDetailsById(detailId);
                Integer categoryId = (Integer) details.get("categoryId");
                String liveStatus = (String) details.get("liveStatus");
                
                // Debug logging
                System.out.println("Admin Update - detailId: " + detailId + ", categoryId: " + categoryId + ", liveStatus: " + liveStatus);
                
                // Block update if session is already completed
                if (categoryId != null && categoryId != 4 && liveStatus != null && liveStatus.equals("Completed")) {
                    System.out.println("BLOCKED: Cannot modify completed session");
                    response.sendRedirect("AdminManageBookings?action=view&id=" + detailId + "&error=Cannot%20modify%20completed%20session");
                    return;
                } 
                
                // Validate other required fields
                if (cgIdParam == null || cgIdParam.trim().isEmpty()) {
                    System.out.println("ERROR: caregiverId is null or empty");
                    response.sendRedirect("AdminManageBookings?action=view&id=" + detailId + "&error=Missing%20caregiver%20ID");
                    return;
                }
                
                int cgId = Integer.parseInt(cgIdParam);
                
                // Normalize time format for database compatibility (add seconds if missing)
                if (sTime != null && sTime.length() == 5) sTime += ":00";
                if (sTime != null && sTime.length() == 4) sTime = "0" + sTime + ":00";

                System.out.println("Calling adminFullUpdate with bId=" + bId + ", cgId=" + cgId + ", date=" + sDate + ", time=" + sTime);
                // Perform database update
                boolean success = bookingDAO.adminFullUpdate(bId, cgId, sDate, sTime);
                System.out.println("Update result: " + success);
                
                if (success) {
                    response.sendRedirect("AdminManageBookings?action=view&id=" + bId + "&message=Update Successful");
                } else {
                    response.sendRedirect("AdminManageBookings?action=view&id=" + bId + "&error=Update%20Failed%20-%20Database%20returned%20false");
                }
            } catch (Exception e) {
                System.out.println("EXCEPTION in updateDetails: " + e.getClass().getName() + " - " + e.getMessage());
                e.printStackTrace();
                response.sendRedirect("AdminManageBookings?error=Update%20Failed%20-%20" + e.getClass().getSimpleName());
            }
        } else if ("updateStatus".equals(action)) {
            // UPDATE STATUS ACTION: Change booking status (e.g., pending, completed, cancelled)
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String status = request.getParameter("status");
                // Update status in database
                bookingDAO.updateBookingStatus(id, status);
                response.sendRedirect("AdminManageBookings?message=Status Updated");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect("AdminManageBookings?error=Status Update Failed");
            }
        }
    }
} 
