<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: 11 Nov 2025 
    -- File: logout.jsp
    -- Description: Invalidates the user session but PRESERVES the persistent cart cookie.
--%>
<%@ page import="java.net.URLEncoder" %>
<%
    // Get current session ID
    String currentSessionId = session.getId();
    // Get application sessions map
    java.util.Map<String, java.util.Map<String, Object>> activeSessions = 
        (java.util.Map<String, java.util.Map<String, Object>>) application.getAttribute("activeSessions");
    // Remove session from map
    if (activeSessions != null) {
        activeSessions.remove(currentSessionId);
    }
    
    // Invalidate session object
    session.invalidate();

    // Set success message
    String message = "You have been successfully logged out.";
    
    // Redirect to home page
    response.sendRedirect("index.jsp?message=" + URLEncoder.encode(message, "UTF-8"));
%>