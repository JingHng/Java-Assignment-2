<%-- 
    Author(s): Wan Jing Hng
    Date: 18 January 2026
    File: dashboard.jsp
    Description: Displays the customers main dashboard, consist of everything related to services, manage profile etc 
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Dashboard | SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/dashboard.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Dashboard welcome section --%>
    <div class="dashboard-wrapper">
        <header class="dashboard-header">
            <div style="display: flex; justify-content: space-between; align-items: flex-end;">
                <div>
                     <%-- Greeting the user with their first name --%>
                    <h1 style="font-size: 2.2rem; font-weight: 800; color: #0f172a;">Welcome, ${sessionScope.user.firstName}</h1>
                    <p style="color: #64748b; margin-top: 5px;">You have <strong style="color: #7CB342;">${bookingsList.size()}</strong> upcoming sessions.</p>
                </div>
                
                
                <%-- Live monitoring indicator --%>                
                <div style="text-align: right;">
                    <span id="sync-indicator" style="font-size: 0.8rem; font-weight: 700; color: #10b981;">● LIVE MONITORING</span>
                    <p id="last-updated" style="font-size: 0.75rem; color: #94a3b8; margin-top: 5px;">Last checked: Just now</p>
                </div>
            </div>
        </header>

        <%-- Layout: sidebar + main content --%>
        <div class="main-layout">
            <aside class="side-nav">
                <div class="nav-group">
                    <span class="nav-label">Management</span>
                    <a href="${pageContext.request.contextPath}/Dashboard" class="nav-link">Dashboard</a>
                    <a href="${pageContext.request.contextPath}/EditProfile" class="nav-link">Account Settings</a>
                    <a href="${pageContext.request.contextPath}/ViewCart" class="nav-link">My Service Cart</a>
                    <a href="${pageContext.request.contextPath}/ViewHistory" class="nav-link">Care History</a>
                    <a href="${pageContext.request.contextPath}/MyFeedback" class="nav-link">Review History</a>
                    <a href="${pageContext.request.contextPath}/ManageBookings" class="nav-link">Service Management</a>
                </div>
            </aside>


            <%-- Main dashboard content area --%>
            <main class="content-area">
                <div class="booking-container">
                    <c:choose>
                        <c:when test="${empty bookingsList}">
                            <div style="text-align: center; padding: 60px; background: white; border-radius: 20px; border: 2px dashed #e2e8f0;">
                                <p style="color: #64748b;">No upcoming schedules found.</p>
                                <a href="${pageContext.request.contextPath}/ViewServices" style="color: #7CB342; font-weight: 700; text-decoration: none; display: block; margin-top: 10px;">Book a Service Now</a>
                            </div>
                        </c:when>
                        
                        <%-- Upcoming bookings exist: iterate over them --%>                        
                        <c:otherwise>
                            <c:forEach var="booking" items="${bookingsList}">
                                <div class="schedule-card ${booking.categoryId == 4 ? 'meal-card' : 'care-card'}">
                                   
                                    <%-- Date block: show month and day --%>                                    
                                    <div class="date-block">
                                        <span class="m"><fmt:formatDate value="${booking.serviceDate}" pattern="MMM" /></span>
                                        <span class="d"><fmt:formatDate value="${booking.serviceDate}" pattern="dd" /></span>
                                    </div>

                                    <%-- Booking details block --%>                         
                                    <div class="details-block">
                                        <h3>${booking.serviceName}</h3>
                                        <span class="badge ${booking.status.toLowerCase()}">${booking.status}</span>
   
                                        <%-- Time and caregiver info row --%>                                        
                                        <div class="bottom-row">
                                            <span><strong>Time:</strong> ${booking.startTime}</span>
                                            <c:if test="${booking.categoryId != 4}">
                                                <span><strong>Caregiver:</strong> ${not empty booking.caregiverName ? booking.caregiverName : 'Assigning Soon...'}</span>
                                            </c:if>
                                        </div>

                                        <%-- Meal-specific info (only show if its food services category)--%>
                                        <c:if test="${booking.categoryId == 4}">
                                            <div class="meal-info-box">
                                                🍴 <strong>Meal:</strong> ${booking.selectedMeal} | 
                                                <strong>Status:</strong> <span id="delivery-text-${booking.bookingId}">${booking.deliveryStatus}</span>
                                            </div>
                                        </c:if>

                                        <%-- Care session live status updates --%>
                                        <c:if test="${booking.categoryId != 4}">
                                            <div style="margin-top: 10px; font-size: 0.85rem; font-weight: 600; color: #0284c7;">
                                                <span id="live-status-text-${booking.bookingId}">
                                                    <c:choose>
                                                        <c:when test="${booking.liveStatus eq 'In Progress'}">⚡ Caregiver has started session</c:when>
                                                        <c:when test="${booking.liveStatus eq 'Completed'}">✅ Session Completed</c:when>
                                                        <c:otherwise>⏳ Caregiver assigned & ready</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                            
                                            
                                            <%-- Check-in / Check-out timestamps --%>
                                            <div style="font-size: 0.75rem; color: #94a3b8; margin-top: 4px;">
                                                <c:if test="${not empty booking.checkIn}">
                                                    In: <fmt:formatDate value="${booking.checkIn}" pattern="HH:mm" /> 
                                                </c:if>
                                                <c:if test="${not empty booking.checkOut}">
                                                    | Out: <fmt:formatDate value="${booking.checkOut}" pattern="HH:mm" />
                                                </c:if>
                                            </div>
                                        </c:if>
                                    </div>
                                    
                                    
                                    <%-- Action block: confirm meal received or show status --%>
                                    <div class="action-block">                                        
                                        <div id="action-container-${booking.bookingId}" style="margin-top: 10px;">
                                            <c:if test="${booking.categoryId == 4 && booking.deliveryStatus eq 'Dispatched'}">
                                                <button type="button" class="btn-confirm" onclick="confirmReceipt('${booking.bookingId}')">Confirm Received</button>
                                            </c:if>
                                            <c:if test="${booking.deliveryStatus eq 'Received by Customer'}">
                                                <span class="received-label">✅ Received</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </main>
        </div>
    </div>

    <script>
    <%-- JavaScript: confirm meal receipt via POST request --%>
    function confirmReceipt(bookingId) {
        if (!confirm('Confirm you have received your order?')) return;
        const params = new URLSearchParams();
        params.append('bookingId', bookingId);

        
        fetch('${pageContext.request.contextPath}/ConfirmReceipt', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        }).then(res => {
        	
            <%-- Update the UI to show received status --%>
            if (res.ok) {
                const container = document.getElementById('action-container-' + bookingId);
                const statusText = document.getElementById('delivery-text-' + bookingId);
                
                
                if (container) container.innerHTML = '<span class="received-label">✅ Received</span>';
                if (statusText) statusText.innerText = 'Customer Received';
            }
        });
    }
    
    
    <%-- JavaScript: live status updater for caregiver sessions and meal delivery --%>
    function updateLiveStatus() {
        fetch('${pageContext.request.contextPath}/Dashboard?ajax=true')
            .then(res => res.json())
            .then(data => {
                data.forEach(item => {
                    // Update Delivery Status for Meals
                    const dText = document.getElementById('delivery-text-' + item.bookingId);
                    if (dText) dText.innerText = item.deliveryStatus;

                    // Update Live Status for Caregivers
                    const lText = document.getElementById('live-status-text-' + item.bookingId);
                    if (lText) {
                        if (item.liveStatus === 'In Progress') {
                            lText.innerText = '⚡ Caregiver has started session';
                        } else if (item.liveStatus === 'Completed') {
                            lText.innerText = '✅ Session Completed';
                        } else {
                            lText.innerText = '⏳ Caregiver assigned & ready';
                        }
                    }
                });
                document.getElementById('last-updated').innerText = 'Last checked: ' + new Date().toLocaleTimeString();
            })
            .catch(err => console.error("Update failed", err));
    }
    setInterval(updateLiveStatus, 10000);
    </script>
        <%@ include file="../footer.jsp" %>
    
</body>
</html>