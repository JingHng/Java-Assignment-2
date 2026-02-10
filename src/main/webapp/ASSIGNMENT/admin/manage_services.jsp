<%-- 
    Author: Wan Jing Hng
    Date: 10 Feb 2026
    File: manage_services.jsp
    Description: Allow Admins to manage services, also includes different kinds of services
--%>


<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="../header.jsp" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/manage_services.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/ASSIGNMENT/Css/admin_footer.css">

<div class="admin-container">
    <header class="page-header">
    
    
        <div>
            <h1>Service Management</h1>
            <p>Maintain the healthcare service catalog and pricing details.</p>
        </div>
        
        <%-- Button to add new service --%>        
        <button onclick="showModal('add')" class="btn-primary">+ Add New Service</button>
    </header>
    
    <%-- Success message after action --%>
    <c:if test="${not empty param.message}">
        <div class="pill" style="margin-bottom: 20px; display: inline-block;">✓ ${param.message}</div>
    </c:if>


    <%-- Services table --%>
    <div class="table-card">
        <table>
            <thead>
                <tr>
                    <th>Service Info</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th style="text-align: right;">Actions</th>
                </tr>
            </thead>
                        
            <tbody>
                <c:forEach var="s" items="${serviceList}">
                    <tr>
                        <td>
                            <div class="user-info">
                                <div class="id-badge">#${s.serviceId}</div>
                                <div class="user-details">
                                    <span class="name">${s.serviceName}</span>
                                </div>
                            </div>
                        </td>
                        <td><span class="pill">${s.categoryName}</span></td>
                        <td><strong style="color: var(--text-main)">$${s.price}</strong></td>
                        <td style="text-align: right;">
                         
                            <%-- Edit Button --%>                        
                            <button onclick="populateEdit('${s.serviceId}', '${fn:escapeXml(s.serviceName)}', '${fn:escapeXml(s.description)}', '${s.price}', '${s.categoryId}', '${s.imageLocation}')" class="btn-icon edit">Edit</button>
                           
    						<%-- Cancel Button --%>                           
                            <a href="${pageContext.request.contextPath}/AdminManageServices?action=delete&id=${s.serviceId}" 
                               class="btn-icon delete" onclick="return confirm('Delete this service?')">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        
        <%-- Show empty state if no services --%>        
        <c:if test="${empty serviceList}">
            <div class="empty-state">No services found in the catalog.</div>
        </c:if>
    </div>
</div>

