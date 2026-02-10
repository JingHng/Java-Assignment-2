<%-- 
    Author(s): Wan Jing Hng
    Date: 18 Janurary 2026
    File: view_invoice.jsp
    Description: Invoice/receipt page for SilverCare Logistics; displays order details, verification status, and payment info.
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Invoice - ${invoice.invoiceId}</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/view_invoice.css">
</head>
<body>

    <!-- Main wrapper for the invoice -->
    <div class="invoice-wrapper">
        <div class="invoice-box shadow-sm">
            
            <div class="d-flex justify-content-between align-items-start mb-5">
                <div class="invoice-header">
                    <h2>Care <span class="invoice-id-accent">Receipt</span></h2>
                    <p class="text-muted small">Standard Service Record</p>
                </div>
                <div class="text-end invoice-meta-text">
                    <strong>Receipt ID:</strong> ${invoice.invoiceId}<br>
                    <strong>Timestamp:</strong> ${invoice.timestamp}
                </div>
            </div>


            <!-- Table showing order item details -->
            <div class="table-responsive">
                <table class="table invoice-table">
                    <thead>
                        <tr>
                            <th>Item Description</th>
                            <th>Reference</th>
                            <th class="text-end">Verification</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td class="fw-bold">${invoice.foodItem}</td>
                            <td>#${invoice.orderId}</td>
                            <td class="text-end">
                                <span class="status-pill-done">Verified</span>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <!-- Payment status summary -->
            <div class="d-flex justify-content-end mt-4">
                <div class="text-end">
                    <p class="text-muted mb-0 small">Payment Status</p>
                    <h4 class="fw-bold" style="color: #27ae60;">Paid</h4>
                </div>
            </div>

            <div class="mt-5 pt-4 border-top text-center d-print-none">
                <button onclick="window.print()" class="btn-invoice-primary me-2">
                    Print Document
                </button>
                <a href="ManageDelivery" class="btn-invoice-outline">
                    Return to Main Page
                </a>
            </div>
        </div>
    </div>

    <!-- Include footer matching SilverCare delivery portal -->
    <%@ include file="../footer_delivery.jsp" %>

</body>
</html>