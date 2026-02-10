<%-- 
    -- Author(s): Wan Jing Hng 
    -- Date: 31 Jan 2026 
    -- File: manage_gst.jsp
    -- Description: Dedicated view for updating system-wide GST settings.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>GST Settings - SilverCare Admin</title>
    <%-- Linking all necessary stylesheets --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_gst.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <main class="admin-main">
    
        <%-- Section Header --%>    
        <div class="section-header">
            <h2 class="section-title">GST Configuration</h2>
            <p>Adjust the Goods and Services Tax percentage applied to all checkout totals.</p>
        </div>


        <%-- GST Update Form --%>
        <div class="settings-container">
            <form action="${pageContext.request.contextPath}/UpdateGSTRate" method="POST">
                <div class="form-group">
                    <label for="gstRate">Current Tax Rate</label>
                    <div class="gst-input-wrapper">
                        <input type="number" 
                               id="gstRate" 
                               name="gstRate" 
                               step="0.01" 
                               min="0" 
                               max="100" 
                               value="${currentGST}" 
                               required>
                    </div>
                    <small style="color: #7f8c8d; display: block; margin-top: 10px;">
                        Note: Changes take effect immediately for all new bookings.
                    </small>
                </div>

                <button type="submit" class="btn-save">Update System Rate</button>
            </form>
            
            <%-- Back link to admin dashboard --%>            
            <a href="${pageContext.request.contextPath}/AdminDashboard" class="back-link">
                ← Return to Admin Dashboard
            </a>
        </div>
    </main>

    <%@ include file="../footer_admin.jsp" %>
</body>
</html>