<%-- 
    Author(s): Wan Jing Hng
    Date: 1 Feb 2026
    File: admin_dashboard.jsp
    Description: Display all statistics and everything about the website for the admin to view
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../security_check.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard - SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css"> 
</head>
<body>
    <%@ include file="../header.jsp" %>


    <%-- Main dashboard container --%>
    <main class="admin-main">
        <div class="dashboard-hero">
            <h1>Admin Dashboard</h1>
            <p>Welcome, <strong>${firstName}</strong>. System status: Online.</p>
        </div>


        <%-- Summary statistics displayed as cards --%>
        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-title">Services</div>
                <div class="stat-value">${stats.serviceCount}</div>
            </div>
            <div class="stat-card">
                <div class="stat-title">Caregivers</div>
                <div class="stat-value">${stats.caregiverCount}</div>
            </div>
            <div class="stat-card">
                <div class="stat-title">Customers</div>
                <div class="stat-value">${stats.customerCount}</div>
            </div>
            <div class="stat-card highlight-emerald">
                <div class="stat-title">Medical Records</div>
                <div class="stat-value">${stats.medicalCount}</div>
            </div>
        </div>


        <%-- Controls for filtering insight data --%>
        <div class="controls-container">
            <input type="text" id="serviceSearch" class="search-input" placeholder="Search data..." onkeyup="runGlobalFilter()">
            <select id="globalToggle" class="filter-dropdown" onchange="runGlobalFilter()">
                <option value="top">Top Performers</option>
                <option value="low">Underperformers</option>
            </select>
        </div>
      
        
        <%-- Grid containing system insights --%>
        <div class="insights-grid">
        
             <%-- Service popularity insights --%>       
            <div class="insight-card border-blue">
                <h4>Popularity</h4>
                <div id="pop-top">
                    <c:forEach var="i" items="${popTop}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-blue">${i.display}</span></div></c:forEach>
                </div>
                
                 <%-- Least popular services --%>                
                <div id="pop-low" class="hidden">
                    <c:forEach var="i" items="${popLow}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-red">${i.display}</span></div></c:forEach>
                </div>
            </div>
            
            <%-- Client booking rate insights --%>
            <div class="insight-card border-gold">
                <h4>Top Clients</h4>
                <div id="rate-top">
                    <c:forEach var="i" items="${rateTop}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-gold">${i.display}</span></div></c:forEach>
                </div>
                <div id="rate-low" class="hidden">
                    <c:forEach var="i" items="${rateLow}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-red">${i.display}</span></div></c:forEach>
                </div>
            </div>
            
            <%-- Revenue performance insights --%>
            <div class="insight-card border-green">
                <h4>Revenue</h4>
                <div id="rev-top">
                    <c:forEach var="i" items="${revTop}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-green">${i.display}</span></div></c:forEach>
                </div>
                <div id="rev-low" class="hidden">
                    <c:forEach var="i" items="${revLow}"><div class="insight-row" data-name="${i.name.toLowerCase()}"><span>${i.name}</span><span class="val-red">${i.display}</span></div></c:forEach>
                </div>
            </div>
        </div>

        <%-- Section header for admin shortcuts --%>
        <div class="section-header"><h2 class="section-title">Quick Actions</h2></div>
        <div class="modules-grid">
            <a href="AdminManageServices" class="module-card card-blue"><div class="module-icon">🛠️</div><div class="module-info"><h3>Services</h3><p>Manage pricing</p></div></a>
            <a href="AdminManageCaregivers" class="module-card card-purple"><div class="module-icon">👩‍⚕️</div><div class="module-info"><h3>Caregivers</h3><p>Manage staff</p></div></a>
            <a href="AdminManageCustomers" class="module-card card-teal"><div class="module-icon">👤</div><div class="module-info"><h3>Customers</h3><p>Client database</p></div></a>
            <a href="AdminManageBookings" class="module-card card-orange"><div class="module-icon">📅</div><div class="module-info"><h3>Bookings</h3><p>Schedules</p></div></a>
            <a href="AdminManageFeedback" class="module-card card-red"><div class="module-icon">💬</div><div class="module-info"><h3>Feedback</h3><p>Reviews</p></div></a>
            <a href="AdminManageGST" class="module-card card-gold"><div class="module-icon">💰</div><div class="module-info"><h3>Update GST</h3><p>Tax config</p></div></a>
			<a href="ServiceAnalytics" class="module-card card-emerald"><div class="module-icon">📊</div><div class="module-info"><h3>Service Analytics</h3><p>Bookings by service & date</p></div></a>
        </div>
    </main>
    <%@ include file="../footer_admin.jsp" %>
    
    
    
    <script>
    	// Filters insight data based on search input and performance mode
        function runGlobalFilter() {
            const mode = document.getElementById('globalToggle').value;
            const search = document.getElementById('serviceSearch').value.toLowerCase();
            const cats = ['pop', 'rate', 'rev'];
            
            // Toggle between top and low performer views
            cats.forEach(c => {
                document.getElementById(c + '-top').classList.toggle('hidden', mode !== 'top');
                document.getElementById(c + '-low').classList.toggle('hidden', mode !== 'low');
            });
            
            // Filter rows based on matching service name
            document.querySelectorAll('.insight-row').forEach(row => {
                const nameAttr = row.getAttribute('data-name');
                row.style.display = (nameAttr && nameAttr.includes(search)) ? "flex" : "none";
            });
        }
    </script>
</body>
</html>