// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for Booking management operations
// Handles all database operations for bookings and their service mappings


package silvercare.dao;

import com.silvercare.model.CartItem;
import com.silvercare.model.Caregiver;
import com.silvercare.util.DBConnection;
import java.sql.*;
import java.util.*;

public class BookingDAO {

	// Fetch all bookings with optional filters (status, date range, service, customer name)
	public List<Map<String, Object>> getAllBookings(String status, String startDate, String endDate, String serviceId, String customerSearch) throws SQLException {
	    List<Map<String, Object>> list = new ArrayList<>();
	    
	    // Build dynamic SQL with first service name and service count
	    StringBuilder sql = new StringBuilder(
	        "SELECT b.bookingid, " +
	        "COALESCE(b.bookingdate, (SELECT MIN(bd2.service_date) FROM booking_details bd2 WHERE bd2.bookingid = b.bookingid)) as bookingdate, " +
	        "b.totalamount, b.status, " +
	        "c.firstname || ' ' || c.lastname AS customername, " +
	        "(SELECT COUNT(*) FROM booking_details bd WHERE bd.bookingid = b.bookingid) as servicecount, " +
	        "(SELECT s.servicename FROM booking_details bd " +
	        " JOIN service s ON bd.serviceid = s.serviceid " +
	        " WHERE bd.bookingid = b.bookingid " +
	        " ORDER BY bd.detailid LIMIT 1) as servicename " +
	        "FROM bookings b " +
	        "JOIN customers c ON b.customerid = c.customerid " +
	        "WHERE 1=1 "
	    );

	    // Apply filters if provided
	    if (status != null && !status.isEmpty()) sql.append(" AND b.status = ? ");
	    if (startDate != null && !startDate.isEmpty()) {
	        sql.append(" AND (b.bookingdate >= ?::timestamp OR ");
	        sql.append(" EXISTS (SELECT 1 FROM booking_details bd WHERE bd.bookingid = b.bookingid AND bd.service_date >= ?::date)) ");
	    }
	    if (endDate != null && !endDate.isEmpty()) {
	        sql.append(" AND (b.bookingdate <= ?::timestamp OR ");
	        sql.append(" EXISTS (SELECT 1 FROM booking_details bd WHERE bd.bookingid = b.bookingid AND bd.service_date <= ?::date)) ");
	    }
	    if (serviceId != null && !serviceId.isEmpty()) {
	        sql.append(" AND b.bookingid IN (SELECT bookingid FROM booking_details WHERE serviceid = ?) ");
	    }
	    if (customerSearch != null && !customerSearch.isEmpty()) {
	        sql.append(" AND (c.firstname ILIKE ? OR c.lastname ILIKE ?) ");
	    }

	    sql.append(" ORDER BY b.bookingid DESC");

	    System.out.println("DEBUG SQL: " + sql.toString());
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
	        
	        // Bind parameters based on filters
	        int paramIdx = 1;
	        if (status != null && !status.isEmpty()) pstmt.setString(paramIdx++, status);
	        if (startDate != null && !startDate.isEmpty()) {
	            pstmt.setString(paramIdx++, startDate + " 00:00:00");
	            pstmt.setString(paramIdx++, startDate);  // For the EXISTS subquery
	        }
	        if (endDate != null && !endDate.isEmpty()) {
	            pstmt.setString(paramIdx++, endDate + " 23:59:59");
	            pstmt.setString(paramIdx++, endDate);  // For the EXISTS subquery
	        }
	        if (serviceId != null && !serviceId.isEmpty()) pstmt.setInt(paramIdx++, Integer.parseInt(serviceId));
	        if (customerSearch != null && !customerSearch.isEmpty()) {
	            String search = "%" + customerSearch + "%";
	            pstmt.setString(paramIdx++, search);
	            pstmt.setString(paramIdx++, search);
	        }

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Map<String, Object> row = new HashMap<>();
	                row.put("bookingId", rs.getInt("bookingid"));
	                
	                // Extract and log booking details
	                Timestamp bookingDate = rs.getTimestamp("bookingdate");
	                String serviceName = rs.getString("servicename");
	                int serviceCount = rs.getInt("servicecount");
	                
	                System.out.println("DEBUG - Booking ID: " + rs.getInt("bookingid") + 
	                                   ", Date: " + bookingDate + 
	                                   ", Service: " + serviceName +
	                                   ", Count: " + serviceCount);
	                
	                row.put("bookingDate", bookingDate);
	                row.put("totalAmount", rs.getDouble("totalamount"));
	                row.put("status", rs.getString("status"));
	                row.put("customerName", rs.getString("customername"));
	                row.put("serviceCount", serviceCount);
	                row.put("serviceName", serviceName);
	                list.add(row);
	            }
	        }
	    }
	    return list;
	}

	// Get specific booking details by detail ID for a customer
	public Map<String, Object> getBookingByDetailId(int detailId, int customerId) throws SQLException {
	    String sql = "SELECT b.status, b.delivery_status, b.live_status, bd.detailid, bd.bookingid, bd.serviceid, bd.service_date, " +
	                 "bd.start_time, bd.duration_hours, bd.caregiverid, bd.selected_meal, s.servicename, s.categoryid, " +
	                 "c.name as caregiverName " + 
	                 "FROM bookings b " +
	                 "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
	                 "JOIN service s ON bd.serviceid = s.serviceid " +
	                 "LEFT JOIN caregiver c ON bd.caregiverid = c.caregiverid " +
	                 "WHERE bd.detailid = ? AND b.customerid = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, detailId);
	        pstmt.setInt(2, customerId);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                Map<String, Object> b = new java.util.HashMap<>();
	                
	                // Get live status (may be null)
	                String rawLiveStatus = rs.getString("live_status");
	                System.out.println("DEBUG DAO - detailId: " + detailId + ", bookingId from DB: " + rs.getInt("bookingid") + ", live_status from DB: '" + rawLiveStatus + "'");
	                
	                b.put("detailID", rs.getInt("detailid"));
	                b.put("bookingId", rs.getInt("bookingid"));
	                b.put("status", rs.getString("status"));
	                b.put("liveStatus", rawLiveStatus);
	                b.put("serviceId", rs.getInt("serviceid"));
	                b.put("serviceName", rs.getString("servicename"));
	                b.put("categoryId", rs.getInt("categoryid")); 
	                b.put("deliveryStatus", rs.getString("delivery_status"));
	                b.put("selectedMeal", rs.getString("selected_meal"));
	                b.put("date", rs.getDate("service_date").toLocalDate());
	                b.put("time", rs.getTime("start_time").toLocalTime());
	                b.put("duration", rs.getDouble("duration_hours"));
	                b.put("caregiverId", rs.getInt("caregiverid"));
	                b.put("caregiverName", rs.getString("caregiverName"));
	                return b;
	            }
	        }
	    }
	    return null;
	}

	// Get booked time periods for caregiver availability check (excludes current detail)
	public List<Map<String, String>> getBookedPeriodsByDetail(int caregiverId, String date, int currentDetailId) throws SQLException {
	    List<Map<String, String>> periods = new java.util.ArrayList<>();
	    String sql = "SELECT start_time, duration_hours FROM booking_details " +
	                 "WHERE caregiverid=? AND service_date=? AND detailid != ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, caregiverId);
	        pstmt.setDate(2, java.sql.Date.valueOf(date));
	        pstmt.setInt(3, currentDetailId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Map<String, String> p = new java.util.HashMap<>();
	                p.put("start", rs.getTime("start_time").toLocalTime().toString());
	                p.put("duration", String.valueOf((long)(rs.getDouble("duration_hours") * 60)));
	                periods.add(p);
	            }
	        }
	    }
	    return periods;
	}

    public List<Caregiver> getCaregiversByServiceId(int serviceId) throws SQLException {
        List<Caregiver> list = new ArrayList<>();
        String sql = "SELECT c.caregiverid, c.name, c.specialty " +
                     "FROM caregiver c " +
                     "JOIN service_caregiver sc ON c.caregiverid = sc.caregiverid " +
                     "WHERE sc.serviceid = ? ORDER BY c.name ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Caregiver c = new Caregiver();
                    c.setCaregiverId(rs.getInt("caregiverid"));
                    c.setName(rs.getString("name"));
                    c.setSpecialty(rs.getString("specialty"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    // Update booking status (Confirmed, Cancelled, Completed, etc.)
    public boolean updateBookingStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE bookingid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Admin: Update all booking details (caregiver, date, time)
    public boolean adminFullUpdate(int bId, int cgId, String date, String time) throws SQLException {
        String sql = "UPDATE booking_details SET caregiverid = ?, service_date = CAST(? AS DATE), " +
                     "start_time = CAST(? AS TIME) WHERE bookingid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cgId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setInt(4, bId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Get all bookings for a specific customer (for history/dashboard)
    public List<Map<String, Object>> getCustomerBookings(int customerId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT b.bookingid, b.bookingdate, b.totalamount, b.status, s.servicename " +
                     "FROM bookings b " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "WHERE b.customerid = ? ORDER BY b.bookingid DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookingId", rs.getInt("bookingid"));
                    row.put("bookingDate", rs.getTimestamp("bookingdate"));
                    row.put("totalAmount", rs.getDouble("totalamount"));
                    row.put("status", rs.getString("status"));
                    row.put("serviceName", rs.getString("servicename"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    // Update booking detail record with conflict checking for caregiver schedule
    public boolean updateBookingDetails(int detailId, int caregiverId, String date, String startTime, double duration) throws Exception {
        // 1. ADD THIS PAD LOGIC: Fixes "8:00" -> "08:00"
        if (startTime != null && startTime.length() == 4) {
            startTime = "0" + startTime;
        }

        // SQL to check for schedule conflicts
        String checkSql = "SELECT start_time, duration_hours FROM booking_details "
                        + "WHERE caregiverid = ? AND service_date = ? AND detailid != ?";
        
        // SQL to update booking details
        String updateSql = "UPDATE booking_details SET caregiverid = ?, service_date = ?, start_time = ?, duration_hours = ? "
                         + "WHERE detailid = ?";

        try (Connection conn = DBConnection.getConnection()) { 
            // Check for time conflicts if caregiver is assigned
            if (caregiverId != -1) {
                // LocalTime.parse now works because startTime is "08:00"
                java.time.LocalTime newStart = java.time.LocalTime.parse(startTime);
                java.time.LocalTime newEnd = newStart.plusMinutes((long) (duration * 60));
                java.time.LocalDate newDate = java.time.LocalDate.parse(date);

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, caregiverId);
                    checkStmt.setDate(2, java.sql.Date.valueOf(newDate));
                    checkStmt.setInt(3, detailId); 
                    
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        while (rs.next()) {
                            // Check if time slots overlap
                            java.time.LocalTime bookedStart = rs.getTime("start_time").toLocalTime();
                            double bookedDur = rs.getDouble("duration_hours");
                            java.time.LocalTime bookedEnd = bookedStart.plusMinutes((long) (bookedDur * 60));

                            if (newStart.isBefore(bookedEnd) && newEnd.isAfter(bookedStart)) {
                                throw new Exception("Time slot conflict: Caregiver is already booked.");
                            }
                        }
                    }
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                // Handle meal delivery (no caregiver)
                if (caregiverId == -1) {
                    pstmt.setNull(1, java.sql.Types.INTEGER);
                } else {
                    pstmt.setInt(1, caregiverId);
                }
                
                pstmt.setDate(2, java.sql.Date.valueOf(date));
                
                // Convert time format to SQL Time
                String sqlTimeStr = startTime.length() == 5 ? startTime + ":00" : startTime;
                pstmt.setTime(3, java.sql.Time.valueOf(sqlTimeStr));
                
                pstmt.setDouble(4, duration);
                pstmt.setInt(5, detailId); 
                
                return pstmt.executeUpdate() > 0;
            }
        }
    }
    
    // Get all active bookings for a customer (excludes cancelled)
    public List<Map<String, Object>> getActiveBookingsByCustomer(int customerId) throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT b.bookingid, b.status, b.live_status, b.delivery_status, b.totalamount, " +
                     "b.actual_check_in, b.actual_check_out, " + // Added timestamps
                     "bd.detailid, bd.service_date, bd.start_time, bd.selected_meal, " +
                     "s.servicename, s.categoryid, " +
                     "c.name as caregiver_name " +
                     "FROM bookings b " +
                     "LEFT JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "LEFT JOIN service s ON bd.serviceid = s.serviceid " +
                     "LEFT JOIN caregiver c ON bd.caregiverid = c.caregiverid " +
                     "WHERE b.customerid = ? AND b.status != 'Cancelled' " +
                     "ORDER BY bd.service_date DESC";

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    int catId = rs.getInt("categoryid");
                    
                    // Universal booking IDs
                    map.put("id", rs.getInt("bookingid"));            
                    map.put("bookingId", rs.getInt("bookingid"));

                    // Live tracking status fields
                    String liveStat = rs.getString("live_status");
                    map.put("liveStatus", (liveStat == null || liveStat.isEmpty()) ? "Scheduled" : liveStat);
                    map.put("deliveryStatus", rs.getString("delivery_status"));
                    map.put("checkIn", rs.getTimestamp("actual_check_in"));
                    map.put("checkOut", rs.getTimestamp("actual_check_out"));
                    
                    // Service and booking data
                    map.put("service", rs.getString("servicename"));  
                    map.put("serviceName", rs.getString("servicename"));
                    map.put("date", rs.getDate("service_date"));      
                    map.put("serviceDate", rs.getDate("service_date")); 
                    map.put("time", rs.getTime("start_time"));        
                    map.put("startTime", rs.getString("start_time"));
                    map.put("amount", rs.getDouble("totalamount"));   
                    map.put("status", rs.getString("status"));
                    map.put("categoryId", catId);
                    map.put("selectedMeal", rs.getString("selected_meal"));
                    
                    // Display caregiver or "NIL" for meal delivery
                    String cg = rs.getString("caregiver_name");
                    String displayCG = (catId == 4) ? "NIL" : (cg != null ? cg : "Pending...");
                    map.put("caregiver", displayCG);
                    map.put("caregiverName", displayCG);
                    
                    bookings.add(map);
                }
            }
        }
        return bookings;
    }
    
    // Get editable booking by ID (only Confirmed/Pending status)
    public Map<String, Object> getBookingById(int bookingId, int customerId) throws SQLException {
        String sql = "SELECT b.status, b.delivery_status, bd.serviceid, bd.service_date, bd.start_time, " +
                     "bd.duration_hours, bd.caregiverid, bd.selected_meal, s.servicename, s.categoryid, " +
                     "c.name as caregiverName " + 
                     "FROM bookings b JOIN booking_details bd ON b.bookingid=bd.bookingid " +
                     "JOIN service s ON bd.serviceid=s.serviceid " +
                     "LEFT JOIN caregiver c ON bd.caregiverid=c.caregiverid " +
                     "WHERE b.bookingid=? AND b.customerid=? AND b.status IN ('Confirmed','Pending')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setInt(2, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> b = new HashMap<>();
                    b.put("status", rs.getString("status"));
                    b.put("serviceId", rs.getInt("serviceid"));
                    b.put("serviceName", rs.getString("servicename"));
                    b.put("categoryId", rs.getInt("categoryid")); 
                    
                    b.put("deliveryStatus", rs.getString("delivery_status"));
                    b.put("selectedMeal", rs.getString("selected_meal"));
                    
                    b.put("date", rs.getDate("service_date").toLocalDate());
                    b.put("time", rs.getTime("start_time").toLocalTime());
                    b.put("duration", rs.getDouble("duration_hours"));
                    b.put("caregiverId", rs.getInt("caregiverid"));
                    b.put("caregiverName", rs.getString("caregiverName"));
                    return b;
                }
            }
        }
        return null;
    }

    // Get booked time periods for availability conflict check
    public List<Map<String, String>> getBookedPeriods(int caregiverId, String date, int currentBookingId) throws SQLException {
        List<Map<String, String>> periods = new ArrayList<>();
        String sql = "SELECT start_time, duration_hours FROM booking_details " +
                     "WHERE caregiverid=? AND service_date=? AND bookingid!=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, caregiverId);
            pstmt.setDate(2, java.sql.Date.valueOf(date));
            pstmt.setInt(3, currentBookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> p = new HashMap<>();
                    p.put("start", rs.getTime("start_time").toLocalTime().toString());
                    p.put("duration", String.valueOf((long)(rs.getDouble("duration_hours") * 60)));
                    periods.add(p);
                }
            }
        }
        return periods;
    }
    
    // Process cart checkout: create booking and booking_details records
	public boolean processCheckout(int customerId, List<CartItem> cartItems) throws SQLException {
    		String bookingSql = "INSERT INTO bookings (customerid, totalamount, status, live_status, delivery_status) " +
                    "VALUES (?, ?, 'Confirmed', 'Pending', 'Food Delivery Company Order Received')";
    		String detailSql = "INSERT INTO booking_details (bookingid, serviceid, caregiverid, service_date, start_time, duration_hours, price, subtotal, selected_meal, dietary_notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            Connection conn = null;
            try {                conn = DBConnection.getConnection();
                conn.setAutoCommit(false);

                // Calculate total from all cart items
                double grandTotal = 0;
                for (CartItem item : cartItems) grandTotal += (item.getPrice() * item.getDurationHours()); 

                // Create the bookings record
                int generatedBookingId = 0;
                try (PreparedStatement psB = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                    psB.setInt(1, customerId);
                    psB.setDouble(2, grandTotal);
                    psB.executeUpdate();
                    ResultSet rs = psB.getGeneratedKeys();
                    if (rs.next()) generatedBookingId = rs.getInt(1);
                }

                // Insert all cart items into booking_details
                try (PreparedStatement psD = conn.prepareStatement(detailSql)) {
                    for (CartItem item : cartItems) {
                        psD.setInt(1, generatedBookingId);
                        psD.setInt(2, item.getServiceID());
                        // Handle meal delivery (no caregiver)
                        if (item.getCaregiverID() <= 0) psD.setNull(3, java.sql.Types.INTEGER);
                        else psD.setInt(3, item.getCaregiverID());
                        
                        psD.setDate(4, new java.sql.Date(item.getBookingDate().getTime())); 
                        // Ensure time format is correct
                        String timeStr = item.getStartTime();
                        if (timeStr != null && timeStr.length() == 5) timeStr += ":00";
                        psD.setTime(5, (timeStr != null) ? java.sql.Time.valueOf(timeStr) : java.sql.Time.valueOf("00:00:00"));
                        
                        psD.setDouble(6, item.getDurationHours());
                        psD.setDouble(7, item.getPrice());
                        psD.setDouble(8, (item.getPrice() * item.getDurationHours()));
                        // Save meal selection and dietary notes
                        psD.setString(9, item.getSelectedMeal()); 
                        psD.setString(10, item.getDietaryNotes());
                        psD.addBatch();
                    }
                    psD.executeBatch();
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                if (conn != null) conn.rollback();
                throw e;
            } finally {
                if (conn != null) conn.close();
            }
        }

	// Get upcoming bookings for dashboard (excludes completed/cancelled)
	public List<Map<String, Object>> getUpcomingBookings(int customerId) {
    	    List<Map<String, Object>> bookings = new ArrayList<>();
    	    // Updated WHERE clause: Exclude 'Cancelled' and 'Customer Received'
    	    String sql = "SELECT b.bookingid, b.status, b.live_status, b.delivery_status, " +
    	                 "bd.service_date, bd.start_time, bd.selected_meal, bd.dietary_notes, " +
    	                 "s.servicename, s.categoryid, " +
    	                 "cg.name as caregivername " +
    	                 "FROM bookings b " +
    	                 "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
    	                 "JOIN service s ON bd.serviceid = s.serviceid " +
    	                 "LEFT JOIN caregiver cg ON bd.caregiverid = cg.caregiverid " +
    	                 "WHERE b.customerid = ? " +
    	                 "AND b.status != 'Completed' " +
    	                 "AND b.delivery_status NOT IN ('Cancelled', 'Customer Received') " + 
    	                 "AND bd.service_date >= CURRENT_DATE " + 
    	                 "ORDER BY bd.service_date ASC, bd.start_time ASC";

    	    try (Connection conn = DBConnection.getConnection();
    	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
    	        pstmt.setInt(1, customerId);
    	        ResultSet rs = pstmt.executeQuery();
    	        while (rs.next()) {
    	            Map<String, Object> booking = new HashMap<>();
    	            booking.put("bookingId", rs.getInt("bookingid"));
    	            booking.put("serviceName", rs.getString("servicename"));
    	            booking.put("categoryId", rs.getInt("categoryid"));
    	            booking.put("caregiverName", rs.getString("caregivername") != null ? rs.getString("caregivername") : "Not Assigned");
    	            booking.put("serviceDate", rs.getDate("service_date"));
    	            
    	            java.sql.Time st = rs.getTime("start_time");
    	            booking.put("startTime", st != null ? st.toString().substring(0, 5) : "N/A");
    	            
    	            booking.put("status", rs.getString("status"));
    	            booking.put("liveStatus", rs.getString("live_status"));
    	            booking.put("selectedMeal", rs.getString("selected_meal"));
    	            booking.put("dietaryNotes", rs.getString("dietary_notes"));
    	            
    	            booking.put("deliveryStatus", rs.getString("delivery_status")); 
    	            
    	            bookings.add(booking);
    	        }
    	    } catch (SQLException e) { e.printStackTrace(); }
    	    return bookings;
    	}
    	
    private List<Map<String, Object>> executeBookingQuery(String sql, int customerId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingId", rs.getInt("bookingid"));
                    map.put("serviceDate", rs.getDate("service_date"));
                    map.put("startTime", rs.getString("start_time"));
                    map.put("serviceName", rs.getString("servicename"));
                    map.put("caregiverName", rs.getString("caregivername") != null ? rs.getString("caregivername") : "N/A");
                    map.put("amount", rs.getDouble("subtotal"));
                    map.put("duration", rs.getDouble("duration_hours")); 
                    map.put("status", rs.getString("status")); 
                    
                    // Live status for tracking
                    map.put("liveStatus", rs.getString("live_status")); 
                    
                    list.add(map);
                }
            }
        }
        return list;
    }

    // Get past bookings with review eligibility check
    public List<Map<String, Object>> getPastBookings(int customerId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        // Join bookings with details and check if feedback exists
        String sql = "SELECT b.bookingid, b.status, b.delivery_status, b.totalamount, " +
                     "bd.serviceid, bd.service_date, bd.start_time, bd.selected_meal, " +
                     "s.servicename, " +
                     "(SELECT COUNT(*) FROM feedback f WHERE f.bookingid = b.bookingid) as feedback_count " +
                     "FROM bookings b " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "WHERE b.customerid = ? " +
                     "ORDER BY bd.service_date DESC";

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
            	while (rs.next()) {
            	    Map<String, Object> row = new HashMap<>();
            	    row.put("bookingId", rs.getInt("bookingid"));
            	    row.put("status", rs.getString("status"));
            	    row.put("deliveryStatus", rs.getString("delivery_status"));
            	    row.put("amount", rs.getDouble("totalamount"));
            	    row.put("serviceName", rs.getString("servicename"));
            	    
            	    java.sql.Timestamp serviceDate = rs.getTimestamp("service_date");
            	    row.put("serviceDate", serviceDate);
            	    
            	    // --- NEW LOGIC START ---
            	    String status = rs.getString("status");
            	    String deliveryStatus = rs.getString("delivery_status");
            	    int feedbackCount = rs.getInt("feedback_count");
            	    
            	    // Check if date is today or in the past
            	    boolean isPastOrToday = serviceDate.before(new java.util.Date()) || 
            	                            serviceDate.equals(new java.util.Date());
            	                            
            	    // Eligible if (Past Date OR Confirmed OR Received) AND not Cancelled AND no feedback yet
            	    boolean canReview = !"Cancelled".equalsIgnoreCase(deliveryStatus) && 
            	                        (isPastOrToday || "Confirmed".equalsIgnoreCase(status) || "Customer Received".equalsIgnoreCase(deliveryStatus)) && 
            	                        feedbackCount == 0;
            	                        
            	    row.put("canReview", canReview);

            	    list.add(row);
            	}
            }
        }
        return list;
    }
    
    // Update delivery status (REST API integration point)
    public boolean updateDeliveryStatus(int id, String status) throws SQLException {
        String sql = "UPDATE bookings SET delivery_status = ? WHERE bookingid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Get complete booking details by detail ID (for invoice/tracking)
    public Map<String, Object> getBookingDetailsById(int detailId) throws SQLException {
        Map<String, Object> details = new HashMap<>();
        // Join all related tables for complete booking information
        String sql = "SELECT bd.detailid, bd.bookingid, bd.serviceid, bd.caregiverid, " +
                     "bd.service_date, bd.start_time, bd.duration_hours, bd.selected_meal, " +
                     "b.status, b.totalamount, b.delivery_status, b.live_status, " + 
                     "s.servicename, s.categoryid, s.price, " +
                     "c.name AS caregiver_name, " +
                     "cust.firstname, cust.lastname, cust.email " +
                     "FROM booking_details bd " +
                     "JOIN service s ON bd.serviceid = s.serviceid " + 
                     "JOIN bookings b ON bd.bookingid = b.bookingid " +
                     "JOIN customers cust ON b.customerid = cust.customerid " + 
                     "LEFT JOIN caregiver c ON bd.caregiverid = c.caregiverid " +
                     "WHERE bd.detailid = ?";

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detailId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int catId = rs.getInt("categoryid");
                    details.put("detailId", rs.getInt("detailid"));
                    details.put("bookingId", rs.getInt("bookingid"));
                    details.put("categoryId", catId);
                    details.put("serviceDate", rs.getDate("service_date")); 
                    details.put("startTime", rs.getString("start_time"));
                    details.put("durationHours", rs.getDouble("duration_hours"));
                    details.put("selectedMeal", rs.getString("selected_meal"));
                    
                    details.put("status", rs.getString("status"));
                    details.put("liveStatus", rs.getString("live_status"));
                    details.put("deliveryStatus", rs.getString("delivery_status"));
                    details.put("serviceName", rs.getString("servicename"));
                    
                    details.put("customerEmail", rs.getString("email"));
                    details.put("servicePrice", rs.getDouble("price"));
                    details.put("totalAmount", rs.getDouble("totalamount"));
                    details.put("serviceId", rs.getInt("serviceid")); 
                    
                    // Build customer full name
                    String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                    details.put("customerName", fullName);

                    // Handle meal delivery category (no caregiver)
                    if (catId == 4) {
                        details.put("caregiverName", "NIL");
                        details.put("caregiverId", null);
                    } else {
                        details.put("caregiverName", rs.getString("caregiver_name"));
                        details.put("caregiverId", rs.getInt("caregiverid"));
                    }
                }
            }
        }
        return details;
    }
    
    // Get dashboard bookings with live tracking info (sorted by upcoming)
    public List<Map<String, Object>> getDashboardBookings(int customerId) throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        // Join with caregiver table for name display
        String sql = "SELECT b.bookingid, b.status, b.live_status, b.delivery_status, " +
                     "b.actual_check_in, b.actual_check_out, " +
                     "bd.detailid, bd.service_date, bd.start_time, bd.selected_meal, " +
                     "s.servicename, s.categoryid, " +
                     "c.name AS caregiver_name " + 
                     "FROM bookings b " +
                     "JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "JOIN service s ON bd.serviceid = s.serviceid " +
                     "LEFT JOIN caregiver c ON bd.caregiverid = c.caregiverid " + 
                     "WHERE b.customerid = ? AND b.status != 'Cancelled' " +
                     "ORDER BY bd.service_date ASC"; // Show upcoming first

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("bookingId", rs.getInt("bookingid"));
                    map.put("detailID", rs.getInt("detailid"));
                    map.put("serviceName", rs.getString("servicename"));
                    map.put("serviceDate", rs.getDate("service_date"));
                    map.put("startTime", rs.getString("start_time"));
                    map.put("liveStatus", rs.getString("live_status"));
                    map.put("deliveryStatus", rs.getString("delivery_status"));
                    map.put("categoryId", rs.getInt("categoryid"));
                    map.put("selectedMeal", rs.getString("selected_meal"));
                    map.put("checkIn", rs.getTimestamp("actual_check_in"));
                    map.put("checkOut", rs.getTimestamp("actual_check_out"));
                    
                    // Map caregiver name from join
                    map.put("caregiverName", rs.getString("caregiver_name"));
                    
                    bookings.add(map);
                }
            }
        }
        return bookings;
    }

    // Get bookings for manage page (table view with action buttons)
    public List<Map<String, Object>> getManageBookings(int customerId) throws SQLException {
        List<Map<String, Object>> bookings = new ArrayList<>();
        String sql = "SELECT b.bookingid, b.status, b.totalamount, b.live_status, " +
                     "bd.detailid, bd.service_date, bd.start_time, s.servicename, s.categoryid, " +
                     "c.name as caregiver_name " +
                     "FROM bookings b " +
                     "LEFT JOIN booking_details bd ON b.bookingid = bd.bookingid " +
                     "LEFT JOIN service s ON bd.serviceid = s.serviceid " +
                     "LEFT JOIN caregiver c ON bd.caregiverid = c.caregiverid " +
                     "WHERE b.customerid = ? AND b.status != 'Cancelled' " +
                     "ORDER BY bd.service_date DESC";

        try (Connection conn = com.silvercare.util.DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("bookingid"));            
                    map.put("detailID", rs.getInt("detailid")); // CRITICAL for View/Edit
                    map.put("service", rs.getString("servicename"));
                    map.put("date", rs.getDate("service_date"));
                    map.put("time", rs.getTime("start_time"));
                    map.put("amount", rs.getDouble("totalamount"));
                    map.put("status", rs.getString("status"));
                    map.put("liveStatus", rs.getString("live_status"));
                    map.put("categoryId", rs.getInt("categoryid"));
                    map.put("caregiver", rs.getString("caregiver_name"));
                    bookings.add(map);
                }
            }
        }
        return bookings;
    }
    }
    