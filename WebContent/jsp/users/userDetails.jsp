<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.library.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Details - Library Management System</title>

    <!-- Bootstrap and Custom LMS Styles -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/site.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />
</head>

<body class="dashboard-container">

    <nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
        <div class="container">
            <a class="navbar-brand fw-bold" href="#">📚 Library Management System</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/AuthServlet?action=logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">

        <div class="dashboard-header text-center mb-5">
            <h1>User Details</h1>
            <p class="lead">Detailed information about selected user</p>
        </div>

        <!-- Detailed user information -->
        <div class="content-card mx-auto" style="max-width: 700px;">
            <div class="card-body">
                <table class="table table-bordered mb-0 align-middle">
                    <tr>
                        <th class="bg-light" style="width: 200px;">User ID</th>
                        <td><%= user.getId() %></td>
                    </tr>
                    <tr>
                        <th class="bg-light">Full Name</th>
                        <td><%= user.getFullname() %></td>
                    </tr>
                    <tr>
                        <th class="bg-light">Email</th>
                        <td><%= user.getEmail() %></td>
                    </tr>
                    <tr>
                        <th class="bg-light">Role</th>
                        <td>
                            <% if (user.isAdmin()) { %>
                                <span class="badge bg-danger">Admin</span>
                            <% } else { %>
                                <span class="badge bg-success">User</span>
                            <% } %>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-12 d-flex justify-content-center">
                <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary">
                    🚪 Back to Users List
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
