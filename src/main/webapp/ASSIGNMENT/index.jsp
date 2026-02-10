<%-- 
    -- Author(s): Wan Jing Hng
    -- Date: 01 Feb 2026 
    -- File: index.jsp 
    -- Description: public landing page for the SilverCare web application. It introduces the organization, highlights core services, and encourages visitors to register or learn more about the company
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="./header.jsp" %> 

<link rel="stylesheet" href="./Css/index.css">

<main>


	            <!-- Hero Section -->
    <section class="hero-wrapper">
        <div class="main-container">
        
            <!-- Primary marketing headline -->
            <span class="hero-tag">SilverCare Premium Home Support</span>
            <h1>Compassionate care<br>for the ones you love.</h1>
            <p>Professional nursing and personal support designed to help your family live with dignity, comfort, and peace of mind.</p>
            
            <div style="display: flex; justify-content: center; gap: 1rem;">
                <a href="register.jsp" class="btn btn-primary">Join our family</a>
                <a href="about.jsp" class="btn btn-secondary">Our philosophy</a>
            </div>
        </div>
    </section>


    <!-- Main Container -->
    <section class="main-container">
        <div class="split-container">
            <img src="./Images/coverPage.jpg" alt="Home Caregiving" class="split-image"
                 onerror="this.src='https://images.unsplash.com/photo-1576765608535-5f04d1e3f289?q=80&w=1200';">
            <div class="split-content">
                <h2>Built on trust and connection.</h2>
                <p style="margin-bottom: 1.5rem; color: var(--medium-gray);">
                    At SilverCare Solutions, we don't just provide medical assistance; we build lasting relationships. 
                    Our caregivers are trained to provide support that feels natural, respectful, and warm.
                </p>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 2rem;">
                    <div style="font-weight: 600; color: var(--primary-green-dark);">✓ Licensed Nursing</div>
                    <div style="font-weight: 600; color: var(--primary-green-dark);">✓ 24/7 Availability</div>
                    <div style="font-weight: 600; color: var(--primary-green-dark);">✓ Personalized Plans</div>
                    <div style="font-weight: 600; color: var(--primary-green-dark);">✓ Family Support</div>
                </div>
                <a href="about.jsp" class="btn btn-secondary">Learn more about us</a>
            </div>
        </div>
    </section>

    <!-- Services Overview -->
    <section class="services-section" style="background-color: white;">
        <div class="main-container">
            <div style="text-align: center; max-width: 600px; margin: 0 auto;">
                <h2 style="font-family: var(--font-heading); font-size: 2.5rem; margin-bottom: 1rem;">How we help.</h2>
                <p style="color: var(--medium-gray);">Thoughtful, expert solutions tailored to your unique requirements.</p>
            </div>

            <div class="service-grid">
                <div class="service-card">
                    <span class="service-icon">🏠</span>
                    <h3>Home Nursing</h3>
                    <p>Professional medical assistance, medication management, and health monitoring in your own home.</p>
                </div>
                <div class="service-card">
                    <span class="service-icon">🤝</span>
                    <h3>Companionship</h3>
                    <p>Friendly visits, social outings, and help with daily errands to keep spirits high and active.</p>
                </div>
                <div class="service-card">
                    <span class="service-icon">🧠</span>
                    <h3>Memory Care</h3>
                    <p>Specialized, patient support for seniors living with dementia or Alzheimer's conditions.</p>
                </div>
            </div>
        </div>
    </section>


    <!-- Final Call to Action -->
    <section style="padding: 100px 0; text-align: center;">
        <div class="main-container">
            <h2 style="font-family: var(--font-heading); font-size: 2.5rem; margin-bottom: 1.5rem;">Ready to experience better care?</h2>
            <p style="color: var(--medium-gray); margin-bottom: 2.5rem; max-width: 500px; margin-left: auto; margin-right: auto;">
                Register today to talk to a care advisor and find the perfect match for your family's needs.
            </p>
            <a href="register.jsp" class="btn btn-primary" style="padding: 1rem 3rem;">Register Now</a>
        </div>
    </section>
</main>

<!-- Universal Footer -->
<%@ include file="footer.jsp" %>



