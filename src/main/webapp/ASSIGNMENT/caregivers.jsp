<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Janurary 2026
    File: caregivers.jsp
    Description: Caregivers page displaying all caregivers associated with us
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Our Caregivers | SilverCare</title>
                 <!-- Page-specific CSS -->               
                <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/caregivers.css">
            </head>



            <body>
    		 <%-- Shared header include (navigation, logo, global scripts) --%>            
                <%@ include file="header.jsp" %>

                    <header class="page-hero">
                        <h1>Meet Our Experts</h1>
                        <p>Professional care tailored to your family's needs.</p>
                    </header>
                    
    				<!-- Main content: grid of caregivers -->
                    <main class="main-content">
                        <div class="grid-container">
                            <c:choose>
                                <c:when test="${not empty caregiverList}">
                                    <c:forEach var="cg" items="${caregiverList}">
                                        <article class="card">
                                            <div class="card-image-wrapper">
                                                <img src="${pageContext.request.contextPath}/${fn:replace(cg.image, '#', '%23')}"
                                                    alt="${cg.name}"
                                                    onerror="this.src='https://placehold.co/400x300/var(--primary-green)/ffffff?text=Caregiver';">
                                            </div>
                                            <div class="card-body">
                                                <span class="badge">${cg.specialty}</span>
                                                <h3>${cg.name}</h3>
                                                <p>${cg.bio}</p>
                                                <a href="${pageContext.request.contextPath}/ViewCaregiverProfile?id=${cg.caregiverId}"
                                                    class="btn-link">
                                                    View Profile &rarr;
                                                </a>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                
                                    <!-- If No Caregivers, Display Message -->
                                
                                    <div class="empty-state">
                                        <p>No caregivers found at this time.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </main>

    				<!-- Universal Footer -->
                    <%@ include file="footer.jsp" %>
            </body>

            </html>