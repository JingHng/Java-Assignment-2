<%-- 
    Author(s): Wan Jing Hng
    Date: 01 Feb 2026
    File: edit_profile.jsp
    Description: Full Profile Edit with expanded "Other" conditions textarea
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Profile - SilverCare</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/edit_profile.css">
</head>
<body>
    <%@ include file="../header.jsp" %>


    <!-- Main content wrapper -->
    <main class="profile-main">
        <div class="profile-container">
            
            <!-- Header section with title and description -->
            <div class="profile-header">
                <h1><span class="icon-main">⚙️</span> Manage Your Profile</h1>
                <p>Update your personal information and care requirements below.</p>
            </div>

            <!-- Display informational message if any -->
            <c:if test="${not empty param.message}">
                <div class="alert alert-info"><c:out value="${param.message}" /></div>
            </c:if>



            <!-- Show form only if userProfile exists -->
            <c:choose>
                <c:when test="${not empty userProfile}">
                    <form action="${pageContext.request.contextPath}/ProcessEditProfile" method="post" id="editProfileForm">
                        <div class="form-grid">
                            
                            <%-- Section 1: Personal --%>
                            <div class="form-section">
                                <h3>👤 Personal & Contact</h3>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>First Name</label>
                                        <input type="text" name="firstName" value="${userProfile.firstName}" required>
                                    </div>
                                    <div class="form-group">
                                        <label>Last Name</label>
                                        <input type="text" name="lastName" value="${userProfile.lastName}" required>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Email Address</label>
                                    <input type="email" value="${userProfile.email}" readonly class="readonly-field">
                                </div>
                                <div class="form-row">
                                    <div class="form-group">
                                        <label>Phone Number</label>
                                        <input type="tel" name="phone" value="${userProfile.phone}">
                                    </div>
                                    <div class="form-group">
                                        <label>Postal Code</label>
                                        <input type="text" name="postalCode" value="${userProfile.postalCode}" required pattern="[0-9]{6}">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>Residential Address</label>
                                    <textarea name="address" rows="2">${userProfile.address}</textarea>
                                </div>
                            </div>

                            <%-- Section 2: Health --%>
                            <div class="form-section">
                                <h3>🏥 Care & Health Details</h3>
                                <label class="label-heading">Medical Conditions</label>
                                
                                <input type="hidden" id="rawConditions" value="${userProfile.medicalCondition}">

                                <div class="condition-selector">
                                    <label class="cb-item"><input type="checkbox" name="cond" value="Dementia"> Dementia</label>
                                    <label class="cb-item"><input type="checkbox" name="cond" value="Mobility Issues"> Mobility Issues</label>
                                    <label class="cb-item"><input type="checkbox" name="cond" value="Diabetes"> Diabetes</label>
                                    <label class="cb-item"><input type="checkbox" name="cond" value="Hypertension"> Hypertension</label>
                                    <label class="cb-item"><input type="checkbox" name="cond" value="Post-Surgery"> Post-Surgery</label>
                                    <label class="cb-item"><input type="checkbox" id="otherCheck" onchange="toggleOtherInput()"> <strong>Other</strong></label>
                                </div>
                                
                                <%-- Expanded Other Textarea --%>
                                <textarea id="otherText" placeholder="Please provide details about other conditions or special requirements..." 
                                          class="other-input-box" rows="5"></textarea>

                                <div class="emergency-box">
                                    <p style="margin-top:0; font-weight:bold; color:#be123c; font-size:0.8rem;">EMERGENCY CONTACT</p>
                                    <div class="form-row">
                                        <div class="form-group">
                                            <label>Name</label>
                                            <input type="text" name="emergencyName" value="${userProfile.emergencyName}" required>
                                        </div>
                                        <div class="form-group">
                                            <label>Phone</label>
                                            <input type="tel" name="emergencyPhone" value="${userProfile.emergencyPhone}" required>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="form-group" style="margin-top:20px;">
                                    <label>Update Password (Optional)</label>
                                    <input type="password" name="newpassword" placeholder="Leave blank to keep current">
                                </div>
                            </div>
                        </div>

                        <!-- Hidden field to store final concatenated medical conditions -->
                        <input type="hidden" name="medicalCondition" id="finalCondition">

                        <!-- Form action buttons -->
                        <div class="form-actions">
                            <button type="submit" class="btn-save">💾 Save Profile Changes</button>
                            <a href="${pageContext.request.contextPath}/Dashboard" class="btn-cancel">Discard Changes</a>
                        </div>
                    </form>
                </c:when>
            </c:choose>
        </div>
    </main>


    <!-- JavaScript to handle "Other" conditions and form submission -->
    <script>
    
    	// On page load: populate checkboxes and other textarea from saved data
        window.onload = function() {
            const raw = document.getElementById('rawConditions').value;
            if (!raw) return;

            const savedItems = raw.split(", ").map(item => item.trim());
            const standardOptions = ["Dementia", "Mobility Issues", "Diabetes", "Hypertension", "Post-Surgery"];
            let otherItems = [];

            savedItems.forEach(item => {
                let checkbox = document.querySelector(`input[name="cond"][value="${item}"]`);
                if (checkbox) {
                    checkbox.checked = true;
                } else if (item !== "") {
                    otherItems.push(item);
                }
            });

            
            // Populate Other field if needed
            if (otherItems.length > 0) {
                document.getElementById('otherCheck').checked = true;
                const otherBox = document.getElementById('otherText');
                otherBox.style.display = 'block';
                otherBox.value = otherItems.join(", ");
            }
        };

        
        // Toggle visibility of "Other" textarea        
        function toggleOtherInput() {
            const otherText = document.getElementById('otherText');
            otherText.style.display = document.getElementById('otherCheck').checked ? 'block' : 'none';
        }

        document.getElementById('editProfileForm').onsubmit = function() {
            let selected = [];
            document.querySelectorAll('input[name="cond"]:checked').forEach(cb => selected.push(cb.value));
            
            const otherCheck = document.getElementById('otherCheck');
            const otherText = document.getElementById('otherText');
            if (otherCheck.checked && otherText.value.trim() !== "") {
                selected.push(otherText.value.trim());
            }

            // Prepare final medicalCondition value before submitting form
            document.getElementById('finalCondition').value = selected.join(", ");
            return true;
        };
    </script>
    <%@ include file="../footer.jsp" %>
</body>
</html>