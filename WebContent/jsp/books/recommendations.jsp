<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Books Recommended for you! : Library Management System</title>

<!-- Bootstrap and Custom lms styles -->
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
			<h1>Recommended Books for You</h1>
			<p class="lead">Discover books aesthetically you!</p>
		</div>

		<div class="mt-4 col-12 d-flex justify-content-between">
			<a href="${pageContext.request.contextPath}/jsp/userDashboard.jsp"
				class="btn btn-secondary">🚪 Back to Dashboard</a>
		</div>
		
		<br>
		<!-- LMS User Recommended Books -->
		<div class="content-card">
			<div class="card-body p-0">
				<c:choose>
					<c:when test="${empty suggestedBooks}">
						<div class="text-center py-5">
							<div class="empty-state">
								<div class="icon">📖</div>
								<h4>No Recommendations Yet</h4>
								<p>We couldn’t find any suggestions based on your recent
									activity.</p>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<table class="table table-hover mb-0 align-middle">
							<thead>
								<tr>
									<th>ISBN</th>
									<th>Title</th>
									<th>Author</th>
									<th>Genre</th>
									<th>Year</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="book" items="${suggestedBooks}">
									<tr>
										<td>${book.isbn}</td>
										<td>${book.title}</td>
										<td>${book.author}</td>
										<td>${book.genre}</td>
										<td>${book.year}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>