<%-- 
    Author: Wan Jing Hng
    Date: 1 Feb 2026
    File: caregiver_dashboard.jsp
    Description: Displays a caregiver's assigned visits with check-in/check-out actions. Shows live status and contact info.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/caregiver_dashboard.css">
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&display=swap" rel="stylesheet">


<%-- Dashboard header showing caregiver name and active shift badge --%>
<div class="container">
    <header class="dashboard-header">
        <div class="header-content">
            <h2>My Care Visits</h2>
            <p class="logged-in-as">Logged in as: <strong>${firstName}</strong></p>
        </div>
               
        <div class="header-badge">
            <span class="pulse-icon">●</span> Active Shift
        </div>
    </header>
    
    
    
    <%-- Main list of assigned visits --%>
    <div class="visit-list">
        <c:choose>
            <c:when test="${not empty assignedVisits}">
                <c:forEach var="visit" items="${assignedVisits}">
                    <div class="visit-card">

                        <%-- Service date block --%>                        
                        <div class="visit-time">
                            <span class="label">Service Date</span>
                            <span class="date">${visit.bookingDate}</span>
                        </div>

         			               <%-- Visit details including service, client info, and status --%>                       		
                       				<div class="visit-info">
							    <h3>${visit.serviceName}</h3>
							    <div class="client-meta">
							        <p class="client-name">👤 ${visit.customerName}</p>
							        
							        <c:if test="${not empty visit.customerPhone}">
							            <p class="client-phone" style="margin-top: 5px; font-size: 0.9rem;">
							                📞 <a href="tel:${visit.customerPhone}" style="color: #7CB342; font-weight: 700; text-decoration: none;">${visit.customerPhone}</a>
							            </p>
							        </c:if>
							    </div>
							    
  	                            <%-- Live status badge, green for 'In Progress', blue otherwise --%>							    
							    <span class="status-badge ${visit.liveStatus == 'In Progress' ? 'bg-success' : 'bg-info'}">
							        ${visit.liveStatus}
							    </span>
							</div>

                        <%-- Actions: Check-in / Check-out buttons depending on liveStatus --%>
                        <div class="visit-actions">
                            <form action="${pageContext.request.contextPath}/CaregiverAction" method="POST">
                                <input type="hidden" name="bookingId" value="${visit.bookingId}">
                                
                                
                                <%-- If scheduled or confirmed, allow check-in --%>                                
                                <c:choose>
                                    <c:when test="${visit.liveStatus == 'Scheduled' || visit.liveStatus == 'Confirmed'}">
                                        <input type="hidden" name="action" value="checkin">
                                        <button type="submit" class="btn-action btn-checkin">Start Session</button>
                                    </c:when>
                                    
                                    <%-- If in progress, allow check-out --%>                                    
                                    <c:when test="${visit.liveStatus == 'In Progress'}">
                                        <input type="hidden" name="action" value="checkout">
                                        <button type="submit" class="btn-action btn-checkout">End Session</button>
                                    </c:when>
                                  
                                    <%-- Otherwise, show disabled button with status --%>                                    
                                    <c:otherwise>
                                        <button class="btn-action btn-disabled" disabled>${visit.liveStatus}</button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            
            <%-- If no visits are assigned --%>            
            <c:otherwise>
                <div class="empty-state">
                    <p>No visits assigned to you yet.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>