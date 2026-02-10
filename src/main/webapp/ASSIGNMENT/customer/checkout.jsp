<%-- 
    Author(s): Wan Jing Hng
    Date: 18 January 2026
    File: checkout.jsp
    Description: Allow Users to checkout items that they have added to cart
--%>



<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/checkout.css">

<div class="checkout-container">
    <h2 class="checkout-title">Confirm Your Booking</h2>
    
    
    <%-- Loop through all items in the cart and display each --%>
    <div style="margin-bottom: 20px;">
        <c:forEach var="item" items="${checkoutItems}">
            <div class="checkout-item">
                <div style="flex: 1;">
                    <div class="item-name">${item.serviceName}</div>
                    <div class="item-meta">${item.durationHours} hours at <fmt:formatNumber value="${item.price}" type="currency"/>/hr</div>
                </div>
                <strong><fmt:formatNumber value="${item.price * item.durationHours}" type="currency"/></strong>
            </div>
        </c:forEach>
    </div>


    <%-- Summary box: subtotal, tax, total --%>
    <div class="summary-box">
        <div class="summary-line">
            <span>Subtotal</span>
            <span><fmt:formatNumber value="${subtotal}" type="currency"/></span>
        </div>
        
        
        <div class="summary-line tax-line">
            <span>Tax (GST ${gstRate}%)</span>
            <span><fmt:formatNumber value="${taxAmount}" type="currency"/></span>
        </div>
        
        
        <div class="total-line">
            <span>Total Payable</span>
            <span class="total-amount"><fmt:formatNumber value="${totalAmount}" type="currency"/></span>
        </div>
    </div>


    <%-- Form to submit the checkout / payment request --%>
    <form action="${pageContext.request.contextPath}/Checkout" method="POST">
        <button type="submit" class="btn-confirm">Confirm & Pay Securely</button>
    </form>
    
    <%-- Link to go back to the cart if user wants to edit items --%>    
    <a href="${pageContext.request.contextPath}/ViewCart" class="back-to-cart">← Return to Cart</a>
</div>

<%@ include file="../footer.jsp" %>