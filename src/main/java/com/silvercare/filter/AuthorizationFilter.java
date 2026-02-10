//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for system authorization - Proper MVC Controller


package com.silvercare.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Authorization Filter - Enforces role-based access control
 * Protects admin, customer, caregiver, and delivery pages from unauthorized access
 */
@WebFilter(urlPatterns = {
    "/ASSIGNMENT/admin/*",
    "/ASSIGNMENT/customer/*", 
    "/ASSIGNMENT/caregiver/*",
    "/ASSIGNMENT/delivery/*"
})
public class AuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Allow public access to index_delivery.jsp (landing page for delivery partners)
        if (requestURI.endsWith("/index_delivery.jsp")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check if user is logged in
        Boolean isLoggedIn = (session != null) ? (Boolean) session.getAttribute("isLoggedIn") : null;
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        
        if (isLoggedIn == null || !isLoggedIn || userRole == null) {
            // User not logged in - redirect to login page
            httpResponse.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=Please+log+in+to+access+this+page");
            return;
        }
        
        // Check role-based access control
        boolean authorized = false;
        
        if (requestURI.contains("/ASSIGNMENT/admin/")) {
            authorized = "admin".equalsIgnoreCase(userRole);
        } else if (requestURI.contains("/ASSIGNMENT/customer/")) {
            authorized = "customer".equalsIgnoreCase(userRole);
        } else if (requestURI.contains("/ASSIGNMENT/caregiver/")) {
            authorized = "caregiver".equalsIgnoreCase(userRole);
        } else if (requestURI.contains("/ASSIGNMENT/delivery/")) {
            authorized = "delivery".equalsIgnoreCase(userRole);
        }
        
        if (authorized) {
            // User has correct role - allow access
            chain.doFilter(request, response);
        } else {
            // User does not have permission - show access denied
            httpResponse.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=Access+Denied:+You+do+not+have+permission+to+view+this+page");
        }
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
