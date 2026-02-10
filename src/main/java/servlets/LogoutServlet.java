//Done by: Wan Jing Hng
//Date: 22/1/2026
//Description: This servlet handles secure logout by invalidating the session.

package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        //Get the current session (do not create a new one)
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            //Clear session data and kill the session
            session.invalidate(); 
        }
        
        //Redirect to login page with a success message
        String contextPath = request.getContextPath();
        String message = "You have been logged out successfully.";
        response.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=" + URLEncoder.encode(message, "UTF-8"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}