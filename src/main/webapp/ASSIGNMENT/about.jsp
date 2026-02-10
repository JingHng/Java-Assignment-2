<%-- 
    Author(s): Wan Jing Hng 
    Date: 18 Nov 2025 
    File: about.jsp 
    Description: Completely redesigned About Us page with new layout 
--%>

<%@ page import="com.silvercare.model.CartItem" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - SilverCare Solutions</title>
    <link rel="stylesheet" href="./Css/about.css">
</head>

<body>
    <%-- Include common header --%>
    <%@ include file="./header.jsp" %>

    <%--===================== Hero Section =====================--%>
    <section class="about-hero">
        <div class="about-hero-content">
            <h1>Caring for Singapore's Seniors</h1>
            <p>
                At SilverCare, we're dedicated to providing compassionate, professional care
                that honors dignity and celebrates life's golden years.
            </p>
        </div>
    </section>

    <%--===================== Main Content Container =====================--%>
    <div class="about-container">

        <%--===================== Our Story Section =====================--%>
        <section class="story-section">
            <div class="story-content">
                <h2>Our Story</h2>
                <p>
                    Founded in 2010, SilverCare Solutions was born from a simple belief:
                    every senior deserves to live with dignity, independence, and joy.
                </p>
                <p>
                    Over the years, we have grown while continuously improving our care services
                    to meet the needs of Singapore's aging population.
                </p>
                <p>
                    Today, we combine compassionate care with modern practices to support
                    seniors and their families.
                </p>
            </div>

            <div>
                <img src="./Images/AssistedLivingFacility.jpg" alt="SilverCare Facility" class="story-image"
                    onerror="this.onerror=null; this.src='https://placehold.co/600x400/7CB342/ffffff?text=Our+Story';">
            </div>
        </section>

        <%--===================== Core Values Section =====================--%>
        <section class="values-section">
            <div class="values-header">
                <h2>Our Core Values</h2>
                <p>The principles that guide everything we do</p>
            </div>

            <div class="values-grid">
                <div class="value-item">
                    <div class="value-icon">❤️</div>
                    <h3>Compassion First</h3>
                    <p>Providing care with empathy and understanding.</p>
                </div>

                <div class="value-item">
                    <div class="value-icon">🤝</div>
                    <h3>Trust & Integrity</h3>
                    <p>Building strong relationships through honesty and transparency.</p>
                </div>

                <div class="value-item">
                    <div class="value-icon">⭐</div>
                    <h3>Excellence Always</h3>
                    <p>Maintaining high standards in all aspects of care.</p>
                </div>

                <div class="value-item">
                    <div class="value-icon">👨‍👩‍👧‍👦</div>
                    <h3>Family-Centered</h3>
                    <p>Supporting both seniors and their families.</p>
                </div>

                <div class="value-item">
                    <div class="value-icon">🌱</div>
                    <h3>Holistic Approach</h3>
                    <p>Focusing on physical, emotional, and social well-being.</p>
                </div>

                <div class="value-item">
                    <div class="value-icon">🛡️</div>
                    <h3>Safety First</h3>
                    <p>Ensuring safety through strict protocols and trained staff.</p>
                </div>
            </div>
        </section>

        <%--===================== Team Section =====================--%>
        <section class="team-section">
            <div class="team-header">
                <h2>Meet Our Leadership</h2>
                <p>Experienced professionals leading our care services</p>
            </div>

            <div class="team-grid">
                <div class="team-member">
                    <img src="./Images/JaneDoh.jpg" alt="Jane Doe" class="team-avatar"
                        onerror="this.onerror=null; this.src='https://placehold.co/200x200/7CB342/ffffff?text=JD';">
                    <h4>Jane Doe</h4>
                    <p class="role">Founder & CEO</p>
                </div>

                <div class="team-member">
                    <img src="./Images/michaelChen.webp" alt="Michael Chen" class="team-avatar"
                        onerror="this.onerror=null; this.src='https://placehold.co/200x200/7CB342/ffffff?text=MC';">
                    <h4>Michael Chen</h4>
                    <p class="role">Medical Director</p>
                </div>

                <div class="team-member">
                    <img src="./Images/AishaKhan.webp" alt="Aisha Khan" class="team-avatar"
                        onerror="this.onerror=null; this.src='https://placehold.co/200x200/7CB342/ffffff?text=AK';">
                    <h4>Aisha Khan</h4>
                    <p class="role">Care Coordinator</p>
                </div>

                <div class="team-member">
                    <img src="./Images/robertLee.avif" alt="Robert Lee" class="team-avatar"
                        onerror="this.onerror=null; this.src='https://placehold.co/200x200/7CB342/ffffff?text=RL';">
                    <h4>Robert Lee</h4>
                    <p class="role">Operations Manager</p>
                </div>
            </div>
        </section>

        <%--===================== Statistics Banner =====================--%>
        <section class="stats-banner">
            <div class="stats-grid">
                <div class="stat-box">
                    <div class="stat-number">15+</div>
                    <div class="stat-label">Years Serving</div>
                </div>
                <div class="stat-box">
                    <div class="stat-number">3,200+</div>
                    <div class="stat-label">Families Helped</div>
                </div>
                <div class="stat-box">
                    <div class="stat-number">98%</div>
                    <div class="stat-label">Satisfaction Rate</div>
                </div>
                <div class="stat-box">
                    <div class="stat-number">60+</div>
                    <div class="stat-label">Care Professionals</div>
                </div>
            </div>
        </section>

        <%--===================== Call-To-Action Section =====================--%>
        <section class="cta-section">
            <h2>Ready to Experience the SilverCare Difference?</h2>
            <p>
                Join thousands of families who trust us for compassionate,
                professional elderly care.
            </p>

            <div class="cta-buttons">
                <a href="services.jsp" class="btn btn-primary">Explore Services</a>
                <a href="caregivers.jsp" class="btn btn-secondary">Meet Our Caregivers</a>
            </div>
        </section>
    </div>

    <%-- Include common footer --%>
    <%@ include file="footer.jsp" %>
</body>

</html>