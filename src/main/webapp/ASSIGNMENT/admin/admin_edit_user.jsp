<%-- 
    Author(s): Wan Jing Hng
    Date: Feb 2026
    File: admin_edit_user.jsp
    Description: Displays full booking details for admins and allows caregiver
                 assignment and schedule updates where applicable.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Patient | SilverCare Admin</title>
    
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_edit_user.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Main wrapper for the edit patient page --%>
    <div class="edit-wrapper">
        <div class="admin-card">
            <div class="card-header">
                <h2>Edit Patient Profile</h2>
                <p>Records for: <strong>${user.firstName} ${user.lastName}</strong></p>
            </div>


            <%-- Form for updating patient details --%>
            <form action="EditUser" method="post" class="edit-form">
                <input type="hidden" name="userId" value="${user.userId}">

                <%-- Section label for basic account details --%>
                <span class="section-label">Account Information</span>
                <div class="form-grid">
                    <div class="input-group">
                        <label>First Name</label>
                        <input type="text" name="firstName" value="${user.firstName}" required>
                    </div>
                    <div class="input-group">
                        <label>Last Name</label>
                        <input type="text" name="lastName" value="${user.lastName}" required>
                    </div>
                    <div class="input-group">
                        <label>Phone Number</label>
                        <input type="text" name="phone" value="${user.phone}">
                    </div>
                    <div class="input-group">
                        <label>Postal Code</label>
                        <input type="text" name="postalCode" value="${user.postalCode}">
                    </div>
                </div>

                <span class="section-label">Medical & Address</span>
                <div class="form-grid">
                    <div class="input-group full-width">
                        <label>Medical Condition(s)</label>
                        <textarea name="medicalCondition" rows="4" class="medical-area">${user.medicalCondition}</textarea>
                    </div>
                    <div class="input-group full-width">
                        <label>Home Address</label>
                        <input type="text" name="address" value="${user.address}">
                    </div>
                </div>

                <span class="section-label">Emergency Contact</span>
                <div class="form-grid">
                    <div class="input-group">
                        <label>Contact Name</label>
                        <input type="text" name="emergencyName" value="${user.emergencyName}">
                    </div>
                    <div class="input-group">
                        <label>Emergency Phone</label>
                        <input type="text" name="emergencyPhone" value="${user.emergencyPhone}">
                    </div>
                </div>

                <div class="form-footer">
                    <button type="submit" class="btn-update">Save Changes</button>
                    <a href="AdminManageCustomers" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>

    <%@ include file="../footer_admin.jsp" %>
</body>
</html>