package com.silvercare.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Session Validator - Utility class for validating user sessions and roles
 * Use this in servlets and JSPs to check authentication and authorization
 */
public class SessionValidator {
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
        return isLoggedIn != null && isLoggedIn;
    }
    
    /**
     * Get the current user's role
     */
    public static String getUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute("userRole");
    }
    
    /**
     * Check if user has admin role
     */
    public static boolean isAdmin(HttpServletRequest request) {
        String role = getUserRole(request);
        return "admin".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user has customer role
     */
    public static boolean isCustomer(HttpServletRequest request) {
        String role = getUserRole(request);
        return "customer".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user has caregiver role
     */
    public static boolean isCaregiver(HttpServletRequest request) {
        String role = getUserRole(request);
        return "caregiver".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user has delivery role
     */
    public static boolean isDelivery(HttpServletRequest request) {
        String role = getUserRole(request);
        return "delivery".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user is authorized for a specific role
     */
    public static boolean hasRole(HttpServletRequest request, String requiredRole) {
        if (requiredRole == null) {
            return false;
        }
        String userRole = getUserRole(request);
        return requiredRole.equalsIgnoreCase(userRole);
    }
    
    /**
     * Comprehensive authorization check - returns true if user is logged in and has the required role
     */
    public static boolean isAuthorized(HttpServletRequest request, String requiredRole) {
        return isLoggedIn(request) && hasRole(request, requiredRole);
    }
}
