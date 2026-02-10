<%-- 
    Author(s): Wan Jing Hng
    Date: 18 January 2026
    File: submit_feedback.jsp
    Description: Allows users to submit ratings and textual feedback for a completed care service booking.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Feedback | SilverCare Solutions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/submit_feedback.css">
</head>
<body>
    <%@ include file="../header.jsp" %>
    
    <div class="submit-wrapper">
    
        <!-- Card container for feedback form -->    
        <div class="form-card">
            <h2>Rate Your Care</h2>
            
            <!-- Summary of the service booking being reviewed -->            
            <div class="booking-summary">
                <p><strong>Service:</strong> ${booking.serviceName}</p>
                <p><strong>Booking ID:</strong> #${booking.bookingId}</p>
            </div>


            <!-- Feedback form: submits rating and comments -->  
            <form action="SubmitFeedback" method="POST">
                <input type="hidden" name="bookingId" value="${booking.bookingId}">
                <input type="hidden" name="serviceId" value="${booking.serviceId}">

                <!-- Rating dropdown -->
                <div class="form-group">
                    <label for="rating">How would you rate the service?</label>
                    <select name="rating" id="rating" required>
                        <option value="" disabled selected>Select a rating</option>
                        <option value="5">⭐⭐⭐⭐⭐ (Excellent)</option>
                        <option value="4">⭐⭐⭐⭐ (Good)</option>
                        <option value="3">⭐⭐⭐ (Average)</option>
                        <option value="2">⭐⭐ (Poor)</option>
                        <option value="1">⭐ (Terrible)</option>
                    </select>
                </div>

                <!-- Comments textarea -->
                <div class="form-group">
                    <label for="comments">Your Feedback</label>
                    <textarea name="comments" id="comments" 
                              placeholder="Please share details about your experience..." required></textarea>
                </div>


                <!-- Form action buttons -->
                <div class="btn-container">
                    <a href="MyFeedback" class="btn-cancel">Cancel</a>
                    <button type="submit" class="btn-submit">Submit Review</button>
                </div>
            </form>
        </div>
    </div>

    <%@ include file="../footer.jsp" %>
</body>
</html>