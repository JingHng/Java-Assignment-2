<%-- 
    -- Author(s): Wan Jing Hng 
    -- Date: 22/1/2026 
    -- File: header.jsp 
    -- Description: Modern header with MVC routing and fixed syntax.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="com.silvercare.model.CartItem" %>

<% 
    /* Session Status Logic */
    Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");
    String userFirstName = (String) session.getAttribute("firstName"); 
    String userRole = (String) session.getAttribute("userRole"); 
    
    boolean isUserLoggedIn = (isLoggedIn != null && isLoggedIn);
    boolean isAdmin = (userRole != null && "admin".equalsIgnoreCase(userRole)); 
    boolean isCustomer = (userRole != null && "customer".equalsIgnoreCase(userRole)); 
    
    /* Default name if missing */ 
    if (userFirstName == null || userFirstName.isEmpty()) { 
        userFirstName = "User"; 
    } 
    
    /* Calculate cart count for customers */ 
    int cartCount = 0; 
    if (isCustomer) { 
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart != null) {
            cartCount = cart.size();
        }
    }

    /* Path Helper */
    String ctx = request.getContextPath();
    String appPath = ctx + "/ASSIGNMENT";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Added versioning to CSS  --%>
    <link rel="stylesheet" href="<%= appPath %>/Css/header.css?v=4">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">
</head>
<body>

<header class="main-header">
    <div class="header-container">
        <div class="logo">
            <%-- Redirect based on role: Admin to Dashboard, Customer to Index --%>
            <a href="<%= isAdmin ? ctx + "/AdminDashboard" : appPath + "/index.jsp" %>">SilverCare</a>
        </div>

        <nav class="nav-center">
            <% if (isAdmin) { %>
                <%-- ADMIN MVC NAVIGATION --%>
                <a href="<%= ctx %>/AdminDashboard">Dashboard</a>
                <a href="<%= ctx %>/AdminManageServices">Services</a>
                <a href="<%= ctx %>/AdminManageCaregivers">Caregivers</a>
                <a href="<%= ctx %>/AdminManageCustomers">Customers</a>
                <a href="<%= ctx %>/AdminManageBookings">Bookings</a>
            <% } else { %>
                <%-- CUSTOMER NAVIGATION --%>
                <a href="<%= appPath %>/index.jsp">Home</a>
                <a href="<%= ctx %>/ViewServices">Services</a>
                <a href="<%= ctx %>/ViewCaregivers">Caregivers</a>
                <a href="<%= ctx %>/AllReviews">Our Reviews</a>
            <% } %>
        </nav>

        <div class="nav-right">
            <% if (isUserLoggedIn) { %>
                <div class="user-menu">
                    <% if (isCustomer) { %>
                        <a href="<%= ctx %>/ViewCart" class="cart-icon-link">
                            🛒 <% if (cartCount > 0) { %>
                                <span class="cart-badge"><%= cartCount %></span>
                            <% } %>
                        </a>
                    <% } %>

                    <div class="pages-dropdown">
                        <div class="user-avatar">
                            <%= userFirstName.substring(0,1).toUpperCase() %>
                        </div>
                        <div class="dropdown-menu">
                            <% if (isAdmin) { %>
                                <a href="<%= ctx %>/AdminDashboard" class="dropdown-item">📊 Admin Panel</a>
                            <% } else { %>
                                <a href="<%= ctx %>/Dashboard" class="dropdown-item">👤 Profile</a>
                            <% } %>
                            <a href="<%= ctx %>/LogoutServlet" class="dropdown-item" style="color: #e74c3c; font-weight: bold;">🚪 Logout</a>
                        </div>
                    </div>
                </div>
            <% } else { %>
                <a href="<%= appPath %>/login.jsp" class="btn btn-secondary">Login</a>
                <a href="<%= appPath %>/register.jsp" class="btn btn-primary">Register</a>
            <% } %>
        </div>
    </div>
</header>