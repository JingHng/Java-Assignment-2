// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: DAO for Cart management operations
// Handles all database operations for cart and their service mappings

package silvercare.dao;

import com.silvercare.model.CartItem;
import java.util.List;
import java.util.ArrayList;
import com.silvercare.util.DBConnection;
import java.sql.*;

public class CartDAO {

    // Get the latest cart ID for a customer
    public int getCartIdByCustomer(int customerId) throws SQLException {
        String sql = "SELECT cartid FROM cart WHERE customerid = ? ORDER BY lastmodified DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("cartid");
            }
        }
        return 0;
    }

    // Create a new cart for a customer
    public int createCart(int customerId) throws SQLException {
        String sql = "INSERT INTO cart (customerid, lastmodified) VALUES (?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create new cart record.");
    }

    // Add item to cart with PostgreSQL date/time casting
    public boolean addItemToCart(int cartId, int serviceId, Integer caregiverId, 
            String serviceDate, String startTime, double duration, 
            String requests, double price, double subtotal, 
            String selectedMeal, String dietaryNotes) {

        // Use PostgreSQL type casting (::date and ::time) to accept String inputs
        String sql = "INSERT INTO cartitemstorage (cartid, serviceid, caregiverid, " +
                     "service_date, start_time, duration_hours, specialrequests, " +
                     "selected_meal, dietary_notes, price, subtotal) " +
                     "VALUES (?, ?, ?, ?::date, ?::time, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartId);
            pstmt.setInt(2, serviceId);

            // Handle optional caregiver (meal deliveries)
            if (caregiverId == null || caregiverId == 0) {
                pstmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(3, caregiverId);
            }

            // PostgreSQL handles casting from string
            pstmt.setString(4, serviceDate);
            pstmt.setString(5, startTime);
            
            pstmt.setDouble(6, duration);
            pstmt.setString(7, requests);
            pstmt.setString(8, selectedMeal);
            pstmt.setString(9, dietaryNotes);
            pstmt.setDouble(10, price);
            pstmt.setDouble(11, subtotal);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all items in cart with service and caregiver details
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT cis.*, s.servicename, sc.categoryname, cg.name as caregivername " +
                     "FROM cartitemstorage cis " +
                     "JOIN service s ON cis.serviceid = s.serviceid " +
                     "JOIN service_category sc ON s.categoryid = sc.categoryid " +
                     "LEFT JOIN caregiver cg ON cis.caregiverid = cg.caregiverid " +
                     "WHERE cis.cartid = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setCartItemId(rs.getInt("cartitemid"));
                    item.setServiceId(rs.getInt("serviceid"));
                    
                    // Check for null caregiver ID
                    int caregiverId = rs.getInt("caregiverid");
                    if (!rs.wasNull()) {
                        item.setCaregiverId(caregiverId);
                    }
                    
                    item.setCaregiverName(rs.getString("caregivername"));
                    item.setServiceName(rs.getString("servicename"));
                    item.setCategory(rs.getString("categoryname"));
                    item.setPrice(rs.getDouble("price"));
                    item.setServiceDate(rs.getDate("service_date"));
                    item.setStartTime(rs.getString("start_time"));
                    item.setDurationHours(rs.getDouble("duration_hours"));
                    item.setSpecialRequests(rs.getString("specialrequests"));
                    item.setSelectedMeal(rs.getString("selected_meal"));
                    item.setDietaryNotes(rs.getString("dietary_notes"));
                    
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Remove a specific item from cart
    public boolean removeCartItem(int cartItemId, int cartId) throws SQLException {
        String sql = "DELETE FROM cartitemstorage WHERE cartitemid = ? AND cartid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartItemId);
            pstmt.setInt(2, cartId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Clear all items from a cart
    public boolean clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM cartitemstorage WHERE cartid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, cartId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Get existing cart or create new one for customer
    public int getOrCreateCart(int customerId) throws SQLException {
        int cartId = getCartIdByCustomer(customerId);
        return (cartId == 0) ? createCart(customerId) : cartId;
    }
}