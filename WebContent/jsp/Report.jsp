
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
    <title>Reports & Analytics</title>
    
    <!-- Bootstrap and shared LMS custom styles -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/site.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/components.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <style>
        .report-controls {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }
        .export-btn-group .btn {
            margin-left: 5px;
        }
        .table th {
            background-color: #e9ecef;
            border-bottom: 2px solid #dee2e6;
        }
        .quick-action-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            text-decoration: none;
            transition: all 0.3s ease;
            border: none;
        }
        .quick-action-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            color: white;
        }
        .icon {
            font-size: 2rem;
            display: block;
            margin-bottom: 10px;
        }
        .label {
            font-size: 1.1rem;
            font-weight: 600;
        }
    </style>
</head>
<body class="dashboard-container">
    <nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">📚 Library Management System</a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">Welcome, <%= user.getFullname() %> (Admin)</span>
                <a class="nav-link" href="${pageContext.request.contextPath}/AuthServlet?action=logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3 col-lg-2 nav-sidebar">
                <nav class="nav flex-column">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/adminDashboard.jsp">🏡 Dashboard</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/books?action=list">📚 Manage Books</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/users?action=list">🫂‍ Manage Users</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/books/addBook.jsp">📘 Add New Book</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/Fines">💷 Overdue Fines</a>
                    <a class="nav-link active" href="${pageContext.request.contextPath}/Reports">📜 Reports</a>
                </nav>
            </div>

            <!-- Main Content -->
            <div class="col-md-9 col-lg-10 main-content">
                
                <!-- Header -->
                <div class="dashboard-header text-center my-4">
                    <h1 class="fade-in fw-bold">Reports and Analytics</h1>
                    <p class="lead">"Not everything that can be counted counts, and not everything that counts can be counted." - William Bruce Cameron</p>
                </div>
                
                <div class="content-card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Available Reports</h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-4">
                            <div class="col-12 col-md-6">
                                <a href="javascript:void(0)" onclick="loadReport('borrowHistory')" class="quick-action-btn text-center d-block">
                
                                    <span class="label">📋Borrow History</span>
                                    <small class="d-block mt-2 text-light">View all borrowing transactions</small>
                                </a>
                            </div>
                            <div class="col-12 col-md-6">
                                <a href="javascript:void(0)" onclick="loadReport('overdueReport')" class="quick-action-btn text-center d-block">
                                    <span class="icon">⚠️</span>
                                    <span class="label">Overdue Items</span>
                                    <small class="d-block mt-2 text-light">View overdue books and fines</small>
                                </a>
                            </div>
                            <div class="col-12 col-md-6">
                                <a href="javascript:void(0)" onclick="loadReport('popularBooks')" class="quick-action-btn text-center d-block">
                                    <span class="icon">⭐</span>
                                    <span class="label">Popular Books</span>
                                    <small class="d-block mt-2 text-light">Most frequently borrowed books</small>
                                </a>
                            </div>
                            <div class="col-12 col-md-6">
                                <a href="javascript:void(0)" onclick="loadReport('fineCollection')" class="quick-action-btn text-center d-block">
                                    <span class="icon">💰</span>
                                    <span class="label">Fine Collection</span>
                                    <small class="d-block mt-2 text-light">Fine payment history and status</small>
                                </a>
                            </div>
                        </div>
                        
                        <div id="reportContent" class="mt-4">
                            <!-- Report content will be loaded here -->
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script>
    let currentReportType = 'borrowHistory';

    function loadReport(reportType) {
        currentReportType = reportType;
        const content = document.getElementById('reportContent');
        content.innerHTML = `
            <div class="text-center py-4">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Loading report...</p>
            </div>
        `;

        // Correct: include context path in fetch URL
        fetch('${pageContext.request.contextPath}/Reports?action=' + reportType)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                content.innerHTML = html;
            })
            .catch(error => {
                console.error('Error:', error);
                content.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error loading report: ${error.message}
                    </div>
                `;
            });
    }

    function filterReport(reportType) {
        const startDate = document.getElementById('startDate')?.value || '';
        const endDate = document.getElementById('endDate')?.value || '';
        const search = document.getElementById('search')?.value || '';

        let url = '${pageContext.request.contextPath}/Reports?action=' + reportType;
        if (startDate) url += '&startDate=' + startDate;
        if (endDate) url += '&endDate=' + endDate;
        if (search) url += '&search=' + encodeURIComponent(search);

        const content = document.getElementById('reportContent');
        content.innerHTML = `
            <div class="text-center py-4">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
                <p class="mt-2">Filtering report...</p>
            </div>
        `;

        fetch(url)
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                content.innerHTML = html;
            })
            .catch(error => {
                console.error('Error:', error);
                content.innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Error filtering report: ${error.message}
                    </div>
                `;
            });
    }

    function exportReport(reportType, format) {
        const startDate = document.getElementById('startDate')?.value || '';
        const endDate = document.getElementById('endDate')?.value || '';
        const search = document.getElementById('search')?.value || '';

        let url = '${pageContext.request.contextPath}/Reports?action=export&reportType=' + reportType + '&format=' + format;
        if (startDate) url += '&startDate=' + startDate;
        if (endDate) url += '&endDate=' + endDate;
        if (search) url += '&search=' + encodeURIComponent(search);

        if (format === 'csv') {
            window.location.href = url;
        } else {
            fetch(url)
                .then(response => response.text())
                .then(html => {
                    document.getElementById('reportContent').innerHTML = html;
                })
                .catch(error => {
                    console.error('Error exporting:', error);
                });
        }
    }

    // Load first report automatically
    document.addEventListener('DOMContentLoaded', function() {
        loadReport('borrowHistory');
    });
	</script>

</body>
</html>