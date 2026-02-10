<%-- 
    Author(s): Wan Jing Hng
    Date: Updated Feb 2026
    File: manage_bookings.jsp
    Description: Admin interface to manage customer appointments with Date, Service, and Account filters
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Appointments - SilverCare Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_bookings.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Page title --%>
    <div class="admin-container">
        <h1>Manage Customer Appointments</h1>


        <%-- Filter section for searching bookings --%>
        <section class="filter-section">
            <form method="get" action="AdminManageBookings" class="filter-grid">
                
                
                <%-- Filter by customer account name --%>                
                <div class="filter-group">
                    <label for="customerSearch">Client Account</label>
                    <input type="text" name="customerSearch" id="customerSearch" 
                           placeholder="Search name..." value="${param.customerSearch}" class="filter-control">
                </div>


                <%-- Filter by care service --%>
                <div class="filter-group">
                    <label for="serviceId">Care Service</label>
                    <select name="serviceId" id="serviceId" class="filter-control">
                        <option value="">All Services</option>
                        <c:forEach var="svc" items="${services}">
                            <option value="${svc.serviceId}" ${param.serviceId == svc.serviceId ? 'selected' : ''}>
                                ${svc.serviceName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Filter by booking status --%>
                <div class="filter-group">
                    <label for="statusFilter">Status</label>
                    <select name="statusFilter" id="statusFilter" class="filter-control">
                        <option value="">All Statuses</option>
                        <option value="Pending" ${param.statusFilter == 'Pending' ? 'selected' : ''}>Pending</option>
                        <option value="Confirmed" ${param.statusFilter == 'Confirmed' ? 'selected' : ''}>Confirmed</option>
                        <option value="Completed" ${param.statusFilter == 'Completed' ? 'selected' : ''}>Completed</option>
                        <option value="Cancelled" ${param.statusFilter == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>

                <%-- Filter by start date --%>
                <div class="filter-group">
                    <label for="startDate">From Date</label>
                    <input type="date" name="startDate" id="startDate" value="${param.startDate}" class="filter-control">
                </div>

                <%-- Filter by end date --%>
                <div class="filter-group">
                    <label for="endDate">To Date</label>
                    <input type="date" name="endDate" id="endDate" value="${param.endDate}" class="filter-control">
                </div>

                <%-- Filter action buttons --%>
                <div class="filter-actions">
                    <button type="submit" class="btn btn-primary">Search</button>
                    <a href="AdminManageBookings" class="btn btn-secondary">Clear</a>
                </div>
            </form>
        </section>

        <%-- Display success message if available --%>
        <c:if test="${not empty param.message}">
            <div class="alert success-message">${param.message}</div>
        </c:if>

        <%-- Bookings result table --%>
        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Customer</th>
                    <th>Service</th>
                    <th>Booking Date</th>
                    <th>Amount</th>
                    <th>Status</th>
                    <th class="actions-header">Actions</th>
                </tr>
            </thead>
            <tbody>
            
                <%-- Loop through all bookings --%>            
                <c:forEach var="b" items="${bookingsList}">
                    <tr>
                        <td>#${b.bookingId}</td>
                        <td><strong>${b.customerName}</strong></td>
                        <td>
                            ${b.serviceName}
                            <c:if test="${b.serviceCount > 1}">
                                <span style="color: #666; font-size: 0.85rem;">+${b.serviceCount - 1} more</span>
                            </c:if>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty b.bookingDate}">
                                    <fmt:formatDate value="${b.bookingDate}" pattern="dd MMM yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise>
                                    <span style="color: #999;">-</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>S$<fmt:formatNumber value="${b.totalAmount}" pattern="#,##0.00"/></td>
                        <td>
                            <span class="status-badge status-${b.status.toLowerCase()}">${b.status}</span>
                        </td>
                       
                       
                       
                        <%-- Actions: update status and view details --%>                       
                        <td class="actions-cell">
					    <div class="action-container">
					        <form action="AdminManageBookings" method="post" class="status-form">
					            <input type="hidden" name="action" value="updateStatus">
					            <input type="hidden" name="id" value="${b.bookingId}">
					            <select name="status" onchange="this.form.submit()" class="status-select">
					                <option disabled selected>Update Status</option>
					                <option value="Pending">Pending</option>
					                <option value="Confirmed">Confirmed</option>
					                <option value="Completed">Completed</option>
					                <option value="Cancelled">Cancelled</option>
					            </select>
					        </form>
        
                                <%-- View full booking details --%>					
					        <a href="AdminManageBookings?action=view&id=${b.bookingId}" class="btn btn-view">View</a>
					    </div>
					</td>
                    </tr>
                </c:forEach>

                <%-- Message shown when no bookings match filters --%>                
                <c:if test="${empty bookingsList}">
                    <tr>
                        <td colspan="7" class="text-center">No appointments found matching your filters.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <%@ include file="../footer_admin.jsp" %>
</body>
</html>