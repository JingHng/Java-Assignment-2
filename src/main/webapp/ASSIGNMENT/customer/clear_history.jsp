<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: Nov 2025
    -- File: clear_history.jsp
    -- Description: Handles the POST request to delete the user-specific history cookie.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>
<%@ page import="java.net.URLEncoder" %>

<%
    if (!"POST".equalsIgnoreCase(request.getMethod())) {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Only POST requests are allowed for clearing history.");
        return;
    }
    
    // Identify the current user session
    Integer loggedInCustomerId = (Integer) session.getAttribute("customerid");
    String historyCookieName = "SilverCare_History"; 
    
    // Check for an active login session; history is tied to specific user IDs
    if (loggedInCustomerId == null) {
        String errorMsg = URLEncoder.encode("You must be logged in to clear your history.", "UTF-8");
        response.sendRedirect("view_history.jsp?error=" + errorMsg);
        return;
    }
    
    // Build the unique cookie name assigned to this specific customer
    String userSpecificCookieName = historyCookieName + loggedInCustomerId;
    String successMsg = "";
    
    try {
        // Create a "tombstone" cookie with the same name to override the existing one
        Cookie historyCookie = new Cookie(userSpecificCookieName, "");
        
        // Setting MaxAge to 0 signals the browser to expire and remove the cookie immediately
        historyCookie.setMaxAge(0);
        
        // Match the original path scope to ensure the browser identifies the correct cookie to delete
        historyCookie.setPath("/"); 
        
        // Commit the deletion instruction to the response header
        response.addCookie(historyCookie);

        successMsg = URLEncoder.encode("Your browsing history has been cleared.", "UTF-8");
        
    } catch (Exception e) {
        // Log generic error if the cookie operation fails
        successMsg = URLEncoder.encode("An error occurred while clearing the history.", "UTF-8");
    }

    // Return the user to the history page and display the resulting status message
    response.sendRedirect("view_history.jsp?message=" + successMsg);
%>