<%-- Modal for Adding / Editing Services --%>
<div id="svcModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2 id="modalTitle">Service Detail</h2>
            <button class="close-btn" onclick="closeModal()">&times;</button>
        </div>
        
        
        <form action="AdminManageServices" method="POST" enctype="multipart/form-data" id="serviceForm">
            <div class="modal-body">
                <input type="hidden" name="action" id="modalAction" value="add">
                <input type="hidden" name="id" id="modalId">
                
                <%-- Service Name Input --%>                
                <div class="form-section">
                    <label>Service Name</label>
                    <div class="field">
                        <input type="text" name="serviceName" id="modalName" required placeholder="Enter service name">
                    </div>
                </div>

                <%-- Description Input --%>
                <div class="form-section">
                    <label>Description</label>
                    <textarea name="description" id="modalDesc" rows="3" placeholder="Describe the service..."></textarea>
                </div>

                <%-- Image Upload --%>
                <div class="form-section">
                    <label>Service Image</label>
                    <div class="field">
                        <input type="file" name="serviceImage" id="modalImage" accept="image/*" onchange="previewImage(event)"
                               style="width:100%; padding:8px; border:1px solid var(--border); border-radius:8px;">
                        <div id="imagePreview" style="margin-top: 10px; display: none;">
                            <img id="previewImg" src="" alt="Preview" style="max-width: 200px; max-height: 150px; border-radius: 8px; border: 2px solid var(--border);">
                        </div>
                    </div>
                </div>

                <%-- Price Input --%>
                <div class="form-grid">
                    <div class="form-section">
                        <label>Price ($)</label>
                        <div class="field">
                            <input type="number" step="0.01" name="price" id="modalPrice" required>
                        </div>
                    </div>
                    
                    
                <%-- Category Input --%>                    
                    <div class="form-section">
                        <label>Category</label>
                        <div class="field">
                            <select name="categoryID" id="modalCat" required onchange="toggleFoodSection()"
                                    style="width:100%; padding:12px; border:1px solid var(--border); border-radius:10px;">
                                <option value="" disabled selected>Select...</option>
                                <c:forEach var="cat" items="${categoryList}">
                                    <option value="${cat.id}">${cat.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
                
                
                <!-- Food Options Section (Only for Category 4) -->
                <div id="foodSection" class="food-section" style="display: none;">
                    <div class="section-divider"></div>
                    <h3 style="margin: 20px 0 10px; color: var(--text-main); font-size: 1.1rem;">
                        🍽️ Food Options Manager
                        <span id="foodCount" style="font-size: 0.85rem; color: var(--text-muted); font-weight: normal;"></span>
                    </h3>
                    
                    <div class="food-manager-container" style="background: #f8f9fa; border-radius: 12px; padding: 20px; margin-bottom: 15px;">
                        <!-- Add New Food Form -->
                        <div class="add-food-header" style="display: flex; gap: 10px; align-items: end; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 2px solid #e0e0e0;">
                            <div style="flex: 2;">
                                <label style="display: block; margin-bottom: 5px; font-size: 0.85rem; font-weight: 600; color: var(--text-main);">Food Item Name</label>
                                <input type="text" id="newFoodName" placeholder="e.g., Grilled Chicken Salad" 
                                       style="width:100%; padding:10px 12px; border:2px solid #ddd; border-radius:8px; font-size: 0.95rem;" 
                                       onkeypress="if(event.key==='Enter'){addFoodOption();}">
                            </div>
                            <div style="flex: 1;">
                                <label style="display: block; margin-bottom: 5px; font-size: 0.85rem; font-weight: 600; color: var(--text-main);">Calories</label>
                                <input type="number" id="newFoodCalories" placeholder="0" min="0" value="0"
                                       style="width:100%; padding:10px 12px; border:2px solid #ddd; border-radius:8px; font-size: 0.95rem;"
                                       onkeypress="if(event.key==='Enter'){event.preventDefault(); addFoodOption(event);}">
                            </div>
                            <button type="button" onclick="addFoodOption(event)" class="btn-primary" style="padding: 10px 24px; height: 42px; white-space: nowrap;">
                                <span style="font-size: 1.1rem;">+</span> Add Item
                            </button>
                        </div>
                        
                        <!-- Existing Foods Table -->
                        <div id="foodList" class="food-list">
                            <div style="text-align: center; padding: 30px; color: var(--text-muted);">
                                <p style="margin: 0; font-size: 0.95rem;">💡 Save the service first, then manage food options here</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <%-- Modal Footer Buttons --%>            
            <div class="modal-footer">
                <button type="button" onclick="closeModal()" class="btn-secondary">Cancel</button>
                <button type="submit" class="btn-primary">Save Service</button>
            </div>
        </form>
    </div>
</div>

<script>
    let currentServiceId = null;
    
    // Show Add/Edit Service Modal
    function showModal(mode) {
        document.getElementById('svcModal').style.display = 'flex';
        document.getElementById('modalAction').value = mode;
        document.getElementById('modalTitle').innerText = (mode === 'add' ? 'Add New Service' : 'Edit Service');
        currentServiceId = null;
        
        const modalContent = document.querySelector('.modal-content');
        
        if(mode === 'add') {
            document.getElementById('modalId').value = '';
            document.getElementById('modalName').value = '';
            document.getElementById('modalDesc').value = '';
            document.getElementById('modalPrice').value = '';
            document.getElementById('modalCat').value = '';
            document.getElementById('newFoodName').value = '';
            document.getElementById('newFoodCalories').value = '0';
            document.getElementById('foodCount').textContent = '';
            document.getElementById('foodList').innerHTML = `
                <div style="text-align: center; padding: 30px; color: var(--text-muted); background: white; border-radius: 8px;">
                    <p style="margin: 0; font-size: 0.95rem;">💡 Save the service first, then manage food options here</p>
                </div>
            `;
            // Reset to narrow modal for new services
            modalContent.classList.remove('wide');
        }
        // Clear image input and preview
        document.getElementById('modalImage').value = '';
        document.getElementById('imagePreview').style.display = 'none';
        document.getElementById('previewImg').src = '';
        
        toggleFoodSection();
        
        // Scroll modal body to top
        const modalBody = document.querySelector('.modal-body');
        if (modalBody) modalBody.scrollTop = 0;
    }
    
    
    
    
    // Close Modal
    function closeModal() { 
        document.getElementById('svcModal').style.display = 'none'; 
        currentServiceId = null;
        // Clear image input and preview
        document.getElementById('modalImage').value = '';
        document.getElementById('imagePreview').style.display = 'none';
        document.getElementById('previewImg').src = '';
    }
    
    // Preview uploaded image
    function previewImage(event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                document.getElementById('previewImg').src = e.target.result;
                document.getElementById('imagePreview').style.display = 'block';
            };
            reader.readAsDataURL(file);
        } else {
            document.getElementById('imagePreview').style.display = 'none';
            document.getElementById('previewImg').src = '';
        }
    }
    
    
    
    
    // Populate modal fields for editing    
    function populateEdit(id, name, desc, price, cat, imageLocation) {
        showModal('edit');
        document.getElementById('modalId').value = id;
        document.getElementById('modalName').value = name;
        document.getElementById('modalDesc').value = desc;
        document.getElementById('modalPrice').value = price;
        document.getElementById('modalCat').value = cat;
        currentServiceId = id;
        
        // Show current image if exists
        if (imageLocation && imageLocation !== 'Images/default.jpg') {
            const contextPath = '${pageContext.request.contextPath}';
            document.getElementById('previewImg').src = contextPath + '/ASSIGNMENT/' + imageLocation;
            document.getElementById('imagePreview').style.display = 'block';
        } else {
            document.getElementById('imagePreview').style.display = 'none';
        }
        
        // Clear food form fields
        document.getElementById('newFoodName').value = '';
        document.getElementById('newFoodCalories').value = '0';
        
        const modalContent = document.querySelector('.modal-content');
        
        toggleFoodSection();
        
        // Load food options if category is 4
        if (cat == 4) {
            modalContent.classList.add('wide');
            setTimeout(() => loadFoodOptions(id), 100);
        } else {
            modalContent.classList.remove('wide');
        }
    }
    
    
    
    // Toggle Food Options section based on category    
    function toggleFoodSection() {
        const categoryId = document.getElementById('modalCat').value;
        const foodSection = document.getElementById('foodSection');
        const modalContent = document.querySelector('.modal-content');
        
        if (categoryId == 4) {
            foodSection.style.display = 'block';
            modalContent.classList.add('wide');
        } else {
            foodSection.style.display = 'none';
            modalContent.classList.remove('wide');
        }
    }
    
    
    
    // Load Food Options from backend via fetch    
    function loadFoodOptions(serviceId) {
        // Show loading state
        const foodList = document.getElementById('foodList');
        foodList.innerHTML = `
            <div style="text-align: center; padding: 40px; color: var(--text-muted); background: white; border-radius: 8px;">
                <div style="font-size: 2rem; margin-bottom: 10px;">⏳</div>
                <p style="margin: 0; font-size: 0.95rem;">Loading food options...</p>
            </div>
        `;
        
        fetch('AdminManageServices?action=getFoodOptions&serviceId=' + serviceId)
            .then(response => response.json())
            .then(foods => {
                const foodCount = document.getElementById('foodCount');
                
                const availableFoods = foods.filter(f => f.isAvailable);
                foodCount.textContent = '(' + availableFoods.length + ' active item' + (availableFoods.length !== 1 ? 's' : '') + ')';
                
                if (foods.length === 0) {
                    foodList.innerHTML = `
                        <div style="text-align: center; padding: 40px; color: var(--text-muted); background: white; border-radius: 8px;">
                            <div style="font-size: 2.5rem; margin-bottom: 10px;">🍽️</div>
                            <p style="margin: 0; font-size: 1rem; font-weight: 500;">No food items yet</p>
                            <p style="margin: 5px 0 0; font-size: 0.9rem;">Add your first food item above</p>
                        </div>
                    `;
                } else {
                    let html = `
                        <div style="background: white; border-radius: 8px; overflow: hidden; border: 1px solid #e0e0e0;">
                            <table style="width: 100%; border-collapse: collapse;">
                                <thead>
                                    <tr style="background: #f8f9fa; border-bottom: 2px solid #e0e0e0;">
                                        <th style="padding: 12px 16px; text-align: left; font-weight: 600; font-size: 0.85rem; color: var(--text-main); text-transform: uppercase; letter-spacing: 0.5px;">Food Item</th>
                                        <th style="padding: 12px 16px; text-align: center; font-weight: 600; font-size: 0.85rem; color: var(--text-main); text-transform: uppercase; letter-spacing: 0.5px; width: 120px;">Calories</th>
                                        <th style="padding: 12px 16px; text-align: center; font-weight: 600; font-size: 0.85rem; color: var(--text-main); text-transform: uppercase; letter-spacing: 0.5px; width: 100px;">Status</th>
                                        <th style="padding: 12px 16px; text-align: center; font-weight: 600; font-size: 0.85rem; color: var(--text-main); text-transform: uppercase; letter-spacing: 0.5px; width: 100px;">Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                    `;
                    
                    foods.forEach((food, index) => {
                        const rowBg = index % 2 === 0 ? 'white' : '#fafafa';
                        const statusBadge = food.isAvailable 
                            ? '<span style="background: #e8f5e9; color: #2e7d32; padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: 600;">Active</span>'
                            : '<span style="background: #ffebee; color: #c62828; padding: 4px 12px; border-radius: 12px; font-size: 0.8rem; font-weight: 600;">Hidden</span>';
                        
                        const actionBtn = food.isAvailable 
                            ? '<button type="button" onclick="deleteFoodOption(' + food.optionId + ')" style="background: #ff5252; color: white; border: none; padding: 6px 16px; border-radius: 6px; font-size: 0.85rem; font-weight: 600; cursor: pointer; transition: all 0.2s;" onmouseover="this.style.background=\'#ff1744\'" onmouseout="this.style.background=\'#ff5252\'">Remove</button>'
                            : '<button type="button" onclick="restoreFoodOption(' + food.optionId + ')" style="background: #4CAF50; color: white; border: none; padding: 6px 16px; border-radius: 6px; font-size: 0.85rem; font-weight: 600; cursor: pointer; transition: all 0.2s;" onmouseover="this.style.background=\'#45a049\'" onmouseout="this.style.background=\'#4CAF50\'">Restore</button>';
                        
                        html += '<tr style="background: ' + rowBg + '; border-bottom: 1px solid #f0f0f0; transition: background 0.2s;" onmouseover="this.style.background=\'#f5f5f5\'" onmouseout="this.style.background=\'' + rowBg + '\'">' +
                            '<td style="padding: 14px 16px;">' +
                                '<strong style="color: var(--text-main); font-size: 0.95rem;">' + food.optionName + '</strong>' +
                            '</td>' +
                            '<td style="padding: 14px 16px; text-align: center;">' +
                                '<span style="color: var(--text-main); font-weight: 500; font-size: 0.9rem;">' + food.calories + '</span>' +
                                '<span style="color: var(--text-muted); font-size: 0.8rem; margin-left: 2px;">cal</span>' +
                            '</td>' +
                            '<td style="padding: 14px 16px; text-align: center;">' +
                                statusBadge +
                            '</td>' +
                            '<td style="padding: 14px 16px; text-align: center;">' +
                                actionBtn +
                            '</td>' +
                        '</tr>';
                    });
                    
                    html += `
                                </tbody>
                            </table>
                        </div>
                    `;
                    foodList.innerHTML = html;
                }
            })
            .catch(err => {
                console.error('Error loading food options:', err);
                document.getElementById('foodList').innerHTML = '<div style="padding: 20px; text-align: center; color: #f44336; background: #ffebee; border-radius: 8px;">⚠️ Error loading food options. Please refresh and try again.</div>';
            });
    }
    
    
    // Add new Food Option    
    function addFoodOption(event) {
        const serviceId = currentServiceId;
        const foodName = document.getElementById('newFoodName').value.trim();
        const calories = document.getElementById('newFoodCalories').value;
        
        if (!serviceId) {
            alert('⚠️ Please create/select the service first before adding food options.');
            return;
        }
        
        if (!foodName) {
            alert('⚠️ Please enter a food item name.');
            document.getElementById('newFoodName').focus();
            return;
        }
        
        if (!calories || calories < 0) {
            alert('⚠️ Please enter valid calories (0 or more).');
            document.getElementById('newFoodCalories').focus();
            return;
        }
        
        // Show loading state (optional - only if btn available)
        if (event && event.target) {
            const btn = event.target;
            btn.innerHTML = '⏳ Adding...';
            btn.disabled = true;
        }
        
        // Create form and submit
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'AdminManageServices';
        
        const fields = {
            action: 'addFood',
            serviceId: serviceId,
            foodName: foodName,
            calories: calories
        };
        
        for (let key in fields) {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = fields[key];
            form.appendChild(input);
        }
        
        document.body.appendChild(form);
        form.submit();
    }
    
    
    
    // Delete food option    
    function deleteFoodOption(optionId) {
        if (!confirm('🗑️ Remove this food item?\n\nIt will be hidden from customers but can be restored later.')) return;
        
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'AdminManageServices';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'deleteFood';
        form.appendChild(actionInput);
        
        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'optionId';
        idInput.value = optionId;
        form.appendChild(idInput);
        
        document.body.appendChild(form);
        form.submit();
    }
    
    
    
    // Restore food option    
    function restoreFoodOption(optionId) {
        if (!confirm('✅ Restore this food item?\n\nIt will become available to customers again.')) return;
        
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'AdminManageServices';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'restoreFood';
        form.appendChild(actionInput);
        
        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'optionId';
        idInput.value = optionId;
        form.appendChild(idInput);
        
        document.body.appendChild(form);
        form.submit();
    }
    
    // Check if a new service was just created and open it for editing
    window.addEventListener('DOMContentLoaded', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const newServiceId = urlParams.get('newServiceId');
        
        if (newServiceId) {
            // Find the service in the table and open edit modal
            const editButtons = document.querySelectorAll('[onclick^="editService"]');
            for (let btn of editButtons) {
                const onclickStr = btn.getAttribute('onclick');
                const match = onclickStr.match(/editService\((\d+)\)/);
                if (match && match[1] === newServiceId) {
                    btn.click();
                    break;
                }
            }
        }
    });
</script>
<%@ include file="../footer_admin.jsp" %>