<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.library.model.User"%>
<%@ page import="com.library.dao.DashboardDAO"%>

<%
	// Checking admin has valid session
User user = (User) session.getAttribute("user");
if (user == null || !user.isAdmin()) {
	response.sendRedirect("login.jsp");
	return;
}

// Dashboard statistics from data access object
DashboardDAO dashboardDAO = new DashboardDAO();
int totalBooks = dashboardDAO.getTotalBooks();
int activeUsers = dashboardDAO.getActiveUsers();
int booksBorrowedToday = dashboardDAO.getBooksBorrowedToday();
int overdueBooks = dashboardDAO.getOverdueBooks();
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Library Management System : Admin Dashboard</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Bootstrap and Custom LMS CSS -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css" />
</head>
<body class="dashboard-container">

	<!-- LMS Navigation Bar -->
	<nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
		<div class="container">
			<a class="navbar-brand fw-bold" href="#"> 📚 Library Management
				System </a>
			<div class="navbar-nav ms-auto">
				<span class="navbar-text me-3"> Welcome, <%=user.getFullname()%>
					(Admin)
				</span> <a class="nav-link"
					href="${pageContext.request.contextPath}/logout"> Logout </a>
			</div>
		</div>
	</nav>

	<div class="container-fluid">
		<div class="row">

			<!-- LMS Sidebar -->
			<div class="col-md-3 col-lg-2 nav-sidebar">
				<nav class="nav flex-column">
					<a class="nav-link active" href="#"> 🏡 Dashboard </a> <a
						class="nav-link"
						href="${pageContext.request.contextPath}/books?action=list">
						📚 Manage Books </a> <a class="nav-link"
						href="${pageContext.request.contextPath}/users?action=list">
						🫂 Manage Users </a> <a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/books/addBook.jsp">
						📘 Add New Book </a><a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/fines.jsp">
						💷 Fines </a><a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/Report.jsp">
						📜 Reports </a>
				</nav>
			</div>

			<!-- Mian admin LMS content pane -->
			<div class="col-md-9 col-lg-10 main-content">

				<!-- Header -->
				<div class="dashboard-header text-center my-4">
					<h1 class="fade-in fw-bold">Admin Dashboard</h1>
					<p class="lead">"Uneasy lies the head that wears a crown." - William Shakespeare, Henry IV, Part 2 (Act 3, Scene 1)</p>
				</div>

				<div class="container">

					<!-- LMS Report stats -->
					<div class="row g-4 mb-5">
						<div class="col-12 col-sm-6 col-xl-3">
							<div class="stat-card">
								<div class="stat-number"><%=totalBooks%></div>
								<div class="stat-label">Total Books</div>
							</div>
						</div>
						<div class="col-12 col-sm-6 col-xl-3">
							<div class="stat-card success">
								<div class="stat-number"><%=activeUsers%></div>
								<div class="stat-label">Active Users</div>
							</div>
						</div>
						<div class="col-12 col-sm-6 col-xl-3">
							<div class="stat-card info">
								<div class="stat-number"><%=booksBorrowedToday%></div>
								<div class="stat-label">Books Borrowed Today</div>
							</div>
						</div>
						<div class="col-12 col-sm-6 col-xl-3">
							<div class="stat-card warning">
								<div class="stat-number"><%=overdueBooks%></div>
								<div class="stat-label">Overdue Books</div>
							</div>
						</div>
					</div>

					<!-- LMS Actions and functionality -->
					<div class="dashboard-section">
						<div class="content-card">
							<div class="card-header">
								<h5 class="card-title">Quick Actions</h5>
							</div>
							<div class="card-body">
								<div class="row g-4">
									<div class="col-12 col-md-4">
										<a
											href="${pageContext.request.contextPath}/jsp/books/addBook.jsp"
											class="quick-action-btn text-center d-block"> <span
											class="icon">📘</span> <span class="label">Add New Book</span>
										</a>
									</div>
									<div class="col-12 col-md-4">
										<a href="${pageContext.request.contextPath}/books?action=list"
											class="quick-action-btn text-center d-block"> <span
											class="icon">📚</span> <span class="label">Manage
												Books</span>
										</a>
									</div>
									<div class="col-12 col-md-4">
										<a href="${pageContext.request.contextPath}/users?action=list"
											class="quick-action-btn text-center d-block"> <span
											class="icon">🫂</span> <span class="label">Manage
												Users</span>
										</a>
									</div>
								</div>
							</div>
						</div>
					</div>


					<div class="dashboard-section mt-5">
						<div class="content-card">
							<div class="card-header">
								<h5 class="card-title">Recent Activity</h5>
							</div>
							<div class="card-body">
								<div class="list-group activity-list">
									<div class="list-group-item">
										<div class="d-flex w-100 justify-content-between">
											<h6>Real-time Statistics</h6>
											<small>Live</small>
										</div>
										<p>Dashboard now shows real data from the database.</p>
									</div>
									<div class="list-group-item">
										<div class="d-flex w-100 justify-content-between">
											<h6>Total Books</h6>
											<small>Database</small>
										</div>
										<p>
											Currently
											<%=totalBooks%>
											books in the library.
										</p>
									</div>
									<div class="list-group-item">
										<div class="d-flex w-100 justify-content-between">
											<h6>Active Users</h6>
											<small>Database</small>
										</div>
										<p><%=activeUsers%>
											active users in the system.
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>

				</div>
			</div>
		</div>
	</div>


	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


	<script>
		setTimeout(function() {
			window.location.reload();
		}, 30000);
	</script>

</body>
</html>
