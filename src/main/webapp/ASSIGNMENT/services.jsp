<%-- 
    -- Author(s): Wan Jing Hng
    -- File: services.jsp
    -- Description: Categorized Catalog with 4+ distinct healthcare sections.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="header.jsp" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/services.css">

<div class="services-container">
    <header class="category-header">
        <span class="category-badge">SilverCare Catalog</span>
        <h1 class="category-title">Our Professional Care Services</h1>
        <p class="category-description">Specialized support designed for dignity, health, and comfort.</p>
    </header>

    <%-- Filter Control --%>
    <div class="filter-controls">
        <form action="${pageContext.request.contextPath}/ViewServices" method="GET">
            <select name="categoryId" onchange="this.form.submit()" class="category-dropdown">
                <option value="0">Show All Categories</option>
                <option value="4" ${param.categoryId == '4' ? 'selected' : ''}>Food & Nutrition</option>
                <option value="1" ${param.categoryId == '1' ? 'selected' : ''}>In-Home Care</option>
                <option value="2" ${param.categoryId == '2' ? 'selected' : ''}>Assisted Living & Rehab</option>
                <option value="3" ${param.categoryId == '3' ? 'selected' : ''}>Specialized Dementia Care</option>
            </select>
        </form>
    </div>

    <div class="catalog-sections">
        
        <%-- SECTION 1: FOOD & NUTRITION (Category 4) - ALWAYS TOP --%>
        <c:if test="${param.categoryId == '0' || empty param.categoryId || param.categoryId == '4'}">
            <section class="category-group food-services-group">
                <div class="group-header">
                    <h2><span class="icon">🥗</span> Food & Nutritional Services</h2>
                    <p>B2B ready meal plans tailored for specific medical dietary requirements. <br> Outsourced to Silvercare Logistics</p>
                </div>
                <div class="services-grid">
                    <c:forEach var="service" items="${serviceList}">
                        <c:if test="${service.categoryId == 4}">
                            <div class="service-card food-card">
                                <img src="${pageContext.request.contextPath}/ASSIGNMENT/${service.imageLocation}" class="service-image">
                                <div class="service-info">
                                    <h3 class="service-name">${service.serviceName}</h3>
                                    <div class="service-price"><fmt:formatNumber value="${service.price}" type="currency"/><span>/meal</span></div>
                                    <a href="ServiceDetails?id=${service.serviceId}" class="btn-view">Book Plan</a>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <%-- SECTION 2: IN-HOME CARE (Category 1) --%>
        <c:if test="${param.categoryId == '0' || empty param.categoryId || param.categoryId == '1'}">
            <section class="category-group">
                <div class="group-header">
                    <h2><span class="icon">🏠</span> In-Home Nursing & Care</h2>
                    <p>Professional assistance for daily living in the comfort of home.</p>
                </div>
                <div class="services-grid">
                    <c:forEach var="service" items="${serviceList}">
                        <c:if test="${service.categoryId == 1}">
                            <div class="service-card">
                                <img src="${pageContext.request.contextPath}/ASSIGNMENT/${service.imageLocation}" class="service-image">
                                <div class="service-info">
                                    <h3 class="service-name">${service.serviceName}</h3>
                                    <div class="service-price"><fmt:formatNumber value="${service.price}" type="currency"/><span>/hr</span></div>
                                    <a href="ServiceDetails?id=${service.serviceId}" class="btn-view">View Details</a>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <%-- SECTION 3: ASSISTED LIVING & THERAPY (Category 2) --%>
        <c:if test="${param.categoryId == '0' || empty param.categoryId || param.categoryId == '2'}">
            <section class="category-group">
                <div class="group-header">
                    <h2><span class="icon">♿</span> Assisted Living & Rehabilitation</h2>
                    <p>Facility-based support and physical rehabilitation sessions.</p>
                </div>
                <div class="services-grid">
                    <c:forEach var="service" items="${serviceList}">
                        <c:if test="${service.categoryId == 2}">
                            <div class="service-card">
                                <img src="${pageContext.request.contextPath}/ASSIGNMENT/${service.imageLocation}" class="service-image">
                                <div class="service-info">
                                    <h3 class="service-name">${service.serviceName}</h3>
                                    <div class="service-price"><fmt:formatNumber value="${service.price}" type="currency"/><span>/session</span></div>
                                    <a href="ServiceDetails?id=${service.serviceId}" class="btn-view">View Details</a>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>

        <%-- SECTION 4: DEMENTIA CARE (Category 3) --%>
        <c:if test="${param.categoryId == '0' || empty param.categoryId || param.categoryId == '3'}">
            <section class="category-group">
                <div class="group-header">
                    <h2><span class="icon">🧠</span> Specialized Dementia Care</h2>
                    <p>Memory support and behavioral care for cognitive health.</p>
                </div>
                <div class="services-grid">
                    <c:forEach var="service" items="${serviceList}">
                        <c:if test="${service.categoryId == 3}">
                            <div class="service-card memory-card">
                                <img src="${pageContext.request.contextPath}/ASSIGNMENT/${service.imageLocation}" class="service-image">
                                <div class="service-info">
                                    <h3 class="service-name">${service.serviceName}</h3>
                                    <div class="service-price"><fmt:formatNumber value="${service.price}" type="currency"/><span>/hr</span></div>
                                    <a href="ServiceDetails?id=${service.serviceId}" class="btn-view">View Details</a>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </section>
        </c:if>

    </div>
</div>

<%@ include file="footer.jsp" %>