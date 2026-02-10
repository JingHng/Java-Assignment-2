//Done by: Wan Jing Hng 
//Date: 5/1/2026
//Description: Centralizes data access using the MVC pattern and Value Beans
//Handles all database operations related to users

package silvercare.dao;
import com.silvercare.model.Caregiver;
import java.util.List;
import java.util.ArrayList;
import com.silvercare.util.DBConnection;
import com.silvercare.model.User; 
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserDAO {

    // Register new customer with hashed password
	public boolean registerCustomer(User user) throws SQLException {
        String sql = "INSERT INTO customers (firstname, lastname, email, phone, address, password, " +
                     "registrationdate, status, preferredlanguage, postal_code, medical_condition, " +
                     "emergency_contact_name, emergency_contact_phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, NOW(), 'active', ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();    
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getAddress());
            pstmt.setString(6, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hash password
            pstmt.setString(7, user.getLanguage());
            pstmt.setString(8, user.getPostalCode());
            pstmt.setString(9, user.getMedicalCondition());
            pstmt.setString(10, user.getEmergencyName());
            pstmt.setString(11, user.getEmergencyPhone());
            
            return pstmt.executeUpdate() > 0;
        }
    }

    // Authenticate user with bcrypt password verification
	public User authenticate(String loginId, String password, String userRole) throws SQLException {
	    User user = null;
	    String sql;

	    // Select query based on user role
	    if ("admin".equalsIgnoreCase(userRole)) {
	        sql = "SELECT adminid, adminname, userid, role, email, password FROM public.admin_user WHERE userid = ?";
	    } else if ("customer".equalsIgnoreCase(userRole)) {
	        sql = "SELECT customerid, firstname, email, phone, address, preferredlanguage, password FROM customers WHERE email = ? AND status = 'active'";
	    } else if ("caregiver".equalsIgnoreCase(userRole)) {
	        sql = "SELECT caregiverid, name, email, phone, password FROM caregiver WHERE username = ? AND LOWER(status) = 'active'";	    } else {
	        return null;
	    }

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, loginId);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {                // Verify password with bcrypt
                String storedHash = rs.getString("password");
                if (!BCrypt.checkpw(password, storedHash)) {
                    return null; // Password mismatch
                }
                	                user = new User(); 
	                
	                if ("admin".equalsIgnoreCase(userRole)) {
	                    // Admin role fields
	                    user.setUserId(rs.getInt("adminid")); 
	                    user.setFirstName(rs.getString("adminname"));
	                    user.setEmail(rs.getString("email"));
	                } else if ("customer".equalsIgnoreCase(userRole)) {
	                    // Customer role fields
	                    user.setUserId(rs.getInt("customerid")); 
	                    user.setFirstName(rs.getString("firstname"));
	                    user.setEmail(rs.getString("email"));
	                    user.setPhone(rs.getString("phone"));
	                    user.setAddress(rs.getString("address"));
	                    user.setLanguage(rs.getString("preferredlanguage"));
	                } else if ("caregiver".equalsIgnoreCase(userRole)) {
	                    // Caregiver role fields
	                    user.setUserId(rs.getInt("caregiverid")); 
	                    user.setFirstName(rs.getString("name"));
	                    user.setEmail(rs.getString("email"));
	                    user.setPhone(rs.getString("phone"));
	                    // Caregivers don't need address/language in User bean
	                }
	            }
	        }
	    }
	    return user;
	}
    
    
    
    // Get caregiver basic profile by ID
    public Caregiver getCaregiverById(int id) throws SQLException {
        Caregiver c = null;
        String sql = "SELECT * FROM caregiver WHERE caregiverid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    c = new Caregiver();
                    c.setCaregiverId(rs.getInt("caregiverid"));
                    c.setName(rs.getString("name"));
                    c.setEmail(rs.getString("email"));
                    c.setPhone(rs.getString("phone"));
                    c.setSpecialty(rs.getString("specialty"));
                    c.setBio(rs.getString("bio"));
                    c.setImage(rs.getString("image"));
                }
            }
        }
        return c;
    }

    // Get list of services provided by a caregiver
    public List<String> getServicesByCaregiverId(int id) throws SQLException {
        List<String> services = new ArrayList<>();
        String sql = "SELECT s.servicename FROM service s " +
                     "JOIN service_caregiver sc ON s.serviceid = sc.serviceid " +
                     "WHERE sc.caregiverid = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    services.add(rs.getString("servicename"));
                }
            }
        }
        return services;
    }

    // Merge guest cart to logged-in customer
    public void mergeCart(int customerId, int persistentCartID) throws SQLException {
        String updateCartSQL = "UPDATE cart SET customerid = ?, lastmodified = NOW() WHERE cartid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateCartSQL)) {
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, persistentCartID);
            pstmt.executeUpdate();
        }
    }

    // Get most recent cart ID for a customer
    public int getLatestCartId(int customerId) throws SQLException {
        String sql = "SELECT cartid FROM cart WHERE customerid = ? ORDER BY lastmodified DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cartid");
                }
            }
        }
        return 0;
    }
    
    // Get customer details by ID (for profile editing)
    public User getUserById(int id) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM customers WHERE customerid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("customerid"));
                    user.setFirstName(rs.getString("firstname"));
                    user.setLastName(rs.getString("lastname"));
                    user.setEmail(rs.getString("email"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setPostalCode(rs.getString("postal_code"));
                    user.setMedicalCondition(rs.getString("medical_condition"));
                    user.setEmergencyName(rs.getString("emergency_contact_name"));
                    user.setEmergencyPhone(rs.getString("emergency_contact_phone"));
                }
            }
        }
        return user;
    }
    
    // Remove cart item with transactional cart update
    public boolean removeCartItem(int cartItemId, int cartId) throws SQLException {
        String deleteSQL = "DELETE FROM cartitemstorage WHERE cartitemid = ? AND cartid = ?";
        String updateCartSQL = "UPDATE cart SET lastmodified = NOW() WHERE cartid = ?";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            try (PreparedStatement deletePstmt = conn.prepareStatement(deleteSQL)) {
                deletePstmt.setInt(1, cartItemId);
                deletePstmt.setInt(2, cartId);
                int rows = deletePstmt.executeUpdate();

                if (rows > 0) {
                    try (PreparedStatement updatePstmt = conn.prepareStatement(updateCartSQL)) {
                        updatePstmt.setInt(1, cartId);
                        updatePstmt.executeUpdate();
                    }
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    // Update customer profile with optional password change
    public boolean updateUser(User user, String newPassword) throws SQLException {
        boolean updatePassword = (newPassword != null && !newPassword.trim().isEmpty());
        
        String sql = "UPDATE customers SET firstname=?, lastname=?, phone=?, address=?, " +
                     "postal_code=?, medical_condition=?, emergency_contact_name=?, emergency_contact_phone=?";
        
        if (updatePassword) {
            sql += ", password=? ";
        }
        sql += " WHERE customerid=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getAddress());
            pstmt.setString(5, user.getPostalCode());
            pstmt.setString(6, user.getMedicalCondition());
            pstmt.setString(7, user.getEmergencyName());
            pstmt.setString(8, user.getEmergencyPhone());

            if (updatePassword) {
                pstmt.setString(9, newPassword);
                pstmt.setInt(10, user.getUserId());
            } else {
                pstmt.setInt(9, user.getUserId());
            }

            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Get all active caregivers (newest first)
    public List<Caregiver> getAllCaregivers() throws SQLException {
        List<Caregiver> list = new ArrayList<>();
        // Sort by ID descending to show newest first
        String sql = "SELECT caregiverid, name, specialty, bio, image FROM caregiver WHERE status = 'active' ORDER BY caregiverid DESC"; 
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Caregiver c = new Caregiver();
                // Map database fields to model
                c.setCaregiverId(rs.getInt("caregiverid"));
                c.setName(rs.getString("name")); 
                c.setSpecialty(rs.getString("specialty"));
                c.setBio(rs.getString("bio"));
                c.setImage(rs.getString("image"));
                list.add(c);
            }
        }
        return list;
    }
    
    // Create caregiver with service mappings (transactional)
    public boolean createCaregiverWithServices(Caregiver cg, String[] serviceIds) {
        String caregiverSql = "INSERT INTO caregiver (name, email, phone, specialty, bio, image) VALUES (?, ?, ?, ?, ?, ?)";
        String joinSql = "INSERT INTO service_caregiver (caregiverid, serviceid) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert Caregiver
            try (PreparedStatement ps = conn.prepareStatement(caregiverSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, cg.getName());
                ps.setString(2, cg.getEmail());
                ps.setString(3, cg.getPhone());
                ps.setString(4, cg.getSpecialty());
                ps.setString(5, cg.getBio());
                ps.setString(6, cg.getImage());
                ps.executeUpdate();

                // Get generated caregiver ID
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int newId = rs.getInt(1);

                    // Insert service mappings for caregiver
                    if (serviceIds != null) {
                        try (PreparedStatement psJoin = conn.prepareStatement(joinSql)) {
                            for (String sId : serviceIds) {
                                psJoin.setInt(1, newId);
                                psJoin.setInt(2, Integer.parseInt(sId));
                                psJoin.addBatch();
                            }
                            psJoin.executeBatch();
                        }
                    }
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        }
    }
    // Add new caregiver with hashed password
    public boolean addCaregiver(Caregiver c) throws SQLException {
        String sql = "INSERT INTO caregiver (name, email, phone, specialty, bio, image, username, password, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getName());
            pstmt.setString(2, c.getEmail());
            pstmt.setString(3, c.getPhone());
            pstmt.setString(4, c.getSpecialty());
            pstmt.setString(5, c.getBio());
            pstmt.setString(6, c.getImage());
            pstmt.setString(7, c.getUsername());
            pstmt.setString(8, BCrypt.hashpw(c.getPassword(), BCrypt.gensalt())); // Hash password
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Get current GST rate from system settings
    public double getGSTRate() throws SQLException {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = 'gst_rate'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) return rs.getDouble("setting_value");
        }
        return 0.0; // Default if not found
    }

    // Update GST rate in system settings
    public boolean updateGSTRate(double newRate) throws SQLException {
        String sql = "UPDATE system_settings SET setting_value = ? WHERE setting_key = 'gst_rate'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newRate);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    // Search customers by medical condition and/or postal code
    public List<User> searchUsers(String condition, String postal) throws SQLException {
        List<User> list = new ArrayList<>();
        // Build query with optional filters
        String sql = "SELECT * FROM customers WHERE status = 'active'";
        
        // Append filters dynamically
        if (condition != null && !condition.isEmpty()) {
            sql += " AND medical_condition LIKE ?";
        }
        if (postal != null && !postal.isEmpty()) {
            sql += " AND postal_code LIKE ?";
        }
        
        sql += " ORDER BY customerid DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Bind filter parameters
            int paramIndex = 1;
            if (condition != null && !condition.isEmpty()) {
                pstmt.setString(paramIndex++, "%" + condition + "%");
            }
            if (postal != null && !postal.isEmpty()) {
                pstmt.setString(paramIndex++, postal + "%"); // Search by postal prefix
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("customerid"));
                    u.setFirstName(rs.getString("firstname"));
                    u.setLastName(rs.getString("lastname"));
                    u.setEmail(rs.getString("email"));
                    u.setPhone(rs.getString("phone"));
                    u.setPostalCode(rs.getString("postal_code"));
                    u.setMedicalCondition(rs.getString("medical_condition"));
                    u.setEmergencyName(rs.getString("emergency_contact_name"));
                    u.setEmergencyPhone(rs.getString("emergency_contact_phone"));
                    list.add(u);
                }
            }
        }
        return list;
    }
    
    // Soft delete user by setting status to inactive
    public boolean deleteUser(int id) throws SQLException {
        String sql = "UPDATE customers SET status = 'inactive' WHERE customerid = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}