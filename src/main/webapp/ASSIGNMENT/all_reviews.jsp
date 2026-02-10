<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Janurary 2026
    File: all_reviews.jsp
    Description: Display all associated reviews with the website based on services
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
<head>
    <title>Customer Reviews | SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/feedbackPage.css">
</head>
<body>
    <%@ include file="header.jsp" %>

    <!-- Section header: title and description -->
    <div class="feedback-container">
        <div class="section-header">
            <h1 class="section-title">What Our Community Says</h1>
            <p>Real experiences from families using SilverCare.</p>
        </div>

        <!-- Feedback Grid to store all feedback in card format -->
        <div class="feedback-grid">
            <c:forEach var="fb" items="${allReviews}">
                <div class="feedback-card">
                    <div class="feedback-card-header">
                        <div>
                            <h3 class="service-name">${fb.serviceName}</h3>
                            <small style="color: #64748b;">Reviewed by ${fb.customerName}</small>
                        </div>
                        <span class="feedback-date">
                            <fmt:formatDate value="${fb.feedbackDate}" pattern="MMM dd, yyyy" />
                        </span>
                    </div>
                    
                    <div class="rating-display">
                        <span class="stars" style="color: #f1c40f;">
                            <c:forEach begin="1" end="5" varStatus="loop">
                                ${loop.index <= fb.rating ? '★' : '☆'}
                            </c:forEach>
                        </span>
                    </div>
                    
                    <p class="feedback-content">"${fb.comments}"</p>

        			<!-- Display Admin Reply if it's present -->
                    <c:if test="${not empty fb.adminReply}">
                        <div class="admin-reply" style="background: #f1f5f9; padding: 10px; border-radius: 8px; margin-top: 10px;">
                            <small><strong>SilverCare Response:</strong></small>
                            <p style="margin: 0; font-size: 0.9rem; font-style: italic;">${fb.adminReply}</p>
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </div>
    </div>

    <%@ include file="footer.jsp" %>
</body>
</html>