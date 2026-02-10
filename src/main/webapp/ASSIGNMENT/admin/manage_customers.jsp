<%-- 
    -- Author(s): Wan Jing Hng 
    -- Date: 31 Jan 2026 
    -- File: manage_Customers.jsp
    -- Description: Dedicated view for mangaing customers.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Manage Customers - SilverCare</title>
    <%-- Shared admin footer styling --%>   
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_customers.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Page heading and description --%>
    <div class="admin-container">
        <div class="page-header">
            <h1>Customer Management Hub</h1>
            <p>Full control over patient details, medical history, and account status.</p>
        </div>


        <%-- Integrated Search & Filters --%>
        <form action="AdminManageCustomers" method="get" class="filter-bar">
        
            <%-- Filter customers by medical condition --%>        
            <div class="filter-group">
                <label>Medical Condition</label>
                <input type="text" name="conditionFilter" placeholder="Search health..." value="${param.conditionFilter}">
            </div>

            <%-- Filter customers by postal code --%>      
            <div class="filter-group">
                <label>Postal Code</label>
                <input type="text" name="postalFilter" placeholder="Search area..." value="${param.postalFilter}">
            </div>
            
            <%-- Apply or clear filters --%>            
            <div class="filter-actions">
                <button type="submit" class="btn btn-apply">Apply Filters</button>
                <a href="AdminManageCustomers" class="btn btn-clear">Clear</a>
            </div>
        </form>

        <%-- Table displaying customer details --%>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Patient</th>
                        <th>Postal</th>
                        <th>Medical Records</th>
                        <th>Emergency Contact</th>
                        <th style="text-align: right;">Actions</th>
                    </tr>
                </thead>
                <tbody>
                
                    <%-- Loop through all customers retrieved from backend --%>                
                    <c:forEach var="u" items="${customerList}">
                        <tr>
                            <td>
                                <span class="patient-name">${u.firstName} ${u.lastName}</span>
                                <span class="patient-meta">${u.email} | ${u.phone}</span>
                            </td>
                            <td><code>${u.postalCode}</code></td>
                            <td>
                                <div class="badge-container">
                                    <c:forEach var="item" items="${fn:split(u.medicalCondition, ', ')}">
                                        <span class="badge badge-health">${item}</span>
                                    </c:forEach>
                                </div>
                            </td>
                            <td>
                                <div class="contact-info">
                                    <span>${u.emergencyName}</span><br>
                                    <small>📞 ${u.emergencyPhone}</small>
                                </div>
                            </td>
                            
                            <%-- Navigate to edit customer profile page --%>                           
                            <td style="text-align: right;">
                                <a href="EditUser?id=${u.userId}" class="btn btn-edit">Edit Profile</a>
                                
                                <%-- Deactivate customer account with confirmation --%>             
                                <a href="AdminManageCustomers?action=delete&id=${u.userId}" 
                                   class="btn btn-delete" 
                                   onclick="return confirm('Deactivate this patient?')">Deactivate</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <%@ include file="../footer_admin.jsp" %>
</body>
</html>