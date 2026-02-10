<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Janurary 2026
    File: caregiver_profile.jsp
    Description: Caregivers page displaying selected caregivers associated with us
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>${cg.name} - Profile</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/caregiver_profile.css">
            </head>

            <body>
                <%@ include file="./header.jsp" %>

            		<!-- Profile Container -->
                    <div class="profile-container">
                        <a href="${pageContext.request.contextPath}/ViewCaregivers" class="btn-back">← Back to
                            Caregivers</a>

           				 <!-- Profile Card -->
                        <div class="profile-card">
                        
                            <!-- Profile header: image and basic info -->                       
                            <div class="profile-header">
                                <div class="profile-image-box">
                                    <img src="${pageContext.request.contextPath}/${fn:replace(cg.image, '#', '%23')}"
                                        alt="${cg.name}" class="profile-image"
                                        onerror="this.src='https://placehold.co/150x150/7CB342/ffffff?text=${fn:substring(cg.name, 0, 1)}';">
                                </div>

                				<!-- Caregiver basic info: specialty, name, contact -->
                                <div class="profile-info">
                                    <p class="profile-specialty">${cg.specialty}</p>
                                    <h1>${cg.name}</h1>

                                    <div class="profile-contact">
                                        <div class="contact-item">📧 <span>${cg.email}</span></div>
                                        <c:if test="${not empty cg.phone}">
                                            <div class="contact-item">📞 <span>${cg.phone}</span></div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                			<!-- Caregiver basic info: Services Provided -->
                            <div class="profile-body">
                                <div class="section">
                                    <h2>Services Provided</h2>
                                    <div class="service-list">
                                        <c:forEach var="serviceName" items="${services}">
                                            <span class="service-tag">${serviceName}</span>
                                        </c:forEach>
                                        <c:if test="${empty services}">
                                            <p class="bio-text">General Caregiving Support</p>
                                        </c:if>
                                    </div>
                                </div>
 
 				               <!-- Caregiver basic description -->
                                <div class="section">
                                    <h2>Professional Biography</h2>
                                    <p class="bio-text">
                                        ${not empty cg.bio ? cg.bio : 'Dedicated professional providing high-quality
                                        care services.'}
                                    </p>
                                </div>

                               <!--  <div class="profile-actions">
                                    <button class="btn-primary">Book an Appointment</button>
                                </div>  -->
                            </div>
                        </div>
                    </div>

                    <%@ include file="footer.jsp" %>
            </body>

            </html>