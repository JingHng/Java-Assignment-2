<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: 01 Feb 2026 
    -- File: register.jsp 
    -- Description: Registration with Multi-select Medical Conditions & Postal Code
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/register.css">

<div class="register-container">
    <div class="register-card">
        <div class="register-header">
            <div class="register-icon">✨</div>
            <h1>Join SilverCare</h1>
            <p>Create your account to manage specialized care for your loved ones</p>
        </div>
        
        <%
            String message = request.getParameter("message");
            if (message != null && !message.isEmpty()) {
                String msgClass = message.toLowerCase().contains("success") ? "alert-success" : "alert-error";
        %>
            <div class="alert <%= msgClass %>">
                <%= message %>
            </div>
        <%
            }
        %>
        
        <form action="${pageContext.request.contextPath}/RegisterServlet" method="POST" class="register-form" id="regForm">
            
            <%-- 1. Personal Details --%>
            <h2 class="section-header">Personal Details</h2>
            <div class="form-row">
                <div class="form-group">
                    <label for="firstName">First Name <span class="required">*</span></label>
                    <input type="text" id="firstName" name="firstName" required placeholder="John">
                </div>
                <div class="form-group">
                    <label for="lastName">Last Name <span class="required">*</span></label>
                    <input type="text" id="lastName" name="lastName" required placeholder="Doe">
                </div>
            </div>


            <div class="form-row">
                <div class="form-group">
                    <label for="email">Email Address <span class="required">*</span></label>
                    <input type="email" id="email" name="email" required placeholder="john@example.com">
                </div>
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="tel" id="phone" name="phone" placeholder="+65 9123 4567">
                </div>
            </div>

            <div class="form-group">
                <label for="language">Preferred Language</label>
                <select id="language" name="language">
                    <option value="EN" selected>English</option>
                    <option value="ZH">Mandarin</option>
                    <option value="ML">Malay</option>
                    <option value="TA">Tamil</option>
                </select>
            </div>

            <%-- 2. Care & Location Details --%>
            <h2 class="section-header">Care & Location Details</h2>
            
            <div class="form-group">
                <label>Medical Conditions / Care Needs (Select all that apply) <span class="required">*</span></label>
                <div class="condition-grid">
                    <label class="checkbox-item"><input type="checkbox" name="condition" value="Dementia"> Dementia</label>
                    <label class="checkbox-item"><input type="checkbox" name="condition" value="Mobility Issues"> Mobility Issues</label>
                    <label class="checkbox-item"><input type="checkbox" name="condition" value="Diabetes"> Diabetes</label>
                    <label class="checkbox-item"><input type="checkbox" name="condition" value="Hypertension"> Hypertension</label>
                    <label class="checkbox-item"><input type="checkbox" name="condition" value="Post-Surgery"> Post-Surgery</label>
                    <label class="checkbox-item">
                        <input type="checkbox" id="otherCheck" onchange="toggleOtherInput()"> Other
                    </label>
                </div>
                <input type="text" id="otherText" placeholder="Please specify other conditions..." class="hidden-input">
                
                <%-- Hidden field to hold the final merged string for DB --%>
                <input type="hidden" name="medicalCondition" id="finalCondition">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="postalCode">Postal Code <span class="required">*</span></label>
                    <input type="text" id="postalCode" name="postalCode" required pattern="[0-9]{6}" placeholder="e.g. 560123">
                </div>
                <div class="form-group">
                    <label for="emergencyName">Emergency Contact Name <span class="required">*</span></label>
                    <input type="text" id="emergencyName" name="emergencyName" required placeholder="Next of Kin Name">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="emergencyPhone">Emergency Contact Phone <span class="required">*</span></label>
                    <input type="tel" id="emergencyPhone" name="emergencyPhone" required placeholder="Contact Number">
                </div>
                <div class="form-group">
                    <label for="address">Residential Address</label>
                    <input type="text" id="address" name="address" placeholder="Unit no., Street name">
                </div>
            </div>
            
            <%-- 3. Security --%>
            <h2 class="section-header">Account Security</h2>
            <div class="form-row">
                <div class="form-group">
                    <label for="password">Password <span class="required">*</span></label>
                    <input type="password" id="password" name="password" required minlength="6" placeholder="Min 6 characters">
                </div>
                <div class="form-group">
                    <label for="confirm_password">Confirm Password <span class="required">*</span></label>
                    <input type="password" id="confirm_password" name="confirm_password" required placeholder="Repeat password">
                </div>
            </div>

            <button type="submit" class="btn-register">Create Account</button>
            
            <div class="form-footer">
                <p>Already have an account? <a href="login.jsp">Sign in here</a></p>
            </div>
        </form>
    </div>
</div>

<script>
    function toggleOtherInput() {
        const otherText = document.getElementById('otherText');
        otherText.style.display = document.getElementById('otherCheck').checked ? 'block' : 'none';
        if(otherText.style.display === 'block') otherText.focus();
    }

    // Process checkboxes into a single string before submitting
    document.getElementById('regForm').onsubmit = function(e) {
        const password = document.getElementById('password').value;
        const confirm = document.getElementById('confirm_password').value;

        if (password !== confirm) {
            alert("Passwords do not match!");
            e.preventDefault();
            return false;
        }

        let selected = [];
        // Get all standard checkboxes
        document.querySelectorAll('input[name="condition"]:checked').forEach(cb => {
            selected.push(cb.value);
        });
        
        // Handle "Other" field
        const otherCheck = document.getElementById('otherCheck');
        const otherText = document.getElementById('otherText');
        if (otherCheck.checked && otherText.value.trim() !== "") {
            selected.push(otherText.value.trim());
        }

        if (selected.length === 0) {
            alert("Please select at least one medical condition or 'None'.");
            e.preventDefault();
            return false;
        }

        document.getElementById('finalCondition').value = selected.join(", ");
    };
</script>

<%@ include file="footer.jsp" %>