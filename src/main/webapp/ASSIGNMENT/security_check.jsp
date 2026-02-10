<%-- 
    Author(s): Wan Jing Hng 
    Date: Feb 2026 
    File: security_check.jsp 
    Description: Security Page to be placed at the top of protected JSP pages which enforces RBAC
--%>


<%@ page import="com.silvercare.util.SessionValidator" %>


<%
    // Get required role from page parameter 
    String requiredRole = (String) request.getAttribute("requiredRole");
    
    if (requiredRole == null) {
        // Fallback: infer role from URL path
        String path = request.getRequestURI();
        if (path.contains("/admin/")) {
            requiredRole = "admin";
        } else if (path.contains("/customer/")) {
            requiredRole = "customer";
        } else if (path.contains("/caregiver/")) {
            requiredRole = "caregiver";
        }
    }
    
    // Check if user is authorized
    if (!SessionValidator.isLoggedIn(request)) {
        response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp?message=Please+log+in+to+access+this+page");
        return;
    }
    
    if (requiredRole != null && !SessionValidator.hasRole(request, requiredRole)) {
        response.sendRedirect(request.getContextPath() + "/ASSIGNMENT/login.jsp?message=Access+Denied");
        return;
    }
%>
