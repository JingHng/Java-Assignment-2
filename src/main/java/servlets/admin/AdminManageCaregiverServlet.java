// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Admin caregiver management servlet following MVC architecture
// Uses CaregiverDAO and ServiceDAO for all database operations

package servlets.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.silvercare.model.Caregiver;
import com.silvercare.model.Service;
import silvercare.dao.CaregiverDAO;
import silvercare.dao.ServiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 50
)
@WebServlet("/AdminManageCaregivers")
public class AdminManageCaregiverServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CaregiverDAO caregiverDAO = new CaregiverDAO();
    private ServiceDAO serviceDAO = new ServiceDAO();

    /**
     * Handle GET requests to display caregiver list or delete a caregiver
     * Shows all caregivers with their assigned services
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if we need to perform a specific action
        String action = request.getParameter("action");

        // Handle caregiver deletion
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            try {
                caregiverDAO.deleteCaregiver(id);
                response.sendRedirect("AdminManageCaregivers?message=Deleted");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("AdminManageCaregivers?error=DeleteFailed");
            }
            return;
        }

        try {
            // Fetch all caregivers with their service IDs from database
            List<Caregiver> caregiverList = caregiverDAO.getAllCaregiversWithServices();
            
            // Fetch available services for assignment (exclude Meal Delivery category)
            // Category 4 is meal delivery which doesn't require caregivers
            List<Service> allServices = serviceDAO.getServicesExcludingCategory(4);
            
            request.setAttribute("caregiverList", caregiverList);
            request.setAttribute("allServices", allServices);
            request.getRequestDispatcher("/ASSIGNMENT/admin/manage_caregivers.jsp").forward(request, response);
            
        } catch (Exception e) { 
            e.printStackTrace();
            response.sendError(500, "Error loading caregivers");
        }
    }

    /**
     * Handle POST requests to add or update caregiver information
     * Processes form submissions including file uploads for profile images
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Extract all form parameters
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String specialty = request.getParameter("specialty");
        String bio = request.getParameter("bio");
        String idStr = request.getParameter("id");
        String[] selectedServiceIds = request.getParameterValues("serviceIds");

        // Handle profile image file upload
        String fileNameInDB = "";
        Part filePart = request.getPart("imageFile"); 
        
        // Process image upload if file was selected
        if (filePart != null && filePart.getSize() > 0) {
            // Extract just the filename (without path)
            String submittedFileName = java.nio.file.Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            // Determine upload directory on server
            String uploadPath = getServletContext().getRealPath("/") + "ASSIGNMENT/Images";
            File uploadDir = new File(uploadPath);
            // Create directory if it doesn't exist
            if (!uploadDir.exists()) uploadDir.mkdirs();
            // Save the file to disk
            filePart.write(uploadPath + File.separator + submittedFileName);
            // Store relative path for database
            fileNameInDB = "ASSIGNMENT/Images/" + submittedFileName; 
        }

        try {
            if ("add".equals(action)) {
                // Add new caregiver to database with service mappings
                caregiverDAO.addCaregiver(name, email, phone, specialty, bio, fileNameInDB, 
                                         username, password, selectedServiceIds);
                                         
            } else if ("edit".equals(action)) {
                // Update existing caregiver information and service mappings
                int cgId = Integer.parseInt(idStr);
                caregiverDAO.updateCaregiver(cgId, name, email, phone, specialty, bio, 
                                            fileNameInDB, selectedServiceIds);
            }
            
            response.sendRedirect("AdminManageCaregivers?message=Success");
            
        } catch (Exception e) { 
            e.printStackTrace();
            response.sendRedirect("AdminManageCaregivers?error=OperationFailed");
        }
    }
}