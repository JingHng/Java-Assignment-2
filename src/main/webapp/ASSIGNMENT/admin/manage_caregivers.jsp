<%-- 
    -- Author(s): Wan Jing Hng 
    -- Date: 31 Jan 2026 
    -- File: manage_caregivers.jsp
    -- Description: Dedicated view for managing caregivers accounts
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ include file="../header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_caregivers.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css"> 


<%-- Page title and action button --%>
<div class="admin-container">
    <div class="page-header">
        <div>
            <h1>Caregiver Management</h1>
            <p>View and manage your healthcare providers.</p>
        </div>
        
        <%-- Opens modal to add a new caregiver --%>        
        <button class="btn btn-primary" onclick="showModal('add')">
            <span class="icon">+</span> Add New Provider
        </button>
    </div>
    
    
    <%-- Caregiver listing table --%>
    <div class="table-card">
        <table>
            <thead>
                <tr>
                    <th>Caregiver</th>
                    <th>Specialty</th>
                    <th>Contact</th>
                    <th style="text-align: right;">Actions</th>
                </tr>
            </thead>
            <tbody>
            
                <%-- Check if caregiver list is available --%>           
                <c:choose>
                    <c:when test="${not empty caregiverList}">
                        <c:forEach var="c" items="${caregiverList}">
                            <tr>
                                <td>
                                    <div class="user-info">
                	
                	                    <%-- Display caregiver profile image --%>                                    
                                        <img src="${pageContext.request.contextPath}/${fn:replace(c.image, '#', '%23')}" 
                                             class="avatar" onerror="this.src='https://placehold.co/100x100?text=CG';">
                                     
                                        <div class="user-details">
                                            <span class="name">${c.name}</span>
                                            <span class="id-badge">ID: #${c.caregiverId}</span>
                                        </div>
                                    </div>
                                </td>
                                
                            
                                <td><span class="pill">${c.specialty}</span></td>
                                <td>
                                    <div class="contact-info">
                                        <span>${c.email}</span>
                                        <small>${c.phone}</small>
                                    </div>
                                </td>
                                
                                
                                <td style="text-align: right;">
                                    <div class="action-btns">
                                    
                                         <%-- Opens edit modal with existing caregiver data --%>                                   
                                        <button class="btn-icon edit" title="Edit" 
                                                onclick="populateEditModal('${c.caregiverId}', '${fn:escapeXml(c.name)}', '${c.email}', '${c.phone}', '${fn:escapeXml(c.specialty)}', '${fn:escapeXml(c.bio)}', '${c.serviceIdList}')">
                                            Edit
                                        </button>
                                        
                                        <%-- Deletes caregiver after confirmation --%>                                        
                                        <a href="AdminManageCaregivers?action=delete&id=${c.caregiverId}" 
                                           class="btn-icon delete" onclick="return confirm('Are you sure?')">Delete</a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    
                    <%-- Display message when no caregivers exist --%>                    
                    <c:otherwise>
                        <tr>
                            <td colspan="4" class="empty-state">
                                No caregivers found. Click "Add New Provider" to start.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</div>


<%-- Modal popup for adding/editing caregivers --%>
<div id="cgModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
        
            <%-- Modal title changes based on action --%>        
            <h2 id="modalTitle">Add Caregiver</h2>
            <button class="close-btn" onclick="closeM()">&times;</button>
        </div>
        
        <%-- Form submits caregiver data to servlet --%>       
        <form action="AdminManageCaregivers" method="POST" enctype="multipart/form-data" class="modal-form">
            <div class="modal-body">
                <input type="hidden" name="action" id="fa" value="add">
                <input type="hidden" name="id" id="cid">
                
                <%-- Basic caregiver information --%>                
                <div class="form-section">
                    <label>Basic Information</label>
                    <div class="form-grid">
                        <div class="field">
                            <input type="text" name="name" id="cn" placeholder="Full Name" required>
                        </div>
                        <div class="field">
                            <input type="text" name="specialty" id="cs" placeholder="Specialty (e.g. Nursing)" required>
                        </div>
                        <div class="field">
                            <input type="email" name="email" id="ce" placeholder="Email Address" required>
                        </div>
                        <div class="field">
                            <input type="text" name="phone" id="cph" placeholder="Phone Number">
                        </div>
                        <div class="field">
                            <input type="text" name="username" id="c_user" placeholder="Login Username" required>
                        </div>
                        <div class="field">
                            <input type="password" name="password" id="c_pass" placeholder="Login Password" required>
                        </div>
                    </div>
                </div>

                <%-- Service assignment section --%>
                <div class="form-section">
                    <label>Assigned Services</label>
                    <p style="font-size: 0.75rem; color: #64748b; margin-bottom: 8px;">
                        <i>Note: Meal delivery (Category 4) is excluded from professional assignment.</i>
                    
                    </p>
                    <%-- List of all available services --%>                    
                    <div class="services-grid">
                        <c:forEach var="service" items="${allServices}">
                            <label class="service-item">
                                <input type="checkbox" name="serviceIds" class="svc-check" value="${service.serviceId}">
                                <span>${service.serviceName}</span>
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <%-- Caregiver profile details --%>
                <div class="form-section">
                    <label>Profile Details</label>
                    <textarea name="bio" id="cb" placeholder="Brief biography..." rows="3"></textarea>
                    <div class="file-input">
                        <span class="file-label">Profile Photo:</span>
                        <input type="file" name="imageFile">
                    </div>
                </div>
            </div>
            
            <%-- Modal action buttons --%>            
            <div class="modal-footer">
                <button type="button" class="btn-secondary" onclick="closeM()">Cancel</button>
                <button type="submit" class="btn-primary">Save Provider</button>
            </div>
        </form>
    </div>
</div>



<script>
	// Reference to caregiver modal
    const modal = document.getElementById('cgModal');

    // Opens modal in add or edit mode
    function showModal(mode) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        document.getElementById('fa').value = mode;
        document.getElementById('modalTitle').innerText = mode === 'add' ? 'Add Caregiver' : 'Edit Caregiver';
        
        // Reset form when adding a new caregiver
        if(mode === 'add') {
            document.querySelector('.modal-form').reset();
            document.querySelectorAll('.svc-check').forEach(cb => cb.checked = false);
        }
    }

    
    // Closes the modal  
    function closeM() { 
        modal.style.display = 'none'; 
        document.body.style.overflow = 'auto'; 
    }

    // Fills modal fields with existing caregiver data for editing    
    function populateEditModal(id, n, e, ph, s, bio, sList) {
        showModal('edit');
        document.getElementById('cid').value = id;
        document.getElementById('cn').value = n;
        document.getElementById('ce').value = e;
        document.getElementById('cph').value = ph;
        document.getElementById('cs').value = s; 
        document.getElementById('cb').value = bio;

        // Mark assigned services as checked
        const checks = document.querySelectorAll('.svc-check');
        const selectedIds = sList ? sList.split(',') : [];
        checks.forEach(cb => {
            cb.checked = selectedIds.includes(cb.value.toString());
        });
    }
    
    // Close modal when clicking outside it    
    window.onclick = function(event) {
        if (event.target == modal) closeM();
    }
</script>

<%@ include file="../footer_admin.jsp" %>