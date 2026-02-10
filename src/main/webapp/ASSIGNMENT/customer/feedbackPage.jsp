<%-- 
    Author(s): Wan Jing Hng
    Date: Feb 2026
    File: feedbackPage.jsp
    Description: Displays all feedback submitted by the user along with any official admin responses.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>My Feedback History - SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/feedbackPage.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <!-- Container for feedback history section -->
    <div class="feedback-container">
        <div class="section-header">
            <h2 class="section-title">My Feedback History</h2>
            <p>You can view all the reviews you've submitted for our services here.</p>
        </div>


        <!-- Grid layout for feedback cards -->
        <div class="feedback-grid">
            <c:choose>
                <c:when test="${not empty submittedFeedback}">
                    <c:forEach var="fb" items="${submittedFeedback}">
                        <div class="feedback-card">
                            <div class="feedback-card-header">
                                <h3 class="service-name">${fb.serviceName}</h3>
                                <span class="feedback-date">
                                    <fmt:formatDate value="${fb.feedbackDate}" pattern="dd MMM yyyy" />
                                </span>
                            </div>
                            
                            
                            
                            <!-- Star rating display -->                            
                            <div class="rating-display">
                                <span class="stars">
                                    <c:forEach begin="1" end="5" varStatus="loop">
                                        <span class="${loop.index <= fb.rating ? 'star-filled' : 'star-empty'}">
                                            ${loop.index <= fb.rating ? '★' : '☆'}
                                        </span>
                                    </c:forEach>
                                </span>
                                <span class="rating-num">(${fb.rating}/5)</span>
                            </div>
                            
                            
                            <!-- User comments -->                            
                            <p class="feedback-content">"${fb.comments}"</p>
                            
                           <!-- Optional admin response -->                            
                          <c:if test="${not empty fb.adminReply}">
                        <div class="admin-reply">
                            <div class="reply-header">
                                <span class="reply-icon">💬</span> 
                                <strong>SilverCare Official Response:</strong>
                            </div>
                            <p class="reply-text">${fb.adminReply}</p>
                        </div>
                    </c:if>
                        </div>
                    </c:forEach>
                </c:when>
                
                <c:otherwise>
                    <%-- Empty state when no feedback is submitted --%>
                    <div class="empty-state">
                        <p>You haven't submitted any reviews yet.</p>
                        <a href="ViewHistory" class="btn-review" style="display:inline-block; margin-top:10px; text-decoration:none;">View History to Leave a Review</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <%@ include file="../footer.jsp" %>
</body>
</html>