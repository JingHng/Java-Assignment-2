//Done by: Jing Hng
//Description: Database centralized point

package com.silvercare.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials
    private static final String JDBC_URL = "jdbc:postgresql://ep-old-fog-a4d86qz8-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    private static final String DB_USER = "neondb_owner";
    private static final String DB_PASSWORD = "npg_o5lXmFeVD3yQ";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    
    /**
     * Establishes and returns a database connection.
     * @return The active Connection object.
     * @throws SQLException If a database access error occurs or the driver is not found.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load the PostgreSQL driver
            Class.forName(JDBC_DRIVER); 
            
            // Establish the connection
            return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            // Throw a more descriptive error if the driver is missing
            throw new SQLException("Database driver not found: " + JDBC_DRIVER, e);
        }
    }

    /**
     * Safely closes the database connection.
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignore) {
            }
        }
    }
    
    // --- Accessor methods  ---
    
    public static String getJdbcUrl() {
        return JDBC_URL;
    }
    
    public static String getDbUser() {
        return DB_USER;
    }

    /**
     * Simple utility to test connectivity.
     * @return true if connection is successful, false otherwise.
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null;
        } catch (SQLException e) {
            System.err.println("Test Connection Failed: " + e.getMessage());
            return false;
        } finally {
            closeConnection(conn);
        }
    }
}
