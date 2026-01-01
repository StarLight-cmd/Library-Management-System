<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="java.time.*, java.time.temporal.ChronoUnit"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>My Borrowed Books - Library Management System</title>

<!-- Bootstrap and shared LMS custom styles -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css" />

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

.fine-text {
	color: #ff4d4d;
	font-weight: bold;
}
</style>
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
			<h1>My Borrowed Books</h1>
			<p class="lead">View and manage your borrowed books</p>
		</div>
		
		<div class="row mt-4">
			<div class="col-12 d-flex justify-content-between">
				<a href="${pageContext.request.contextPath}/jsp/userDashboard.jsp"
					class="btn btn-secondary"> 🚪 Back to Dashboard </a>
			</div>
		</div>
		
		<br>

		<!-- LMS Borrowed Books Table -->
		<div class="content-card">
			<div class="card-body p-0">
				<table class="table table-hover mb-0 align-middle">
					<thead>
						<tr>
							<th>ISBN</th>
							<th>Borrowed Date</th>
							<th>Return Date</th>
							<th>Status</th>
							<th>Fine</th>
							<th class="text-center">Action</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="b" items="${borrowedBooks}">
							<tr>
								<td><strong>${b.isbn}</strong></td>
								<td>${b.borrowedDate}</td>
								<td>${b.returnDate != null ? b.returnDate : '-'}</td>
								<td><c:choose>
										<c:when test="${b.status eq 'Borrowed'}">
											<span class="badge bg-warning text-dark">${b.status}</span>
										</c:when>
										<c:otherwise>
											<span class="badge bg-success">${b.status}</span>
										</c:otherwise>
									</c:choose></td>
								<td><c:set var="fineKey" value="${'fine_' += b.id}" /> <c:choose>
										<c:when test="${requestScope[fineKey] > 0}">
											<span class="fine-text">Late fine payment required</span>
										</c:when>
										<c:otherwise>
											<span class="text-muted">-</span>
										</c:otherwise>
									</c:choose></td>

								<td class="text-center"><c:if
										test="${b.status == 'Borrowed' || b.status == 'borrowed'}">
										<a
											href="${pageContext.request.contextPath}/borrowed?action=return&id=${b.id}"
											class="btn btn-success btn-sm"> Return </a>
									</c:if></td>
							</tr>
						</c:forEach>

						<c:if test="${empty borrowedBooks}">
							<tr>
								<td colspan="6" class="text-center py-4">
									<div class="empty-state">
										<div class="icon">📘</div>
										<h4>No Borrowed Books</h4>
										<p>You haven’t borrowed any books yet.</p>
									</div>
								</td>
							</tr>
						</c:if>
					</tbody>
				</table>
			</div>
		</div>
	</div>

	<!-- Bootstrap JS -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
