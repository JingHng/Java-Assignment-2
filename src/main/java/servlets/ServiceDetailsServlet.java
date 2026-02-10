//Done by: Wan Jing Hng
//Date: 23/1/2026
//Description: Servlet configuration to allow customers view services that we offer (Controller Logic)
// We use a servlet so that Java logic is not mixed with HTML (JSP)

package servlets;

import silvercare.dao.FoodDAO;
import silvercare.dao.ServiceDAO;
import com.silvercare.model.Service;
import com.silvercare.model.FoodOption;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/ServiceDetails")
public class ServiceDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("ViewServices");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            ServiceDAO serviceDao = new ServiceDAO();
            
            // 1. Fetch the main Service Details
            Service service = serviceDao.getServiceById(id);

            if (service != null) {
                // 2. Fetch Available Caregivers via ServiceDAO
                List<Map<String, Object>> caregivers = serviceDao.getCaregiversByService(id);
                
                // 3. Fetch Food Options via FoodDAO (only if Category ID is 4)
                if (service.getCategoryId() == 4) {
                    FoodDAO foodDao = new FoodDAO(); 
                    List<FoodOption> foodOptions = foodDao.getFoodOptionsByService(id);
                    request.setAttribute("foodOptionsList", foodOptions);
                }
                
                // 4. Pass everything to the JSP
                request.setAttribute("service", service);
                request.setAttribute("caregiverList", caregivers);
                
                request.getRequestDispatcher("ASSIGNMENT/serviceDetails.jsp").forward(request, response);
            } else {
                response.sendRedirect("ViewServices?error=not_found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("ViewServices?error=invalid_id");
        }
    }
}