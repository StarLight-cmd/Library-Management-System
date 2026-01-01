<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="com.library.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Fines Management - Library Management System</title>
    
    <!-- Bootstrap and shared LMS custom styles -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/site.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        body {
            background-color: #1e1e2f;
            color: #fff;
        }
        .dashboard-header h1 {
            color: #fff;
            font-weight: bold;
        }
        .dashboard-header p {
            color: #ccc;
        }
        .content-card {
            background: #2c2c3e;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
        }
        .table th, .table td {
            color: #2c2c3e;
        }
        .fine-amount {
            color: #ff4d4d;
            font-weight: bold;
        }
        .action-buttons .btn {
            margin: 2px;
        }
    </style>
</head>

<body class="dashboard-container">

    <!-- LMS Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">📚 Library Management System</a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    Welcome, <%= user.getFullname() %> (Admin)
                </span>
                <a class="nav-link" href="${pageContext.request.contextPath}/AuthServlet?action=logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 nav-sidebar">
                <nav class="nav flex-column">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/adminDashboard.jsp">📊 Dashboard</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/books?action=list">📚 Manage Books</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/users?action=list">🫂 Manage Users</a>
                    <a class="nav-link active" href="#">💷 Overdue Fines</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/Report.jsp">📜 Reports </a>
                </nav>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                <!-- Fines Management Header -->
                <div class="dashboard-header">
                    <div class="container">
                        <h1 class="fade-in">Fines Management</h1>
                        <p class="lead">Manage library fines and process payments</p>
                    </div>
                </div>

                <!-- Quick Stats -->
                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="stat-card-report">
                            <span class="stat-number-report">${totalFinesCount}</span>
                            <span class="stat-label-report">Total Fines</span>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card-report">
                            <span class="stat-number-report">${pendingFinesCount}</span>
                            <span class="stat-label-report">Pending</span>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card-report">
                            <span class="stat-number-report">${paidFinesCount}</span>
                            <span class="stat-label-report">Paid</span>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="stat-card-report">
                            <span class="stat-number-report">R <fmt:formatNumber value="${totalFinesAmount}" pattern="#,##0.00"/></span>
                            <span class="stat-label-report">Total Amount</span>
                        </div>
                    </div>
                </div>

                <!-- Quick Actions -->
                <div class="row mb-4">
                    <div class="col-md-6">
                        <a href="${pageContext.request.contextPath}/Fines?action=calculateOverdueFines" 
                           class="btn btn-primary w-100" onclick="return confirm('Calculate fines for all overdue books?')">
                            <i class="fas fa-calculator me-2"></i>Calculate Overdue Fines
                        </a>
                    </div>
                    <div class="col-md-6">
                        <a href="${pageContext.request.contextPath}/Fines?action=paymentHistory" 
                           class="btn btn-primary w-100">
                            <i class="fas fa-history me-2"></i>View Payment History
                        </a>
                    </div>
                </div>

                <!-- Fines Table -->
                <div class="content-card">
                    <div class="card-header">
                        <h5 class="card-title text-white mb-0"><i class="fas fa-money-bill-wave me-2"></i>Fines Management</h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover mb-0 align-middle">
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Book</th>
                                        <th>Amount</th>
                                        <th>Overdue Days</th>
                                        <th>Fine Date</th>
                                        <th>Due Date</th>
                                        <th>Status</th>
                                        <th class="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="fine" items="${finesList}">
                                        <tr>
                                            <td>
                                                <div><strong>${fine.userFullName}</strong></div>
                                                <div><small class="text-muted">${fine.userEmail}</small></div>
                                            </td>
                                            <td>${fine.bookTitle}</td>
                                            <td><span class="fine-amount">R <fmt:formatNumber value="${fine.amount}" pattern="#,##0.00"/></span></td>
                                            <td>${fine.overdueDays} days</td>
                                            <td><fmt:formatDate value="${fine.fineDate}" pattern="yyyy-MM-dd"/></td>
                                            <td><fmt:formatDate value="${fine.dueDate}" pattern="yyyy-MM-dd"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${fine.status eq 'pending'}">
                                                        <span class="badge bg-warning text-dark">Pending</span>
                                                    </c:when>
                                                    <c:when test="${fine.status eq 'paid'}">
                                                        <span class="badge bg-success">Paid</span>
                                                    </c:when>
                                                    <c:when test="${fine.status eq 'waived'}">
                                                        <span class="badge bg-info">Waived</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${fine.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center action-buttons">
                                                <c:if test="${fine.status eq 'pending'}">
                                                    <button class="btn btn-sm btn-success" 
                                                            onclick="showPaymentModal(${fine.id}, ${fine.amount}, '${fine.userFullName}')">
                                                        <i class="fas fa-credit-card"></i> Pay
                                                    </button>
                                                    <button class="btn btn-sm btn-warning" 
                                                            onclick="showAdjustModal(${fine.id}, ${fine.amount}, '${fine.userFullName}')">
                                                        <i class="fas fa-edit"></i> Adjust
                                                    </button>
                                                    <button class="btn btn-sm btn-info" 
                                                            onclick="waiveFine(${fine.id}, '${fine.userFullName}')">
                                                        <i class="fas fa-hand-holding-usd"></i> Waive
                                                    </button>
                                                </c:if>
                                                <c:if test="${fine.status ne 'pending'}">
                                                    <span class="text-muted">No actions</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty finesList}">
                                        <tr>
                                            <td colspan="8" class="text-center py-4">
                                                <div class="empty-state">
                                                    <div class="icon">💰</div>
                                                    <h4>No Fines Found</h4>
                                                    <p>There are no fines to display at the moment.</p>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Payment Modal -->
    <div class="modal fade" id="paymentModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Process Payment</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="paymentForm" action="${pageContext.request.contextPath}/Fines?action=processPayment" method="post">
                        <input type="hidden" id="paymentFineId" name="fineId">
                        <div class="mb-3">
                            <label class="form-label">User:</label>
                            <input type="text" id="paymentUserName" class="form-control" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Amount Due:</label>
                            <input type="text" id="paymentAmountDue" class="form-control" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Amount Paid:</label>
                            <input type="number" id="paymentAmountPaid" name="amountPaid" class="form-control" step="0.01" min="0" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Payment Method:</label>
                            <select id="paymentMethod" name="paymentMethod" class="form-control" required>
                                <option value="">Select Payment Method</option>
                                <option value="cash">Cash</option>
                                <option value="eft">EFT</option>
                                <option value="paypal">PayPal</option>
                                <option value="credit_card">Credit Card</option>
                                <option value="debit_card">Debit Card</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Notes:</label>
                            <textarea id="paymentNotes" name="notes" class="form-control" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-success" onclick="processPayment()">Process Payment</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Adjust Fine Modal -->
    <div class="modal fade" id="adjustModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Adjust Fine Amount</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="adjustForm" action="${pageContext.request.contextPath}/Fines?action=adjustFine" method="post">
                        <input type="hidden" id="adjustFineId" name="fineId">
                        <div class="mb-3">
                            <label class="form-label">User:</label>
                            <input type="text" id="adjustUserName" class="form-control" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Current Amount:</label>
                            <input type="text" id="adjustCurrentAmount" class="form-control" readonly>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">New Amount:</label>
                            <input type="number" id="adjustNewAmount" name="newAmount" class="form-control" step="0.01" min="0" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Adjustment Reason:</label>
                            <select id="adjustReason" name="reason" class="form-control" required>
                                <option value="">Select Reason</option>
                                <option value="correction">Correction</option>
                                <option value="dispute">Dispute Resolution</option>
                                <option value="waiver">Partial Waiver</option>
                                <option value="other">Other</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Notes:</label>
                            <textarea id="adjustNotes" name="notes" class="form-control" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-warning" onclick="adjustFine()">Adjust Fine</button>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    
    <script>
        let paymentModal, adjustModal;

        document.addEventListener('DOMContentLoaded', function() {
            paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));
            adjustModal = new bootstrap.Modal(document.getElementById('adjustModal'));
        });

        function showPaymentModal(fineId, amount, userName) {
            document.getElementById('paymentFineId').value = fineId;
            document.getElementById('paymentUserName').value = userName;
            document.getElementById('paymentAmountDue').value = 'R ' + amount.toFixed(2);
            document.getElementById('paymentAmountPaid').value = amount.toFixed(2);
            document.getElementById('paymentAmountPaid').max = amount;
            document.getElementById('paymentMethod').value = '';
            document.getElementById('paymentNotes').value = '';
            paymentModal.show();
        }

        function processPayment() {
            document.getElementById('paymentForm').submit();
        }

        function showAdjustModal(fineId, amount, userName) {
            document.getElementById('adjustFineId').value = fineId;
            document.getElementById('adjustUserName').value = userName;
            document.getElementById('adjustCurrentAmount').value = 'R ' + amount.toFixed(2);
            document.getElementById('adjustNewAmount').value = amount.toFixed(2);
            document.getElementById('adjustReason').value = '';
            document.getElementById('adjustNotes').value = '';
            adjustModal.show();
        }

        function adjustFine() {
            document.getElementById('adjustForm').submit();
        }

        function waiveFine(fineId, userName) {
            if (!confirm('Are you sure you want to waive the fine for ' + userName + '?')) {
                return;
            }
            const reason = prompt('Please enter the reason for waiving this fine:');
            if (!reason) {
                return;
            }
            // Create a form and submit it
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/Fines?action=waiveFine';
            
            const fineIdInput = document.createElement('input');
            fineIdInput.type = 'hidden';
            fineIdInput.name = 'fineId';
            fineIdInput.value = fineId;
            
            const reasonInput = document.createElement('input');
            reasonInput.type = 'hidden';
            reasonInput.name = 'reason';
            reasonInput.value = reason;
            
            form.appendChild(fineIdInput);
            form.appendChild(reasonInput);
            document.body.appendChild(form);
            form.submit();
        }
    </script>
</body>
</html>