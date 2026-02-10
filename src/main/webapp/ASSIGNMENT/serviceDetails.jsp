<%-- 
    Author(s): Wan Jing Hng 
    Date: Feb 2026 
    File: serviceDetails.jsp 
    Description: MVC-compliant booking with DYNAMIC Food Service options, 
                 conditional duration logic, and "Tomorrow-only" date restriction.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/serviceDetails.css">

<section class="service-detail-hero">
    <div class="breadcrumb">
        <a href="${pageContext.request.contextPath}/ViewServices">Services</a> / 
        <span>${service.serviceName}</span>
    </div>
    <h1>${service.serviceName}</h1>
</section>

        <!-- Service Details Section -->
<div class="service-detail-container">
    <div class="service-detail-grid">
    
            <!-- Service image -->
        <div class="service-image-box">
            <img src="${pageContext.request.contextPath}/ASSIGNMENT/${service.imageLocation}" alt="${service.serviceName}">
        </div>

        <!-- Service information -->
        <div class="service-content">
            <span class="category-badge">${service.categoryName}</span>
            <h2 class="service-title">${service.serviceName}</h2>
            <div class="service-price">
                <fmt:formatNumber value="${service.price}" type="currency" currencySymbol="$" />
                <span class="unit">/ session</span>
            </div>
                    <!-- Service description -->
       
            <p class="service-description">${service.description}</p>

            <div class="features-section">
                <h3>Service Highlights</h3>
                <ul class="features-list">
                    <li><span class="feature-icon">✔</span> Certified Professionals</li>
                    <li><span class="feature-icon">✔</span> Personalized Care Plans</li>
                    <li><span class="feature-icon">✔</span> Nutritionist Approved</li>
                </ul>
            </div>
        </div>
    </div>


        <!-- Bookings Section -->
    <div class="booking-section">
        <div class="booking-card">
            <h3>Schedule Your Service</h3>
            <c:choose>
                        <%-- Booking is only available to logged-in users --%>
                <c:when test="${not empty sessionScope.user}">
                    <form action="${pageContext.request.contextPath}/AddToCartDB" method="POST">
                        <input type="hidden" name="serviceID" id="serviceIdHidden" value="${service.serviceId}">
                        <input type="hidden" name="price" value="${service.price}">

                        <%-- CONDITIONAL: Caregiver Selection --%>
              <%-- Caregiver selection is NOT required for Food Services (categoryId = 4) --%>
                        
                        <c:choose>
                            <c:when test="${service.categoryId != 4}">
                                <div class="form-group">
                                    <label for="caregiverSelect">1. Choose Your Caregiver</label>
                                    <select name="caregiverID" id="caregiverSelect" required class="form-control">
                                        <option value="" disabled selected>-- Select a Professional --</option>
                                        <c:forEach var="cg" items="${caregiverList}">
                                            <option value="${cg.id}">${cg.name} (${cg.specialty})</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:when>
                            
                            
                             <%-- Food services use logistics delivery, no caregiver needed --%>
                            
                            <c:otherwise>
                                <input type="hidden" name="caregiverID" value="0">
                                <div class="form-group">
                                    <p class="meal-info-note" style="padding: 12px; background: #f0fdf4; border-left: 4px solid #7CB342; border-radius: 4px; font-size: 0.9rem;">
                                        ℹ️ <strong>Meal Delivery:</strong> Managed by our logistics partners. No caregiver assignment required.
                                    </p>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <%-- CONDITIONAL: Food Options --%>
                        <c:if test="${service.categoryId == 4}">
                            <div class="form-group special-option-box">
                                <label for="mealChoice">2. Select Your Specific Meal</label>
                                <select name="selectedFoodItem" id="mealChoice" class="form-control highlight-select" required>
                                    <option value="" disabled selected>-- Choose from today's menu --</option>
                                    <c:forEach var="opt" items="${foodOptionsList}">
                                        <option value="${opt.optionName}">${opt.optionName}</option>
                                    </c:forEach>
                                </select>
                                
                                <label for="dietaryNotes" style="margin-top:15px;">3. Dietary Restrictions / Allergies</label>
                                <textarea name="dietaryNotes" id="dietaryNotes" rows="2" 
                                          placeholder="e.g. Nut allergies, low-carb, no seafood..." 
                                          class="form-control"></textarea>
                            </div>
                        </c:if>

                        <%-- SHARED: Date & Time (Restricted to Tomorrow) --%>
                        <div class="form-row">
                            <div class="form-group">
                                <label for="bookingDate">Select Date</label>
                                <% 
                                    // Calculate Tomorrow's Date
                                    java.util.Calendar cal = java.util.Calendar.getInstance();
                                    cal.add(java.util.Calendar.DAY_OF_YEAR, 1);
                                    String dateTomorrow = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()); 
                                %>
                                <input type="date" id="bookingDate" name="booking_date" required 
                                       onchange="fetchSlots()" min="<%= dateTomorrow %>" class="form-control">
                                <small style="color: #636e72; font-size: 0.75rem;">* Advanced booking only (min 24h notice)</small>
                            </div>

                            <div class="form-group">
                                <label for="timeSlotSelect">Select Start Time</label>
                                <select name="start_time" id="timeSlotSelect" required disabled onchange="enableSubmit()" class="form-control">
                                    <option value="">-- Select Date First --</option>
                                </select>
                            </div>
                        </div>

                        <%-- CONDITIONAL: Duration Handling --%>
                        <c:choose>
                            <c:when test="${service.categoryId == 4}">
                                <input type="hidden" name="duration_hours" value="1.0">
                            </c:when>
                            <c:otherwise>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label for="duration">Duration (Hours)</label>
                                        <select name="duration_hours" id="duration" required class="form-control">
                                            <option value="1.0">1 Hour</option>
                                            <option value="2.0">2 Hours</option>
                                            <option value="3.0">3 Hours</option>
                                            <option value="4.0">4 Hours</option>
                                        </select>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <button type="submit" class="btn-book" id="submitBtn" disabled>Add to Booking Cart</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="login-prompt">
                        <p>Please <strong>Log In</strong> to book.</p>
                        <a href="${pageContext.request.contextPath}/ASSIGNMENT/login.jsp" class="btn-login">Login / Register</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<script>
	// Fetch available time slots from the server based on selected date
    function fetchSlots() {
        const date = document.getElementById('bookingDate').value;
        const sId = document.getElementById('serviceIdHidden').value;
        const select = document.getElementById('timeSlotSelect');

        if (!date) return;
        
        
        select.innerHTML = '<option>Checking...</option>';
        select.disabled = true;

        
        fetch('CheckAvailability?serviceId=' + sId + '&date=' + date)
            .then(res => res.text())
            .then(data => {
                const booked = data.split(',');
                select.innerHTML = '<option value="">-- Choose a Time --</option>';
                
                
                // Generate time slots from 8AM to 8PM
                for (let h = 8; h <= 20; h++) {
                    let time = (h < 10 ? '0' + h : h) + ':00';
                    let opt = document.createElement('option');
                    opt.value = time;
                    
                    
                    if (booked.includes(time)) {
                        opt.text = time + " (Full)";
                        opt.disabled = true;
                    } else {
                        opt.text = time + " (Available)";
                    }
                    select.add(opt);
                }
                
                select.disabled = false;
            });
    }

    // Enable submit button only when a valid time slot is selected
    function enableSubmit() {
        const timeVal = document.getElementById('timeSlotSelect').value;
        document.getElementById('submitBtn').disabled = (timeVal === "");
    }
</script>
<%@ include file="footer.jsp" %>