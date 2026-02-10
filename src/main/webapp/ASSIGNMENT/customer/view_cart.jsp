<%-- 
    Author(s): Wan Jing Hng
    Date: 18 January 2026
    File: view_cart.jsp
    Description: Displays the user's booking cart with selected eldercare services; allows item removal and proceeds to checkout.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="../header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/view_cart.css">


<!-- Page hero section with title and description -->
<div class="cart-container">
    <div class="cart-hero">
        <h1>Your Booking Cart</h1>
        <p>Review your selected eldercare services before proceeding to checkout.</p>
    </div>


    <!-- Display optional success/error messages from request parameters -->
    <c:if test="${not empty param.message}">
        <div style="background: #dcfce7; color: #166534; padding: 15px; border-radius: 12px; margin-bottom: 20px; font-weight: 600;">
            ${param.message}
        </div>
    </c:if>

    <!-- Cart has items -->
    <c:choose>
        <c:when test="${not empty cart}">
            <div class="cart-grid">
                
                <div class="cart-items">
                    <h2 style="font-size: 1.1rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 20px;">
                        Selected Services (${cart.size()})
                    </h2>
                    
                    <c:forEach var="item" items="${cart}">
                        <div class="cart-item">
                        
                            <!-- Date badge showing month and day -->                       
                            <div class="date-badge">
                                <span class="m"><fmt:formatDate value="${item.serviceDate}" pattern="MMM" /></span>
                                <span class="d"><fmt:formatDate value="${item.serviceDate}" pattern="dd" /></span>
                            </div>

                            <!-- Item details: service name, caregiver/meal info, time, and rate -->
                            <div class="item-info">
                                <h3>${item.serviceName}</h3>
                                
                                <div class="detail-list">
                                    <c:choose>
                                        <c:when test="${empty item.selectedMeal}">
                                            <div><strong>Caregiver:</strong> ${not empty item.caregiverName ? item.caregiverName : "Assigning..."}</div>
                                            <div><strong>Time:</strong> ${item.startTime} (${item.durationHours} hrs)</div>
                                            <div><strong>Rate:</strong> <fmt:formatNumber value="${item.price}" type="currency"/>/hr</div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="food-highlight">
                                                <strong>Meal:</strong> ${item.selectedMeal}
                                                <c:if test="${not empty item.dietaryNotes}">
                                                    <br><small>Note: ${item.dietaryNotes}</small>
                                                </c:if>
                                            </div>
                                            <div><strong>Delivery Time:</strong> ${item.startTime}</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>


                            <!-- Item actions: display total price and remove button -->
                            <div class="item-actions">
                                <div class="item-price">
                                    <fmt:formatNumber value="${item.price * item.durationHours}" type="currency" currencySymbol="$"/>
                                </div>
                                
                                <!-- Remove item link with confirmation -->                                
                                <a href="${pageContext.request.contextPath}/RemoveCartItem?itemID=${item.cartItemId}" 
                                   class="btn-remove" 
                                   onclick="return confirm('Remove this service?');">
                                    &times; Remove
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>


                <!-- Cart summary with subtotal, tax, and total -->
                <div class="cart-summary">
                    <h2>Order Summary</h2>
                    
                    <div class="summary-row">
                        <span>Subtotal</span>
                        <span><fmt:formatNumber value="${subtotal}" type="currency" currencySymbol="$"/></span>
                    </div>
                    
                    <div class="summary-row">
                        <span>GST (${gstRate}%)</span>
                        <span><fmt:formatNumber value="${taxAmount}" type="currency" currencySymbol="$"/></span>
                    </div>
                    
                    <div class="summary-row summary-total">
                        <span>Total</span>
                        <span><fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="$"/></span>
                    </div>
                    
                    <div class="cart-actions">
                        <a href="${pageContext.request.contextPath}/Checkout" class="btn btn-primary">Proceed to Checkout</a>
                        <a href="${pageContext.request.contextPath}/ViewServices" class="btn btn-secondary">Add More Services</a>
                    </div>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <%-- Case: Cart is empty --%>
            <div style="text-align: center; padding: 100px; background: white; border-radius: 24px; border: 2px dashed #e2e8f0;">
                <div style="font-size: 4rem; margin-bottom: 20px;">🛒</div>
                <h2 style="font-size: 1.8rem; font-weight: 800;">Your cart is empty</h2>
                <p style="color: #64748b; margin-bottom: 30px;">It looks like you haven't added any services yet.</p>
                <a href="${pageContext.request.contextPath}/ViewServices" class="btn btn-primary" style="padding: 15px 40px;">Browse Services</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../footer.jsp" %>