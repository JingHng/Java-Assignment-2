// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin services management servlet following MVC architecture
// Uses ServiceDAO and FoodDAO for all database operations

package servlets.admin;
import java.io.File;

import silvercare.dao.ServiceDAO;
import silvercare.dao.FoodDAO;
import com.silvercare.model.Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import jakarta.servlet.annotation.MultipartConfig;

@WebServlet("/AdminManageServices")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class AdminManageServicesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handle GET requests to display services list or handle AJAX requests
     * Supports AJAX endpoint for fetching food options by service ID
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        // 1. Security Check: Verify user is logged in as admin
        boolean isValidAdmin = (session != null && 
                                Boolean.TRUE.equals(session.getAttribute("isLoggedIn")) &&
                                "admin".equalsIgnoreCase((String)session.getAttribute("userRole")));

        if (!isValidAdmin) { 
            response.sendRedirect(contextPath + "/ASSIGNMENT/login.jsp?message=Unauthorized");
            return; 
        }

        // 2. Route delete action from GET to POST handler
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            doPost(request, response);
            return;
        }
        
        // 3. Handle AJAX request to fetch food options for a specific service
        if ("getFoodOptions".equals(action)) {
            String serviceIdStr = request.getParameter("serviceId");
            if (serviceIdStr != null && !serviceIdStr.isEmpty()) {
                FoodDAO foodDao = new FoodDAO();
                // Fetch all food options for the requested service
                List<Map<String, Object>> foods = foodDao.getAllFoodOptionsByService(Integer.parseInt(serviceIdStr));
                
                // Build and return JSON response manually
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < foods.size(); i++) {
                    Map<String, Object> food = foods.get(i);
                    if (i > 0) json.append(",");
                    json.append("{")
                        .append("\"optionId\":").append(food.get("optionId")).append(",")
                        .append("\"optionName\":\"").append(food.get("optionName")).append("\",")
                        .append("\"calories\":").append(food.get("calories")).append(",")
                        .append("\"isAvailable\":").append(food.get("isAvailable"))
                        .append("}");
                }
                json.append("]");
                response.getWriter().write(json.toString());
                return;
            }
        }

        // 4. Default action: Fetch and display all services and categories
        try {
            ServiceDAO dao = new ServiceDAO();
            // Get all services from database
            List<Service> list = dao.getAllServices();
            // Get all service categories for dropdown
            List<Map<String, Object>> categories = dao.getAllCategories();
            
            request.setAttribute("serviceList", list);
            request.setAttribute("categoryList", categories);
            
            request.getRequestDispatcher("ASSIGNMENT/admin/manage_services.jsp").forward(request, response);            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/AdminDashboard?error=load_failed");
        }
    }

    /**
     * Handle POST requests to add, edit, delete services or manage food options
     * Supports CRUD operations for both services and their associated food items
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        ServiceDAO dao = new ServiceDAO();
        FoodDAO foodDao = new FoodDAO();
        String message = "";

        try {
            if ("delete".equals(action)) {
                // DELETE SERVICE: Remove service from database
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.isEmpty()) {
                    dao.deleteService(Integer.parseInt(idStr));
                    message = "Service deleted successfully.";
                }
            } else if ("add".equals(action) || "edit".equals(action)) {
                // ADD/EDIT SERVICE: Create new or update existing service
                Service s = new Service();
                // Set service ID only for edit action
                if ("edit".equals(action)) {
                    s.setServiceId(Integer.parseInt(request.getParameter("id")));
                }
                // Populate service object from form data
                s.setServiceName(request.getParameter("serviceName"));
                s.setDescription(request.getParameter("description"));
                s.setPrice(Double.parseDouble(request.getParameter("price")));
                s.setCategoryId(Integer.parseInt(request.getParameter("categoryID")));
                
                // Handle image upload
                String imagePath = "Images/default.jpg"; // Default
                Part filePart = request.getPart("serviceImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "ASSIGNMENT" + File.separator + "Images";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();
                    
                    String savedFileName = System.currentTimeMillis() + "_" + fileName;
                    File file = new File(uploadPath + File.separator + savedFileName);
                    
                    try (InputStream input = filePart.getInputStream()) {
                        Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imagePath = "Images/" + savedFileName;
                    }
                } else if ("edit".equals(action)) {
                    // Keep existing image for edit if no new image uploaded
                    Service existing = dao.getServiceById(s.getServiceId());
                    if (existing != null) {
                        imagePath = existing.getImageLocation();
                    }
                }
                s.setImageLocation(imagePath); 

                if ("add".equals(action)) {
                    int newServiceId = dao.addService(s);
                    message = "Service added successfully.";
                    response.sendRedirect("AdminManageServices?message=" + URLEncoder.encode(message, "UTF-8") + "&newServiceId=" + newServiceId);
                    return;
                } else {
                    dao.updateService(s);
                    message = "Service updated successfully.";
                }
            } else if ("addFood".equals(action)) {
                // ADD FOOD OPTION: Add new meal option to a service
                int serviceId = Integer.parseInt(request.getParameter("serviceId"));
                String foodName = request.getParameter("foodName");
                int calories = Integer.parseInt(request.getParameter("calories"));
                
                if (foodDao.addFoodOption(serviceId, foodName, calories)) {
                    message = "Food option added successfully.";
                } else {
                    message = "Failed to add food option.";
                }
            } else if ("deleteFood".equals(action)) {
                // DELETE FOOD OPTION: Soft delete meal option (set to unavailable)
                int optionId = Integer.parseInt(request.getParameter("optionId"));
                
                if (foodDao.deleteFoodOption(optionId)) {
                    message = "Food option removed successfully.";
                } else {
                    message = "Failed to remove food option.";
                }
            } else if ("restoreFood".equals(action)) {
                // RESTORE FOOD OPTION: Reactivate previously deleted meal option
                int optionId = Integer.parseInt(request.getParameter("optionId"));
                
                if (foodDao.restoreFoodOption(optionId)) {
                    message = "Food option restored successfully.";
                } else {
                    message = "Failed to restore food option.";
                }
            }
            response.sendRedirect("AdminManageServices?message=" + URLEncoder.encode(message, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage() != null ? e.getMessage() : "action_failed";
            System.err.println("AdminManageServices error: " + errorMsg);
            response.sendRedirect("AdminManageServices?error=" + URLEncoder.encode(errorMsg, "UTF-8"));
        }
    }
}