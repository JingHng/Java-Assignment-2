<%-- 
    Author(s): Wan Jing Hng 
    Date: 18 January 2026
    File: manage_bookings_profile.jsp 
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Bookings - SilverCare Solutions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_bookings_profile.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>
    <%@ include file="../header.jsp" %>

    
    <!-- Hero section with page title and description -->    
    <div class="bookings-hero">
        <h1>Manage Your Bookings</h1>
        <p>View, edit, or cancel your active service bookings</p>
    </div>


    <!-- Back button to Dashboard -->
    <div class="bookings-container">
        <div class="page-actions">
            <a href="${pageContext.request.contextPath}/Dashboard" class="btn-back">← Back to Dashboard</a>
        </div>

    
        <!-- Display success or error messages from servlet -->    
        <c:if test="${not empty param.message}">
            <c:choose>
                <c:when test="${param.message.contains('Cannot') || param.message.contains('Invalid') || param.message.contains('not found') || param.message.contains('Error')}">
                    <div class="error-message">
                        <span class="error-icon">⚠️</span>
                        <span class="error-text">${param.message}</span>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="success-message">
                        <span class="success-icon">✓</span>
                        <span class="success-text">${param.message}</span>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>


        <!-- Section header for active bookings table -->
        <div class="section-header">
            <h2 class="section-title">Active Bookings</h2>
        </div>


        <c:choose>
            <%-- Checked against 'bookings' to match Servlet setAttribute --%>
            <%-- Check if there are any active bookings --%>            
            <c:when test="${empty bookings}">
                <div class="empty-state">
                    <div class="empty-icon">📅</div>
                    <h3>No Active Bookings</h3>
                    <p>You don't have any active bookings at the moment.</p>
                    <a href="${pageContext.request.contextPath}/ViewServices" class="btn-primary">Browse Services</a>
                </div>
            </c:when>
            <c:otherwise>
            
                <!-- Table displaying active bookings -->            
                <div class="bookings-table-container">
                    <table class="bookings-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Service</th>
                                <th>Caregiver</th>
                                <th>Date</th>
                                <th>Time</th>
                                <th>Total</th>
                                <th>Status</th>
                                <th style="text-align: center;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        
                        
                            <%-- Iterating through 'bookings' --%>
                            <c:forEach var="b" items="${bookings}">
                                <tr>
                                    <!-- Booking ID -->
                                    <td><span class="booking-id">#${b.id}</span></td>
                                    <td><span class="service-name">${b.service}</span></td>
                                    
                                    <!-- Caregiver assignment logic -->
                                    <td>
                                        <c:choose>
                                            <c:when test="${b.categoryId == 4}">
                                                <span class="text-muted">None Needed (Meal)</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${not empty b.caregiver ? b.caregiver : '<span class="assigning-tag">Assigning Soon...</span>'}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <!-- Booking date -->                                    
                                    <td><fmt:formatDate value="${b.date}" pattern="dd MMM yyyy" /></td>
                                    
                                    <!-- Booking time -->                                    
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty b.time}">
                                                ${b.time.toString().substring(0, 5)}
                                            </c:when>
                                            <c:otherwise>N/A</c:otherwise>
                                        </c:choose>
                                    </td>
                                    
                                    <!-- Booking Amount -->                                    
                                    <td class="amount-cell">$<fmt:formatNumber value="${b.amount}" minFractionDigits="2" /></td>
                                    <td><span class="status-badge status-${b.status.toLowerCase()}">${b.status}</span></td>
                                    <td>
                                    
                                    
                                    
                                   		<!-- Actions (Edit/View and Cancel) -->                                    
                                        <div class="action-buttons">
                                            <%-- Disable edit for completed caregiver sessions (categoryId != 4 and liveStatus = Completed) --%>
                                            <c:choose>
                                                <c:when test="${b.categoryId != 4 && b.liveStatus eq 'Completed'}">
                                                    <span class="btn-action btn-disabled" style="background-color: #ccc; cursor: not-allowed;" title="Session completed - cannot edit">
                                                        ✓ Completed
                                                    </span>
                                                </c:when>
                                                
                                                <c:otherwise>
                                                    <%-- Edit/View booking link --%>
                                                    <a href="EditBooking?detailID=${b.detailID}" class="btn-action btn-edit">View/Edit</a>
                                                </c:otherwise>
                                           		 </c:choose>
                                            
                                            
                                               <!-- Cancel booking link -->                                          
                                          	  <a href="${pageContext.request.contextPath}/CancelBooking?bookingID=${b.id}" 
                                               class="btn-action btn-cancel" 
                                               onclick="return confirm('Cancel this booking?')">Cancel</a>
                                       		 </div>
                                    		</td>
                                    
                                    
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="../footer.jsp" %>
</body>
</html>