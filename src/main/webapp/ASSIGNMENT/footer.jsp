<%-- 
    -- Author(s):Wan Jing Hng
    -- Date: 18 Nov 2025 
    -- File: footer.jsp
    -- Description: Footer matching EldereeLife exactly with green theme
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link rel="stylesheet" href=".Css/footer.css">
<%-- Link local CSS --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/ASSIGNMENT/Css/footer.css">
<%-- Link context path CSS --%>


<%-- Main footer start --%>
<footer class="main-footer">
    <div class="footer-container">
        <%-- Top footer grid (links) --%>
        <div class="footer-grid">
            <div class="footer-column">
                <div class="footer-logo">
                    <span>SilverCare</span>
                </div>
                <%-- Mission statement --%>
                <p class="footer-description">
                    At SilverCare, we are committed to providing compassionate, professional, and trustworthy elderly care. 
                    Our mission is to ensure every senior feels safe, respected, and valued while enjoying the comfort of a 
                    supportive environment. Together, we create a place where care meets dignity.
                </p>
            </div>
            
            <div class="footer-column">
                <h3>Quick Links</h3>
                <%-- Main navigation links --%>
                <ul class="footer-links">
                    <li><a href="index.jsp">Home</a></li>
                    <li><a href="about.jsp">About Us</a></li>
                    <li><a href="services.jsp">Services</a></li>
                    <li><a href="caregivers.jsp">Caregivers</a></li>
                </ul>
            </div>
            
            <div class="footer-column">
                <h3>Support</h3>
                <%-- Auth links --%>
                <ul class="footer-links">
                    <li><a href="login.jsp">Login</a></li>
                    <li><a href="register.jsp">Register</a></li>
            </div>
            
            <div class="footer-column">
                <h3>Legal</h3>
                <%-- Legal/Info links --%>
                <ul class="footer-links">
                    <li><a href="terms.jsp">Terms & Conditions</a></li>
                    <li><a href="faq.jsp">FAQ</a></li>
                </ul>
            </div>
        </div>
        
        <%-- Bottom footer section --%>
        <div class="footer-bottom">
            <div class="footer-disclaimer">
                <h4>Disclaimer and Footnotes</h4>
                <%-- Project disclaimer and notes --%>
                <ol class="disclaimer-list">
                    <li>The information on this website is for general purposes only and is not medical or professional advice.</li>
                    <li>This website is an educational project developed for JAD (Java Application Development) Assignment at Singapore Polytechnic. 
                    It demonstrates full-stack web development capabilities including user authentication, role-based access control, 
                    database integration, and responsive design principles.</li>
                </ol>
            </div>
            
            <div class="footer-right">
                <div class="footer-copyright">
                    <%-- Dynamic year for copyright --%>
                    © <%= java.time.Year.now().getValue() %> Jing Hng & Owen. All Rights Reserved.
                </div>
                <%-- School and contact info --%>
                <div class="footer-contact">
                    Singapore Polytechnic | School of Computing<br>
                    <a href="mailto:computing@sp.edu.sg">computing@sp.edu.sg</a> | +65 6775 1133
                </div>
                <%-- Bottom-most links --%>
                <div class="footer-bottom-links">
                    <a href="terms.jsp">Terms & conditions</a>
                </div>
            </div>
        </div>
    </div>
</footer>

</body>
</html>