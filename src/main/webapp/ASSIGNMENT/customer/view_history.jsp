<%-- 
    Author(s): Wan Jing Hng
    Date: 18 January 2026
    File: view_history.jsp
    Description: Displays past care sessions and meal deliveries; allows leaving reviews if eligible.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%-- Define today's date for comparison --%>
<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Activity History | SilverCare</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/view_history.css">
</head>
<body>
    <%@ include file="../header.jsp" %>
     
     
    <!-- Page header with title and back button -->  
    <div class="history-wrapper">
        <header class="history-header">
            <div class="welcome-meta">
                <h1>Activity History</h1>
                <p>Review your past care sessions and meal deliveries.</p>
            </div>
            <a href="Dashboard" class="btn-back">← Back to Dashboard</a>
        </header>


        <!-- Main content area -->
        <section class="content-area">
            <c:choose>
                <c:when test="${empty historyBookings}">
                    <%-- No past bookings found --%>
                    <div class="empty-state">
                        <p>No past service records found.</p>
                        <a href="${pageContext.request.contextPath}/Services" class="text-link">Book a Service</a>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <%-- Past bookings exist --%>
                    <div class="history-list">
                        <c:forEach var="booking" items="${historyBookings}">
                            <div class="history-card">
                                <div class="date-tag">
                                    <span class="m"><fmt:formatDate value="${booking.serviceDate}" pattern="MMM" /></span>
                                    <span class="d"><fmt:formatDate value="${booking.serviceDate}" pattern="dd" /></span>
                                </div>
                                
                                <!-- Booking details -->                                
                                <div class="history-details">
                                    <div class="history-top">
                                        <h3>${booking.serviceName}</h3>
                                        <c:choose>
                                            <c:when test="${booking.deliveryStatus eq 'Cancelled'}">
                                                <span class="status-badge bg-cancelled">CANCELLED</span>
                                            </c:when>
                                            <c:when test="${booking.deliveryStatus eq 'Customer Received'}">
                                                <span class="status-badge bg-received">RECEIVED</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge bg-completed">${booking.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- Metadata for the booking -->                                    
                                    <div class="history-meta">
                                        <c:choose>
                                            <c:when test="${not empty booking.selectedMeal}">
                                                <span><strong>Meal:</strong> ${booking.selectedMeal}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span><strong>Time:</strong> ${booking.startTime}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span><strong>Paid:</strong> $<fmt:formatNumber value="${booking.amount}" pattern="#,##0.00"/></span>
                                    </div>
                                </div>
 
                                <!-- Action button area -->                               
                               <div class="history-action">
							    <c:choose>
							        <%-- Simply check the boolean flag we created in the DAO --%>
							        <c:when test="${booking.canReview}">
							            <a href="SubmitFeedback?bookingId=${booking.bookingId}" class="btn-review">Leave Review</a>
							        </c:when>
							        
							        <c:when test="${booking.feedbackCount > 0}">
							            <span class="text-muted small">✓ Reviewed</span>
							        </c:when>
							        
							        <c:otherwise>
							            <span class="text-muted small">N/A</span>
							        </c:otherwise>
							    </c:choose>
							</div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
    <%@ include file="../footer.jsp" %>
</body>
</html>