<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.silvercare.model.Feedback" %>

<%-- 
    Author(s): Wan Jing Hng
    Date: Updated Feb 2026
    File: manage_feedback.jsp
    Description: Full management interface for reviewing and replying to customer feedback.
--%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Feedback - SilverCare Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/adminManageFeedback.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>

    <%
        List<Feedback> feedbackList = (List<Feedback>) request.getAttribute("feedbackList");
        String currentFilter = (String) request.getAttribute("currentFilter");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
    %>

    <main class="admin-container">
        <header class="page-header">
            <div class="header-left">
                <h1>Customer Feedback</h1>
                <p>Manage service experiences and client responses.</p>
            </div>
            
            <form action="AdminManageFeedback" method="GET" class="filter-controls">
                <label for="minRating">Filter by Rating:</label>
                <select name="minRating" id="minRating" onchange="this.form.submit()" class="filter-select">
                    <option value="">All Ratings</option>
                    <option value="5" <%="5".equals(currentFilter) ? "selected" : "" %>>5 Stars Only</option>
                    <option value="4" <%="4".equals(currentFilter) ? "selected" : "" %>>4+ Stars</option>
                    <option value="3" <%="3".equals(currentFilter) ? "selected" : "" %>>3+ Stars</option>
                </select>
            </form>
        </header>

        <% if (message != null) { %>
            <div class="alert alert-success"><%= message %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="alert alert-error"><%= error %></div>
        <% } %>

        <div class="feedback-grid">
            <% 
                if (feedbackList != null && !feedbackList.isEmpty()) { 
                    for (Feedback f : feedbackList) {
            %>
                <div class="feedback-card">
                    <div class="card-header">
                        <div class="stars">
                            <% for(int i=1; i<=5; i++) { %>
                                <span class="<%= (i <= f.getRating()) ? "star-filled" : "star-empty" %>">★</span>
                            <% } %>
                        </div>
                        <span class="date-text"><%= f.getFeedbackDate() != null ? df.format(f.getFeedbackDate()) : "" %></span>
                    </div>

                    <div class="card-content">
                        <p class="comment-bubble">"<%= f.getComments() != null ? f.getComments() : "No comment provided." %>"</p>
                        <div class="client-info">
                            <strong>Client:</strong> <%= f.getCustomerName() %><br>
                            <strong>Service:</strong> <%= f.getServiceName() %>
                        </div>
                    </div>

                    <div class="reply-container">
                        <% if (f.getAdminReply() != null && !f.getAdminReply().trim().isEmpty()) { %>
                            <div class="previous-reply">
                                <span class="reply-tag">Your Reply:</span>
                                <p><%= f.getAdminReply() %></p>
                            </div>
                        <% } %>

                        <form action="AdminManageFeedback" method="POST" class="reply-form">
                            <input type="hidden" name="action" value="reply">
                            <input type="hidden" name="feedbackId" value="<%= f.getFeedbackId() %>">
                            <textarea name="adminReply" placeholder="Enter your response..." required></textarea>
                            <button type="submit" class="btn-reply">Send Response</button>
                        </form>
                    </div>

                    <div class="card-footer">
                        <small class="id-badge">ID #<%= f.getBookingId() %></small>
                        <form action="AdminManageFeedback" method="POST" onsubmit="return confirm('Delete this feedback?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="feedbackId" value="<%= f.getFeedbackId() %>">
                            <button type="submit" class="btn-delete">Remove</button>
                        </form>
                    </div>
                </div>
            <% 
                    } 
                } else { 
            %>
                <div class="empty-state">
                    <p>No feedback found. Try changing the rating filter.</p>
                </div>
            <% } %>
        </div>
    </main>

    <%@ include file="../footer_admin.jsp" %>
</body>
</html>