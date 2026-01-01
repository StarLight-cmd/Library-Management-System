<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Available Books : Library Management System</title>

    <!-- Bootstrap and shared Custom LMS styles -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/site.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css" />

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
            box-shadow: 0 2px 10px rgba(0,0,0,0.3);
        }

        .table th {
            color: #2c2c3e;
        }

        .table td {
            color: #2c2c3e;
        }

        .btn-outline-secondary {
            color: #fff;
            border-color: #fff;
        }

        .btn-outline-secondary:hover {
            background-color: #fff;
            color: #000;
        }

        .btn-success {
            background-color: #28a745;
            border: none;
        }

        .btn-success:hover {
            background-color: #34d058;
        }

        .empty-state {
            color: #bbb;
        }

        .form-control {
            background-color: #3b3b52;
            border: none;
            color: #fff;
        }

        .form-control::placeholder {
            color: #aaa;
        }

        .form-control:focus {
            background-color: #3b3b52;
            color: #fff;
            box-shadow: 0 0 5px #ffc107;
        }
    </style>
</head>

<body class="dashboard-container">

    <!-- Navbar -->
    <nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
        <div class="container">
            <a class="navbar-brand" href="#">📚 Library Management System</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/AuthServlet?action=logout">Logout</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">

        <!-- Header -->
        <div class="dashboard-header text-center mb-5">
            <h1>Available Books</h1>
            <p class="lead">Browse and borrow books from our book collection</p>
        </div>
        
        <div class="row mt-4">
            <div class="col-12 d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/jsp/userDashboard.jsp" class="btn btn-secondary">🚪 Back to Dashboard</a>
            </div>
        </div>
        
        <br>

        <!-- Search Bar -->
        <div class="content-card mb-4">
            <div class="card-body">
                <form method="get" action="${pageContext.request.contextPath}/books" class="row g-3">
                    <input type="hidden" name="action" value="searchUserBooks" />
                    <div class="col-md-8">
                        <input type="text" name="keyword" class="form-control" placeholder="Search by title, author, or genre..." value="${param.keyword}">
                    </div>
                    <div class="col-md-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary w-50">Search</button>
                        <a href="${pageContext.request.contextPath}/books?action=listUserBooks" class="btn btn-outline-secondary w-50">Clear</a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Books Table -->
        <div class="content-card">
            <div class="card-body p-0">
                <table class="table table-hover mb-0 align-middle">
                    <thead>
                        <tr>
                            <th>ISBN</th>
                            <th>Title</th>
                            <th>Author</th>
                            <th>Genre</th>
                            <th>Year</th>
                            <th class="text-center">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="b" items="${books}">
                            <tr>
                                <td><strong>${b.isbn}</strong></td>
                                <td>${b.title}</td>
                                <td>${b.author}</td>
                                <td><span class="badge bg-light text-dark">${b.genre}</span></td>
                                <td>${b.year}</td>
                                <td class="text-center">
                                    <form action="${pageContext.request.contextPath}/borrowed" method="post" class="d-inline">
                                        <input type="hidden" name="isbn" value="${b.isbn}" />
                                        <button type="submit" class="btn btn-success btn-sm">Borrow</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty books}">
                            <tr>
                                <td colspan="6" class="text-center py-4">
                                    <div class="empty-state">
                                        <div class="icon">📖</div>
                                        <h4>No Books Found</h4>
                                        <p>No books are currently available to borrow.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>

    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
