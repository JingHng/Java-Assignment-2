<%-- 
    Author(s): Wan Jing Hng
    Date: Feb 2026
    File: edit_bookings_profile.jsp
    Description: Allows users to view and edit their booking details, reschedule caregiver sessions, or view meal order restrictions.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Booking - SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/edit_bookings_profile.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <!-- Page hero section -->
    <div class="edit-hero">
        <div class="hero-content">
            <a href="${pageContext.request.contextPath}/ManageBookings" class="btn-back">← Back to My Bookings</a>
            <h1>Booking Details</h1>
            <p class="ref-text">Order Reference: <span>#${booking.bookingId}</span></p>
        </div>
    </div>


    <!-- Main edit container -->
    <div class="edit-container">
        <%-- Message Alerts --%>
        <c:if test="${not empty param.message}">
            <div class="alert ${param.message.contains('success') ? 'alert-success' : 'alert-error'}">
                ${param.message}
            </div>
        </c:if>


        <%-- Left Column: Current Booking Summary --%>
        <div class="info-grid">
            <%-- Left Column: Summary Card --%>
            <div class="card current-booking-card">
                <div class="card-header">
                    <span class="icon">📋</span>
                    <h3>Appointment Summary</h3>
                </div>
                
                
                <div class="summary-list">
                    <div class="summary-item">
                        <label>Service Type</label>
                        <span class="value">${booking.serviceName}</span>
                    </div>


                    <%-- Show meal info if meal order, else caregiver info --%>
                    <c:choose>
                        <c:when test="${booking.categoryId == 4}">
                            <div class="summary-item">
                                <label>Meal Selected</label>
                                <span class="value highlight">${not empty booking.selectedMeal ? booking.selectedMeal : 'Standard Meal'}</span>
                            </div>
                            <div class="summary-item">
                                <label>Delivery Status</label>
                                <span class="status-badge status-${booking.deliveryStatus.toLowerCase().replace(' ', '-')}">
                                    ${booking.deliveryStatus}
                                </span>
                            </div>
                        </c:when>
                        
                        
                   		 <%-- Show Caregiver --%>                        
                        <c:otherwise>
                            <div class="summary-item">
                                <label>Assigned Caregiver</label>
                                <span class="value highlight">${not empty booking.caregiverName ? booking.caregiverName : 'Assigning Soon...'}</span>
                            </div>
                            <div class="summary-item">
                                <label>Current Date</label>
                                <span class="value">${booking.date}</span>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>



            <%-- Right Column: Form Section --%>
            <div class="action-section">
                <c:choose>
                    <c:when test="${booking.categoryId != 4}">
                        <div class="card form-card">
                            <div class="card-header">
                                <span class="icon">📅</span>
                                <h3>Modify Schedule</h3>
                            </div>                                                       
                            	<form action="EditBooking" method="get" class="edit-form">
                                <input type="hidden" name="detailID" value="${booking.detailID}">
                                <input type="hidden" name="bookingID" value="${booking.bookingId}">                                
                                
                                
                                <%-- Caregiver selection dropdown --%>                                
                                <div class="form-group">
                            <label>Preferred Caregiver</label>                                   
						    <select name="caregiverID_select" class="form-control" required>
						     <%-- Disable the first drop down called ---Select--- --%>                            
						    <option value="" disabled ${empty param.caregiverID_select && empty booking.caregiverId ? 'selected' : ''}>-- Select --</option>
						    
        					<%-- Iterate over all qualified caregivers provided by the servlet --%>
						    <c:forEach var="cg" items="${qualifiedCaregivers}">
						    
						        <%-- Store current caregiver's ID in a variable --%>
						        <c:set var="thisCgId" value="${cg['id']}" />						        
						        <c:set var="isSelected" value="false" />
						        
						        
   						        <%-- Decide if this caregiver should be pre-selected --%>
						        <c:choose>
						            <%-- Case 1: User has already selected a caregiver in this session (URL param) --%>						
						            <c:when test="${not empty param.caregiverID_select}">
						                <c:if test="${param.caregiverID_select == thisCgId}">
						                     <%-- Mark this caregiver as selected --%>						                
						                    <c:set var="isSelected" value="true" />
						                </c:if>
						            </c:when>
						            <%-- Otherwise, use the ID from the existing booking --%>
						            <c:otherwise>
						                <c:if test="${booking.caregiverId == thisCgId}">
						                    <c:set var="isSelected" value="true" />
						                </c:if>
						            </c:otherwise>
						        </c:choose>
						
								<%-- Show each caregiver as a dropdown option and pre-select the one already chosen --%>
						        <option value="${thisCgId}" ${isSelected ? 'selected="selected"' : ''}>
						            ${cg.name} (${cg.specialty})
						        </option>
						    </c:forEach>
						</select>
						</div>


								<%-- Form Group to allow rescheuling of dates --%>
                                <div class="form-group">
                                    <label>Reschedule Date</label>
                                    <input type="date" name="booking_date_select" id="reschedule_date" class="form-control" required
                                        value="${not empty param.booking_date_select ? param.booking_date_select : booking.date}">
                              
                              
                                </div>
								<%-- Button to check caregiver availability for the selected date --%>                                
                                <button type="submit" class="btn btn-check">Check Availability</button>
                            </form>
                        </div>
                    </c:when>
           
                    
                    <%-- For meal orders: show notice instead of edit form --%>                    
                    <c:otherwise>
                        <div class="card meal-notice-card">
                            <div class="notice-content">
                                <div class="notice-icon">🚚</div>
                                <h3>Meal Order Support</h3>
                                <p>Confirmed meal orders cannot be edited online.</p>
                                <a href="mailto:support@silvercare.com" class="btn-support">Contact Support</a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>


        <%-- Time Slots Selection --%>
        <c:if test="${not empty param.booking_date_select && booking.categoryId != 4}">

   			<%-- Show time slots for non-meal bookings after date is selected --%>        
            <div class="card time-slots-section">
                <div class="card-header">
                    <h3>Select New Time Slot</h3>
                </div>
                
   		        <%-- Generate buttons for each hour from 8am to 8pm --%>                
                <div class="time-slots-grid">
                    <c:forEach var="hour" begin="8" end="20">
                        <button type="button" class="time-slot-btn" data-time="${hour}:00">${hour}:00</button>
                    </c:forEach>
                </div>
                
   			    <%-- Form to submit the selected time slot and update booking --%>
                <form action="UpdateBooking" method="post" id="final_update_form" class="update-form">
                    <input type="hidden" name="detailID" value="${booking.detailID}">
                    <input type="hidden" name="bookingID" value="${booking.bookingId}">
                    
                    <%-- Pass the selected caregiver back to the Update Servlet --%>
                    <input type="hidden" name="caregiverID" 
                           value="${not empty param.caregiverID_select ? param.caregiverID_select : booking.caregiverId}">
                    
		            <%-- Pass the selected date, duration, and start time --%>                    
                    <input type="hidden" name="booking_date" value="${param.booking_date_select}">
                    <input type="hidden" name="duration" value="${booking.duration}">
                    <input type="hidden" name="start_time" id="final_start_time">
                
		            <%-- Submit button, initially disabled until a time slot is selected --%>                    
                    <button type="submit" class="btn btn-update" id="final_update_btn" disabled>Select a Time Slot Above</button>
                </form>
            </div>
        </c:if>
    </div>

    <%@ include file="../footer.jsp" %>
    
    <script>
    	<%-- Enable time slot selection and update the form dynamically --%>
        document.querySelectorAll('.time-slot-btn').forEach(btn => {
            btn.addEventListener('click', function () {
                document.querySelectorAll('.time-slot-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
                document.getElementById('final_start_time').value = this.dataset.time;
                document.getElementById('final_update_btn').disabled = false;
                document.getElementById('final_update_btn').innerText = 'Confirm Update to ' + this.dataset.time;
            });
        });
        
        <%-- Set minimum selectable date to tomorrow to prevent past date selection --%>        
        const dateInput = document.getElementById('reschedule_date');
        if(dateInput) {
            const tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            dateInput.setAttribute('min', tomorrow.toISOString().split('T')[0]);
        }
    </script>
</body>
</html>