<%-- 
    Author: Wan Jing Hng
    Date: 10 Feb 2026
    File: service_analytics.jsp
    Description: Displays a service analytics page using JFreeChart to visualize total bookings per service category.
                 Includes date range filters and a dynamically generated chart.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Service Analytics - SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/service_analytics.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <main class="admin-main">
    
        <%-- Hero section with title and description --%>    
        <div class="dashboard-hero">
            <div style="margin-bottom: 10px;">
                <%-- Link to go back to admin dashboard --%>            
                <a href="AdminDashboard" style="text-decoration:none; color:var(--primary-blue); font-weight:bold;">← Back to Dashboard</a>
            </div>
            
            
			<%-- Main page title --%>            
            <h1>Service Analytics (JFreeChart)</h1>
            <p>Visualizing total bookings per service category using Java-based charts.</p>
            <p style="font-size: 0.9rem; color: #666; margin-top: 5px;">💡 Use the date filters below to view bookings for a specific period</p>
        </div>

        <%-- Controls section with date filters --%>
        <div class="controls-container">
            <form action="ServiceAnalytics" method="GET" style="display:flex; gap:15px; align-items:center; width:100%; flex-wrap: wrap;">
                <div style="display:flex; align-items:center; gap:8px;">
               		
               		
               		<%-- Start date filter --%>                
                    <label>From:</label>
                    <input type="date" name="startDate" value="${param.startDate}" class="search-input" style="width: auto;">
                	</div>
                
                <%-- End date filter --%>                	
                <div style="display:flex; align-items:center; gap:8px;">
                    <label>To:</label>
                    <input type="date" name="endDate" value="${param.endDate}" class="search-input" style="width: auto;">
                </div>
                
                <%-- Submit button to filter data --%>                
                <button type="submit" class="btn-report" style="border:none; cursor:pointer;">Filter Data</button>

                <%-- Reset filters link --%>               
                <a href="ServiceAnalytics" class="btn-report" style="background:#6c757d; border:none; text-decoration:none;">Reset</a>
           		</form>
        		</div>
     
        <%-- Chart display card --%>
        <div class="insight-card" style="padding: 30px; min-height: 450px; text-align: center;">
        
            <%-- Dynamically generate chart image via JFreeChart servlet --%>            
            <img src="ServiceAnalytics?action=generateChart&startDate=${param.startDate}&endDate=${param.endDate}" 
                 alt="Service Analytics Bar Chart" 
                 style="max-width: 100%; height: auto; border: 1px solid #e0e0e0; border-radius: 8px;">
       		</div>

        <%-- Footer note about chart library --%>        
        <div style="text-align: center; margin-top: 20px; color: #666; font-size: 0.9rem;">
            <p>📊 Chart generated using <strong>JFreeChart</strong> - A pure Java charting library</p>
        </div>
    </main>

    <%@ include file="../footer_admin.jsp" %>
</body>
</html>