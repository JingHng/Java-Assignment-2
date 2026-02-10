<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Nov 2025
    File: faq.jsp
    Description: FAQ page with accordion dropdowns
--%>
	
  	<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

	<%-- Import model class --%>
	<%@ page import="com.silvercare.model.CartItem" %>

	<%-- Include common header (navigation, logo, etc.) --%>
	<%@ include file="header.jsp" %>

	<%-- Link to FAQ-specific stylesheet --%>
	<link rel="stylesheet" href="./Css/faq.css">

	<%-- FAQ Hero Section --%>
	<div class="faq-hero">
    	<p class="faq-hero-subtitle">Frequently Asked Questions</p>
    		<h1>SilverCare FAQ</h1>
		</div>

	<%-- Main FAQ Container --%>
	<div class="faq-container">
    	<p class="faq-intro">
        Find answers to common questions about our services, pricing, caregivers, and more. 
        Click on any question to expand the answer.
    </p>

    <%-- ===================== General Questions Category ===================== --%>
    <div class="faq-category">
        <h2 class="category-title">General Questions</h2>
        
        <%-- Individual FAQ Item --%>
        <div class="faq-item">
            <button class="faq-question" onclick="toggleFAQ(this)">
                <span>What is SilverCare Solutions?</span>
                <span class="faq-icon">+</span>
            </button>
            <div class="faq-answer">
                <div class="faq-answer-content">
                    <p>
                        SilverCare Solutions is a comprehensive elderly care service provider offering professional,
                        reliable, and personalized care services for seniors.
                    </p>
                </div>
            </div>
        </div>

        <%-- Another FAQ Item --%>
        <div class="faq-item">
            <button class="faq-question" onclick="toggleFAQ(this)">
                <span>What are your operating hours?</span>
                <span class="faq-icon">+</span>
            </button>
            <div class="faq-answer">
                <div class="faq-answer-content">
                    <p>
                        We provide 24/7 care services. Our administrative offices are open from 9 AM to 5 PM, Monday through Friday.
                    </p>
                </div>
            </div>
        </div>

        <%-- Another FAQ Item --%>
        <div class="faq-item">
            <button class="faq-question" onclick="toggleFAQ(this)">
                <span>What areas do you serve?</span>
                <span class="faq-icon">+</span>
            </button>
            <div class="faq-answer">
                <div class="faq-answer-content">
                    <p>
                        SilverCare Solutions currently serves all major areas in Singapore.
                    </p>
                </div>
            </div>
        </div>
    </div>

    <%-- ===================== Services & Pricing Category ===================== --%>
    <div class="faq-category">
        <h2 class="category-title">Services & Pricing</h2>
        
        <div class="faq-item">
            <button class="faq-question" onclick="toggleFAQ(this)">
                <span>What services do you offer?</span>
                <span class="faq-icon">+</span>
            </button>
            <div class="faq-answer">
                <div class="faq-answer-content">
                    <p>We offer a wide range of elderly care services, including:</p>
                    <ul>
                        <li>Personal Care</li>
                        <li>Medical Care</li>
                        <li>Companionship Services</li>
                        <li>Meal Preparation</li>
                        <li>Light Housekeeping</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="faq-item">
            <button class="faq-question" onclick="toggleFAQ(this)">
                <span>How much do your services cost?</span>
                <span class="faq-icon">+</span>
            </button>
            <div class="faq-answer">
                <div class="faq-answer-content">
                    <p>
                        Pricing varies based on service type, duration, and level of care required.
                    </p>
                </div>
            </div>
        </div>
    </div>

    <%-- ===================== Contact Information Section ===================== --%>
    <div class="contact-box">
        <h3>Still Have Questions?</h3>
        <p>If you couldn't find the answer you were looking for, please contact us.</p>
        <p><strong>Email:</strong> computing@sp.edu.sg</p>
        <p><strong>Phone:</strong> +65 6775 1133</p>
    </div>
</div>

<%-- JavaScript for FAQ accordion behavior --%>
<script>
    // Function to toggle FAQ open/close
    function toggleFAQ(button) {
        const faqItem = button.parentElement; // Get the clicked FAQ item
        const answer = faqItem.querySelector('.faq-answer'); // Get its answer section
        const isActive = faqItem.classList.contains('active'); // Check if already open
        
        // Close all other FAQ items
        document.querySelectorAll('.faq-item').forEach(item => {
            if (item !== faqItem) {
                item.classList.remove('active');
                item.querySelector('.faq-answer').style.maxHeight = null;
            }
        });
        
        // Toggle the selected FAQ item
        if (isActive) {
            faqItem.classList.remove('active');
            answer.style.maxHeight = null;
        } else {
            faqItem.classList.add('active');
            answer.style.maxHeight = answer.scrollHeight + 'px';
        }
    }
</script>

<%-- Include common footer --%>
<%@ include file="footer.jsp" %>
	