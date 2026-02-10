// Done by: Wan Jing Hng
// Date: 10/2/2026
// Description: Service analytics servlet following MVC architecture
// Uses AnalyticsDAO for all database operations and generates charts with JFreeChart

package servlets.admin;

import silvercare.dao.AnalyticsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

@WebServlet("/ServiceAnalytics")
public class ServiceAnalyticsServlet extends HttpServlet {

    private AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    /**
     * Handle GET requests to display analytics page or generate chart image
     * Supports two modes: show analytics page or return chart as PNG image
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // If action is generateChart, create and stream the chart image directly
        if ("generateChart".equals(action)) {
            generateChartImage(request, response);
        } else {
            // Otherwise, forward to JSP page to display analytics interface
            request.getRequestDispatcher("/ASSIGNMENT/admin/service_analytics.jsp").forward(request, response);
        }
    }
    
    /**
     * Generate bar chart image showing service booking statistics
     * Returns PNG image to be displayed in analytics page
     */
    private void generateChartImage(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Extract date filter parameters (optional)
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");

        // Create dataset for JFreeChart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            // Get analytics data from DAO with date filtering
            Map<String, Integer> analytics = analyticsDAO.getServiceBookingAnalytics(startDate, endDate);
            
            // Populate chart dataset with service names and booking counts
            for (Map.Entry<String, Integer> entry : analytics.entrySet()) {
                dataset.addValue(entry.getValue(), "Total Bookings", entry.getKey());
            }
            
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        // Build dynamic chart title based on date filters
        String chartTitle = "Service Analytics - Bookings by Service";
        if ((startDate != null && !startDate.isEmpty()) || (endDate != null && !endDate.isEmpty())) {
            chartTitle += " (";
            if (startDate != null && !startDate.isEmpty()) {
                chartTitle += "From: " + startDate;
            }
            if (endDate != null && !endDate.isEmpty()) {
                if (startDate != null && !startDate.isEmpty()) chartTitle += " ";
                chartTitle += "To: " + endDate;
            }
            chartTitle += ")";
        }

        // Create the bar chart using JFreeChart library
        JFreeChart barChart = ChartFactory.createBarChart(
            chartTitle,                                   // Dynamic chart title with date range
            "Service",                                    // X-axis label
            "Total Bookings",                            // Y-axis label
            dataset,                                      // Dataset
            PlotOrientation.VERTICAL,                    // Orientation
            true,                                         // Include legend
            true,                                         // Show tooltips on hover
            false                                         // No URLs
        );

        // Customize chart appearance for better visualization
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Set bar color to emerald green
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(16, 185, 129)); // Emerald green (#10b981)
        
        // Configure Y-axis to show only integers (no decimals)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        // Rotate X-axis labels 45 degrees to prevent overlapping
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Set response content type to PNG image
        response.setContentType("image/png");
        
        // Write chart as PNG image to response output stream
        OutputStream out = response.getOutputStream();
        ChartUtils.writeChartAsPNG(out, barChart, 1200, 500);  // 1200x500 pixels
        out.close();
    }
}