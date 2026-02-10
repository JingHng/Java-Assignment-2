<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Janurary 202
    File: index_delivery.jsp
    Description: Delivery Management dashboard for SilverCare Logistics; 
                 includes live order tracking, dispatch actions, and B2B integration tools.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SilverCare | Delivery Management</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/index_delivery.css">
</head>
<body>

    <div class="container">
    
        <!-- Header section with page title and sync button -->
        <div class="header-box">
            <div class="logistics-badge">Delivery Operations</div>
            <h1>SilverCare <span class="logistics-accent">Logistics</span></h1>
            <p class="slogan">Reliable meal delivery management for the SilverCare ecosystem.</p>
            <p class="text-muted">
                Monitor live manifests, coordinate fleet dispatch, and manage digital invoicing 
                for all nutritional support services.
            </p>
            
             <!-- /GET Button that calls the servlet in which then consumes the /GET Endpoint from B2B -->
            <a href="${pageContext.request.contextPath}/ManageDelivery" class="btn-retrieve">
                Sync Active Orders
            </a>
            
        </div>
        
        <%-- Info Cards Section --%>
        <c:if test="${empty deliveryOrders}">
            <div class="info-section">
                <div class="info-card">
                    <h3>Order Tracking</h3>
                    <p>Real-time visibility into incoming care requests and meal distribution schedules.</p>
                </div>
                <div class="info-card">
                    <h3>Fleet Dispatch</h3>
                    <p>Standardized workflows for transitioning orders from reception to transit and final delivery.</p>
                </div>
                <div class="info-card">
                    <h3>Cloud Invoicing</h3>
                    <p>Instant generation of digital receipts upon verified customer receipt confirmation.</p>
                </div>
            </div>
            
            <div class="welcome-message">
                <h3>Welcome to the Delivery Partner Dashboard</h3>
                <p>
                    This portal provides comprehensive tools for managing nutritional meal deliveries to elderly care recipients. 
                    Our B2B integration ensures seamless coordination between SilverCare's meal ordering system and your delivery operations.
                </p>
                <p class="text-muted mt-3">
                    Click "Retrieve All Deliveries" above to load your active orders and begin managing today's deliveries.
                </p>
            </div>
        </c:if>


        <%-- Active Orders Section --%>
        <c:if test="${not empty deliveryOrders}">
            <div class="stats-bar">
                <div class="stat-item">
                    <div class="stat-number">${deliveryOrders.size()}</div>
                    <div class="stat-label">Active Orders</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number">
                        <c:set var="pendingCount" value="0" />
                        <c:forEach var="order" items="${deliveryOrders}">
                            <c:if test="${order.deliveryStatus == 'Food Delivery Company Order Received'}">
                                <c:set var="pendingCount" value="${pendingCount + 1}" />
                            </c:if>
                        </c:forEach>
                        ${pendingCount}
                    </div>
                    <div class="stat-label">Awaiting Dispatch</div>
                </div>
                <div class="stat-item">
                    <div class="stat-number">
                        <c:set var="transitCount" value="0" />
                        <c:forEach var="order" items="${deliveryOrders}">
                            <c:if test="${order.deliveryStatus == 'Dispatched'}">
                                <c:set var="transitCount" value="${transitCount + 1}" />
                            </c:if>
                        </c:forEach>
                        ${transitCount}
                    </div>
                    <div class="stat-label">In Transit</div>
                </div>
            </div>
            
            
            <!-- Live Delivery Tracking Table -->          
            <div class="manifest-card shadow-sm">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h5 class="mb-0 fw-bold">Live Delivery Tracking</h5>
                        <p class="text-muted small mb-0">Real-time order tracking and management</p>
                    </div>
                    
                    <!-- /GET by ID - REST -->           
                    <form action="${pageContext.request.contextPath}/ManageDelivery" method="GET" class="d-flex gap-2">
                        <input type="text" name="searchId" class="form-control form-control-sm" placeholder="Order ID..." style="width: 120px;">
                        <button type="submit" class="btn btn-dark btn-sm">Search</button>
                    </form>
                </div>

            <!-- Table to display all the orders -->
                <div class="table-responsive">
                    <table class="table mb-0">
                        <thead>
                            <tr>
                                <th>Ref</th>
                                <th>Recipient & Address</th>
                                <th>Meal Item</th>
                                <th>Status</th>
                                <th class="text-end">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${deliveryOrders}">
                                <tr>
                                    <td><span class="text-muted">#${order.bookingId}</span></td>
                                    <td>
                                        <div class="fw-bold">${order.customer}</div>
                                        <div class="small text-muted">${order.address}</div>
                                    </td>
                                    <td>${order.meal}</td>
                    	          <td>
									    <c:choose>
									        <c:when test="${order.deliveryStatus == 'Food Delivery Company Order Received'}">
									            <span class="s-badge bg-pending">NEW</span>
									        </c:when>
									        <c:when test="${order.deliveryStatus == 'Dispatched'}">
									            <span class="s-badge bg-transit">TRANSIT</span>
									        </c:when>
									        <c:when test="${order.deliveryStatus == 'Received by Customer'}">
									            <span class="s-badge bg-done">RECEIVED</span>
									        </c:when>
									        <c:when test="${order.deliveryStatus == 'Cancelled'}">
									            <span class="s-badge bg-cancelled">CANCELLED</span>
									        </c:when>
									        <c:otherwise>
									            <span class="text-muted small">${order.deliveryStatus}</span>
									        </c:otherwise>
									    </c:choose>
									</td>
                                    <td class="text-end">
                                        <div class="d-flex justify-content-end gap-2">
                                            <%-- Dispatch and Cancel show only for New Orders --%>
                                            <c:if test="${order.deliveryStatus == 'Food Delivery Company Order Received'}">
                                                
                                                <!-- /PUT - REST - Calls the Servlet which will make consume the endpoint  -->                                                      
                                                <form action="${pageContext.request.contextPath}/UpdateDeliveryStatus" method="POST" class="d-inline">
                                                    <input type="hidden" name="id" value="${order.bookingId}">
                                                    <input type="hidden" name="status" value="Dispatched">
                                                    <button type="submit" class="btn-sm-action">Dispatch</button>
                                                </form>
                                                
                                                 <!-- /DELETE - REST - Calls the Servlet which will make consume the endpoint  -->                                                                                                    
                                                <form action="${pageContext.request.contextPath}/CancelOrder" method="POST" class="d-inline" onsubmit="return confirm('Are you sure you want to cancel Order #${order.bookingId}?');">
                                                    <input type="hidden" name="id" value="${order.bookingId}">
                                                    <button type="submit" class="btn-sm-action text-danger">Cancel</button>
                                                </form>
                                            </c:if>

                                            <%-- Invoice shows only for Received Orders --%>
                                        <c:if test="${order.deliveryStatus == 'Received by Customer'}">
                                                <form action="${pageContext.request.contextPath}/GenerateInvoice" method="POST" class="d-inline">
                                                    <input type="hidden" name="bookingId" value="${order.bookingId}">
                                                    <button type="submit" class="btn-sm-action" style="color: #27ae60;">Invoice</button>
                                                </form>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
    </div>

    <%@ include file="../footer_delivery.jsp" %>

</body>
</html>