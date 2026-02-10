<%-- 
    Author(s): Wan Jing Hng
    Date: 7 Feb 2026
    File: admin_report.jsp
    Description: Displays full booking details for admins and allows caregiver
                 assignment and schedule updates where applicable.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Admin - Patient Census Report</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_report.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Page title and description --%>
    <div class="report-container">
        <div class="report-header">
            <h1>Patient Care Census</h1>
            <p>Filtering and monitoring active customer health profiles.</p>
        </div>

        <%-- Filter form to narrow down patient records --%>        
        <form action="AdminReport" method="get" class="filter-bar">
            <div class="filter-group">
                <label>Filter by Condition</label>
                <input type="text" name="conditionFilter" placeholder="e.g. Dementia" value="${param.conditionFilter}">
            </div>
            <div class="filter-group">
                <label>Postal Code Prefix</label>
                <input type="text" name="postalFilter" placeholder="e.g. 52" value="${param.postalFilter}">
            </div>
            <button type="submit" class="btn-filter">Apply Filters</button>
            <a href="AdminReport" class="btn-reset">Reset</a>
        </form>


        <%-- Table displaying filtered patient records --%>
        <table class="report-table">
            <thead>
                <tr>
                    <th>Patient Name</th>
                    <th>Postal</th>
                    <th>Medical Conditions</th>
                    <th>Emergency Contact</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            
                <%-- Loop through each patient in the list --%>            
                <c:forEach var="u" items="${userList}">
                    <tr>
                        <td class="td-name">
                            <strong>${u.firstName} ${u.lastName}</strong><br>
                            <span class="email-sub">${u.email}</span>
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
                            <span class="em-name">${u.emergencyName}</span><br>
                            <a href="tel:${u.emergencyPhone}" class="em-phone">📞 ${u.emergencyPhone}</a>
                        </td>
                        <td>
                            <a href="EditUser?id=${u.userId}" class="btn-action">View</a>
                        </td>
                    </tr>
                </c:forEach>
                
                <%-- Message shown when no patients match filters --%>                
                <c:if test="${empty userList}">
                    <tr>
                        <td colspan="5" style="text-align:center; padding: 40px; color: #94a3b8;">
                            No patients found matching those filters.
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <%@ include file="../footer_admin.jsp" %>

</body>
</html>