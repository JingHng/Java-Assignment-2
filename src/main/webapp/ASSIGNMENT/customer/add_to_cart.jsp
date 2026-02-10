<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: 30 Jan 2026 (Updated for MVC compliance)
    -- File: add_to_cart.jsp
    -- Description: DEPRECATED - Redirects to proper MVC Controller (AddToCartDBServlet)
    -- 
    -- This page no longer contains business logic (moved to AddToCartDBServlet and CartDAO)
    -- Following MVC pattern: JSP = View only, Servlet = Controller, DAO = Model
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>

<%
    // Redirect all requests to the proper MVC Controller
    // The servlet will handle all business logic and database operations
    String contextPath = request.getContextPath();
    
    // Forward all parameters to the servlet
    request.getRequestDispatcher(contextPath + "/AddToCartDB").forward(request, response);
%>
