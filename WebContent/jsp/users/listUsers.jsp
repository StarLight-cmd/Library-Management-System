<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Manage Users of the Library Management System</title>

<!-- Bootstrap and custom LMS styling -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css" />
</head>

<body class="dashboard-container">

	<!-- LMS Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
		<div class="container">
			<a class="navbar-brand fw-bold" href="#">📚 Library Management
				System</a>
			<div class="navbar-nav ms-auto">
				<a class="nav-link"
					href="${pageContext.request.contextPath}/AuthServlet?action=logout">Logout</a>
			</div>
		</div>
	</nav>

	<div class="container mt-5">

		<div class="dashboard-header text-center mb-5">
			<h1>Manage Users</h1>
			<p class="lead">View and manage all registered users</p>
		</div>
		
		<div class="row mt-4">
			<div class="col-12 d-flex justify-content-between">
				<a href="${pageContext.request.contextPath}/jsp/adminDashboard.jsp"
					class="btn btn-secondary">🚪 Back to Dashboard</a>
			</div>
		</div>
		
		<br>

		<!-- LMS Users Table -->
		<div class="content-card">
			<div class="card-body p-0">
				<table class="table table-hover mb-0 align-middle">
					<thead>
						<tr>
							<th>ID</th>
							<th>Full Name</th>
							<th>Email</th>
							<th>Role</th>
							<th class="text-center">Actions</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="u" items="${users}">
							<tr>
								<td>${u.id}</td>
								<td>${u.fullname}</td>
								<td>${u.email}</td>
								<td><c:choose>
										<c:when test="${u.admin}">
											<span class="badge bg-danger">Admin</span>
										</c:when>
										<c:otherwise>
											<span class="badge bg-success">User</span>
										</c:otherwise>
									</c:choose></td>
								<td class="text-center"><a
									href="${pageContext.request.contextPath}/users?action=details&id=${u.id}"
									class="btn btn-sm btn-outline-primary action-btn"> View
										Details </a></td>
							</tr>
						</c:forEach>

						<c:if test="${empty users}">
							<tr>
								<td colspan="5" class="text-center py-4">
									<div class="empty-state">
										<div class="icon">🫂</div>
										<h4>No Users Found</h4>
										<p>No registered users available at the moment.</p>
									</div>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>

	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
