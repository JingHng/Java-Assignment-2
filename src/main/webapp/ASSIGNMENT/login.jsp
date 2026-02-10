<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: 01 Feb 2026 
    -- File: login.jsp 
    -- Description: Provides unified login for three user roles (Admin, Customer, Caregiver)
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/login.css">


        <!-- Login Container -->
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <h1>SilverCare</h1>
            <p>Access your Dashboard</p>
        </div>


        <!-- Role Selection Tabs -->
        <!-- Allow Users to log in depending on what role they are -->
       
        <div class="role-tabs">
            <button type="button" class="role-tab active" id="customer-tab" onclick="switchRole('customer')">
                Customer
            </button>
            <button type="button" class="role-tab" id="caregiver-tab" onclick="switchRole('caregiver')">
                Caregiver
            </button>
            <button type="button" class="role-tab" id="admin-tab" onclick="switchRole('admin')">
                Admin <span class="admin-label">STAFF</span>
            </button>
        </div>


        <!-- Server Feedback -->
        <!-- Receives message from request param regarding failed, successful or insufficient privileged logins -->
        

        <% 
            String message = request.getParameter("message"); 
            if (message != null && !message.isEmpty()) { 
                String msgLower = message.toLowerCase(); 
                String msgClass = (msgLower.contains("success") || msgLower.contains("successful")) ? "alert-success" : "alert-error"; 
        %>
        
        
        	<!-- Display Feedback Message -->
            <div class="alert <%= msgClass %>">
                <%= message %>
            </div>
        <% } %>


		<!--  Form submits login credentials and selected role to the LoginServlet for authentication.-->
        <form action="${pageContext.request.contextPath}/LoginServlet" method="POST" class="login-form" id="login-form">
            
             <%-- Hidden field storing selected user role --%>      
            <input type="hidden" name="userRole" id="userRole" value="customer">

            <!-- Login ID field (label & placeholder updated dynamically) -->
            <div class="form-group">
                <label for="loginId" id="loginId-label">Email Address</label>
                <input type="text" id="loginId" name="loginId" placeholder="Enter your email" required class="form-control">
            </div>
            
            
            <!-- Password field -->
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Enter your password" required class="form-control">
            </div>

            <!-- Submit button -->
            <button type="submit" class="btn-login">
                Sign In
            </button>
        </form>

            <!-- Footer Universal -->
        <div class="form-footer" id="form-footer">
            <p>
                Don't have an account? <a href="register.jsp">Register here</a>
            </p>
        </div>
    </div>
</div>

<script>

	// Handle UI Updates when user switches between login roles
    function switchRole(role) {
        document.getElementById('customer-tab').classList.toggle('active', role === 'customer');
        document.getElementById('caregiver-tab').classList.toggle('active', role === 'caregiver');
        document.getElementById('admin-tab').classList.toggle('active', role === 'admin');
        document.getElementById('userRole').value = role;

        const loginIdInput = document.getElementById('loginId');
        const loginIdLabel = document.getElementById('loginId-label');
        const formFooter = document.getElementById('form-footer');

        // Update the Login UI based on the user role
        if (role === 'admin') {
            loginIdLabel.textContent = 'Admin User ID';
            loginIdInput.placeholder = 'Enter admin user ID';
            formFooter.style.display = 'none'; 
        } else if (role === 'caregiver') {
            loginIdLabel.textContent = 'Caregiver Username';
            loginIdInput.placeholder = 'Enter your username';
            formFooter.style.display = 'none';
        } else {
            loginIdLabel.textContent = 'Email Address';
            loginIdInput.placeholder = 'Enter your email';
            formFooter.style.display = 'block';
        }
        loginIdInput.value = '';
        document.getElementById('password').value = '';
    }
</script>

<%@ include file="footer.jsp" %>