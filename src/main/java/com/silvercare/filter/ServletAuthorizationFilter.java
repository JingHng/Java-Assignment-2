//Done by: Wan Jing Hng
//Date: 30/1/2026
//Description: Servlet for system authorization - Proper MVC Controller


package com.silvercare.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Servlet Authorization Filter - Protects servlet endpoints
 * Ensures users are logged in and have appropriate roles for servlet access
 */
@WebFilter(urlPatterns = {
    "/AdminDashboard", "/AdminManageServices", "/AdminManageCustomers", 
    "/AdminManageCaregivers", "/AdminManageBookings", "/AdminManageFeedback",
    "/AdminManageGST", "/AdminReport", "/ServiceAnalytics", "/AdminEditUser",
    "/Dashboard", "/Checkout", "/ViewCart", "/ManageBookings", "/ViewHistory",
    "/AddToCartDB", "/SubmitFeedback", "/EditProfile", "/ProcessEditProfile",
    "/RemoveCartItem", "/PaymentSuccess", "/CheckAvailability", "/EditBooking",
    "/UpdateBooking", "/MyFeedback", "/ConfirmReceipt", "/ClearHistory",
    "/CaregiverDashboard", "/CaregiverAction"
})
public class ServletAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String servletPath = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();
        
        // Check if user is logged in
        Boolean isLoggedIn = (session != null) ? (Boolean) session.getAttribute("isLoggedIn") : null;
        String userRole = (session != null) ? (String) session.getAttribute("userRole") : null;
        
        if (isLoggedIn == null || !isLoggedIn || userRole == null) {
            // Not logged in - redirect to login
            httpResponse.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=Session+expired.+Please+log+in");
            return;
        }
        
        // Role-based servlet access control
        boolean authorized = false;
        
        if (isAdminServlet(servletPath)) {
            authorized = "admin".equalsIgnoreCase(userRole);
        } else if (isCustomerServlet(servletPath)) {
            authorized = "customer".equalsIgnoreCase(userRole);
        } else if (isCaregiverver(servletPath)) {
            authorized = "caregiver".equalsIgnoreCase(userRole);
        }
        
        if (authorized) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=Access+Denied");
        }
    }
    
    private boolean isAdminServlet(String path) {
        return path.startsWith("/Admin") || path.equals("/ServiceAnalytics");
    }
    
    private boolean isCustomerServlet(String path) {
        return path.equals("/Dashboard") || path.equals("/ViewCart") || 
               path.equals("/Checkout") || path.equals("/ManageBookings") ||
               path.equals("/ViewHistory") || path.equals("/AddToCartDB") ||
               path.equals("/SubmitFeedback") || path.equals("/EditProfile") ||
               path.equals("/ProcessEditProfile") || path.equals("/RemoveCartItem") ||
               path.equals("/PaymentSuccess") || path.equals("/CheckAvailability") ||
               path.equals("/EditBooking") || path.equals("/UpdateBooking") ||
               path.equals("/MyFeedback") || path.equals("/ConfirmReceipt") ||
               path.equals("/ClearHistory");
    }
    
    private boolean isCaregiverver(String path) {
        return path.startsWith("/Caregiver");
    }

    @Override
    public void destroy() {
        // Cleanup
    }
}
