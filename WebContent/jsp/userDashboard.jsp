<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.library.model.User"%>
<%@ page import="com.library.dao.DashboardDAO"%>
<%
	// Checking valid user session
User user = (User) session.getAttribute("user");
if (user == null || user.isAdmin()) {
	response.sendRedirect("login.jsp");
	return;
}
int id = user.getId();
// Dashboard statistics from DAO
DashboardDAO dashboardDAO = new DashboardDAO();
int borrowedCount = dashboardDAO.getBorrowedCountByUser(id);
int overdueCount = dashboardDAO.getOverdueCountByUser(id);
int availableBooks = dashboardDAO.getAvailableBooksForUser();
int booksRead = dashboardDAO.getBooksReadByUser(id);
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>User Dashboard - Library Management System</title>

<!-- Bootstrap -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">

<!-- Custom LMS styling -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/components.css" />

<style>
body.dashboard-container {
	background-color: #f9f9ff;
	font-family: 'Poppins', sans-serif;
}

.dashboard-nav {
	background: linear-gradient(90deg, #6f42c1, #8a63d2);
}

.quick-action-btn {
	background: #ffffff;
	border-radius: 10px;
	padding: 20px;
	box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
	transition: transform 0.2s;
	text-decoration: none;
	color: #333;
}

.quick-action-btn:hover {
	transform: translateY(-4px);
	background-color: #f1f0ff;
}

.quick-action-btn .icon {
	font-size: 2rem;
	display: block;
}

.stat-card {
	background: white;
	border-radius: 10px;
	padding: 20px;
	text-align: center;
	box-shadow: 0 3px 8px rgba(0, 0, 0, 0.1);
}

.stat-card .stat-number {
	font-size: 2rem;
	font-weight: bold;
}

.stat-card.info {
	border-top: 4px solid #0d6efd;
}

.stat-card.warning {
	border-top: 4px solid #ffc107;
}

.stat-card.success {
	border-top: 4px solid #198754;
}
</style>
</head>

<body class="dashboard-container">
	<!-- LMS Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
		<div class="container">
			<a class="navbar-brand" href="#"> 📚 Library Management System </a>
			<div class="navbar-nav ms-auto align-items-center">
				<span class="navbar-text text-white me-3"> Welcome, <%=user.getFullname()%>
				</span>
				<form action="<%=request.getContextPath()%>/logout" method="get"
					class="mb-0">
					<button type="submit" class="btn btn-outline-light btn-sm">Logout</button>
				</form>
			</div>
		</div>
	</nav>

	<div class="container mt-5">
		<div class="text-center mb-5">
			<h1 class="fw-bold" style="color: #6f42c1;">User Dashboard</h1>
			<p class="text-muted">"I can no other answer make but thanks, and thanks, and ever thanks." - William Shakespeare, Twelfth Night (Act 3, Scene 3)</p>
		</div>

		<!-- LMS User Actions -->
		<div class="row g-4 mb-5">
			<div class="col-12 col-sm-6 col-md-4">
				<a
					href="${pageContext.request.contextPath}/books?action=listUserBooks"
					class="quick-action-btn text-center d-block"> <span
					class="icon">📚</span> <span class="label">Browse Available
						Books</span>
				</a>
			</div>
			<div class="col-12 col-sm-6 col-md-4">
				<a href="${pageContext.request.contextPath}/borrowed"
					class="quick-action-btn text-center d-block"> <span
					class="icon">📖</span> <span class="label">My Borrowed Books</span>
				</a>
			</div>
			<div class="col-12 col-sm-6 col-md-4">
				<a href="${pageContext.request.contextPath}/recommendations"
					class="quick-action-btn text-center d-block"> <span
					class="icon">💡</span> <span class="label">Book
						Recommendations</span>
				</a>
			</div>
		</div>

		<!-- User stats and reports-->
		<div class="row g-4 mb-5">
			<div class="col-12 col-sm-6 col-md-3">
				<div class="stat-card info">
					<div class="stat-number" id="borrowedCount"><%=borrowedCount%></div>
					<div class="stat-label">Currently Borrowed</div>
				</div>
			</div>
			<div class="col-12 col-sm-6 col-md-3">
				<div class="stat-card warning">
					<div class="stat-number" id="overdueCount"><%=overdueCount%></div>
					<div class="stat-label">Overdue Books</div>
				</div>
			</div>
			<div class="col-12 col-sm-6 col-md-3">
				<div class="stat-card success">
					<div class="stat-number" id="availableBooks"><%=availableBooks%></div>
					<div class="stat-label">Available Books</div>
				</div>
			</div>
			<div class="col-12 col-sm-6 col-md-3">
				<div class="stat-card">
					<div class="stat-number" id="readingHistory"><%=booksRead%></div>
					<div class="stat-label">Books Read</div>
				</div>
			</div>
		</div>

		<!-- LMS Patron Guide -->
		<div class="card shadow-sm mt-5 ">
			<div class="card-header text-white"
				style="background: linear-gradient(90deg, #6f42c1, #8a63d2);">
				<h5 class="mb-0">📖 Quick Library Guide</h5>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-md-6">
						<h6>How to Borrow Books</h6>
						<ul class="list-unstyled">
							<li>• Browse available books</li>
							<li>• Click "Borrow" on any book</li>
							<li>• Borrow duration is 7 days</li>
						</ul>
					</div>
					<div class="col-md-6">
						<h6>Borrowing Rules</h6>
						<ul class="list-unstyled">
							<li>• R75 fine for overdue books</li>
						</ul>
					</div>
					<div class="col-md-6">
						<h6>Book Recommendations</h6>
						<ul class="list-unstyled">
							<li>• Click on Book Recommendations for your next read!</li>
							<li>• We suggest books based on your borrowing history</li>
						</ul>
					</div>
				</div>
			</div>
		</div>

	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

	<script>
		// Refresh every 30 seconds and on page load
		window.onload = updateStats;
		setInterval(updateStats, 30000);
	</script>


</body>
</html>
