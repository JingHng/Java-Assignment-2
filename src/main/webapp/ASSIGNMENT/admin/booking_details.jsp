<%-- 
    Author(s): Wan Jing Hng
    Date: Feb 2026
    File: booking_details.jsp
    Description: Displays full booking details for admins and allows caregiver
                 assignment and schedule updates where applicable.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin - Booking Details</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/adminBookingDetails.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
    
    <%-- Prevent updates if caregiver session is already completed --%>    
    <script>
        function validateForm(isCompleted) {
            // Convert to boolean properly - check string 'true' or actual boolean true
            if (isCompleted === true || isCompleted === 'true') {
                alert('⚠️ Cannot modify completed caregiver session!');
                return false;
            }
            return true;
        }
    </script>
</head>
<body>


<%-- Navigation back to booking list --%>
<div class="booking-container">
    <a href="AdminManageBookings" class="back-link">&larr; Back to All Bookings</a>
    
    
    <%-- Page title --%>    
    <h2>Booking Assignment Details</h2>

    <%-- Success message after update --%>
    <c:if test="${not empty param.message}">
        <div class="alert alert-success">${param.message}</div>
    </c:if>
    
   	<%-- Error message display --%> 
    <c:if test="${not empty param.error}">
        <div class="error-banner">
            <span class="error-icon">⚠️</span>
            <span class="error-text">${param.error}</span>
        </div>
    </c:if>


    <%-- Customer and service summary card --%>
    <div class="info-card">
        <h3>Customer & Service Information</h3>
        
        
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Customer Name</span>
                <span class="info-value">${bookingDetails.customerName}</span>
            </div>
            
            
            <div class="info-item">
                <span class="info-label">Service Type</span>
                <span class="info-value">${bookingDetails.serviceName}</span>
            </div>
            
            
            <div class="info-item">
                <span class="info-label">Contact Email</span>
                <span class="info-value">${bookingDetails.customerEmail}</span>
            </div>
            
            
            <div class="info-item">
                <span class="info-label">Service Price</span>
                <span class="info-value"><strong>$<fmt:formatNumber value="${bookingDetails.servicePrice}" minFractionDigits="2"/></strong></span>
            </div>
            
            
            <div class="info-item">
                <span class="info-label">Total Amount Paid</span>
                <span class="info-value">$<fmt:formatNumber value="${bookingDetails.totalAmount}" minFractionDigits="2"/></span>
            </div>
            
            
            
            <%-- Only show Caregiver info if NOT Category 4 --%>
            <c:if test="${bookingDetails.categoryId != 4}">
                <div class="info-item">
                    <span class="info-label">Currently Assigned Caregiver</span>
                    <span class="badge-caregiver">
                        ${not empty bookingDetails.caregiverName ? bookingDetails.caregiverName : 'Unassigned'}
                    </span>
                </div>
            </c:if>
        </div>
    </div>

    <%-- CONDITIONAL SECTION: Show Schedule Management ONLY for Non-Meal Services --%>
    <c:choose>
        <c:when test="${bookingDetails.categoryId != 4}">
        
            <%-- Warning shown when caregiver session is completed --%>        
            <c:if test="${isSessionCompleted}">
                <div class="info-card" style="border-left: 4px solid #f44336; background-color: #ffebee;">
                    <h3 style="color: #c62828;">⚠️ Session Completed</h3>
                    <p style="color: #d32f2f; margin: 10px 0;">This caregiver session has been completed (checked in and out). Modifications are no longer allowed.</p>
                </div>
            </c:if>
            
            
            <c:if test="${not empty param.error}">
                <div class="error-banner">
                    <span class="error-icon">⚠️</span>
                    <span class="error-text">${param.error}</span>
                </div>
            </c:if>
            
            
            
            <%-- Schedule and caregiver management form --%>            
            <div class="action-card">
                <h3>Manage Schedule & Assignment</h3>
                <form action="AdminManageBookings" method="POST" onsubmit="return validateForm(${isSessionCompleted})">
       
                    <%-- Hidden identifiers --%>                 
                    <input type="hidden" name="action" value="updateDetails">
                    <input type="hidden" name="bookingId" value="${bookingDetails.bookingId}">
                    <input type="hidden" name="detailId" value="${bookingDetails.detailId}">
           
                    <%-- Display-only service duration --%>
                    <div class="form-group">
                        <label>Service Duration (Hours)</label>
                        <input type="text" value="${bookingDetails.durationHours} Hours" class="form-control" readonly>
                    </div>

                    <%-- Caregiver assignment dropdown --%>
                    <div class="form-group">
                        <label for="caregiverId">Assign/Change Caregiver</label>
                        <select name="caregiverId" id="caregiverId" class="form-control" required ${isSessionCompleted ? 'disabled' : ''}>
                            <option value="">-- Select Qualified Caregiver --</option>
                            <c:forEach var="cg" items="${caregiverList}">
                                <option value="${cg.caregiverId}" ${bookingDetails.caregiverId == cg.caregiverId ? 'selected' : ''}>
                                    ${cg.name} (${cg.specialty})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <%-- Service date and start time --%>
                    <div class="info-grid">
                        <div class="form-group">
                            <label for="serviceDate">Service Date</label>
                            <input type="date" name="serviceDate" id="serviceDate" value="${bookingDetails.serviceDate}" class="form-control" required ${isSessionCompleted ? 'disabled' : ''}>
                        </div>
                        
                        
                        <div class="form-group">
                            <label for="startTime">Start Time</label>
                            <input type="time" name="startTime" id="startTime" value="${bookingDetails.startTime}" class="form-control" required ${isSessionCompleted ? 'disabled' : ''}>
                        </div>
                    </div>

                    <%-- Submit button disabled if session is completed --%>
                    <button type="submit" class="btn-update" ${isSessionCompleted ? 'disabled style="opacity: 0.5; cursor: not-allowed;"' : ''}>Update Booking Details</button>
                </form>
            </div>
        </c:when>


        <%-- SECTION FOR CATEGORY 4: MEAL DELIVERY --%>
        <c:otherwise>
            <div class="info-card" style="border-top: 4px solid #7CB342;">
                <h3>Meal Delivery Logistics</h3>
                <div class="info-grid">
                    <div class="info-item">
                        <span class="info-label">Selected Meal</span>
                        <span class="info-value" style="color: #2e7d32; font-weight: bold;">🍴 ${bookingDetails.selectedMeal}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Delivery Date</span>
                        <span class="info-value">${bookingDetails.serviceDate}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Expected Time</span>
                        <span class="info-value">${bookingDetails.startTime}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Current Status</span>
                        <span class="badge-status">${bookingDetails.deliveryStatus}</span>
                    </div>
                </div>

                <%-- Explanation for why caregiver assignment is not shown --%>                
                <div style="margin-top: 20px; padding: 15px; background: #f1f5f9; border-radius: 8px; font-size: 0.9rem; color: #64748b;">
                    <strong>Note:</strong> Caregiver assignment is not applicable for meal deliveries. Please use the 
                    <a href="AdminManageBookings" style="color: #7CB342; font-weight: 600;">Main Bookings Dashboard</a> to update delivery status.
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../footer_admin.jsp" %>

</body>
</html>