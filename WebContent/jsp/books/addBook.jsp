<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.library.model.User"%>

<%
	// Session check for library admin
User user = (User) session.getAttribute("user");
if (user == null || !user.isAdmin()) {
	response.sendRedirect("login.jsp");
	return;
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Add New Book - Library Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Bootstrap and LMS custom styles -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css">
</head>

<style>
	.alert-danger {
    background-color: #f3e8ff !important; 
    color: #5b21b6 !important;           
    border-color: #c084fc !important;   
}
	
</style>

<body class="dashboard-container">

	<!-- LMS Navbar -->
	<nav class="navbar navbar-expand-lg navbar-dark dashboard-nav">
		<div class="container">
			<a class="navbar-brand fw-bold" href="#">📚 Library Management
				System</a>
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
					<a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/adminDashboard.jsp">🏡
						Dashboard</a> <a class="nav-link"
						href="${pageContext.request.contextPath}/books?action=list">📚
						Manage Books</a> <a class="nav-link"
						href="${pageContext.request.contextPath}/users?action=list">🫂
						Manage Users</a> <a class="nav-link active"
						href="${pageContext.request.contextPath}/jsp/books/addBook.jsp">📘
						Add New Book</a><a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/fines.jsp">
						💷 Fines </a><a class="nav-link"
						href="${pageContext.request.contextPath}/jsp/Report.jsp">
						📜 Reports </a>
				</nav>
			</div>

			<!-- LMS main pane-->
			<div class="col-md-9 col-lg-10 main-content">

				<div class="dashboard-header text-center my-4">
					<h1 class="fade-in fw-bold">Add New Book</h1>
					<p class="lead">Add books to your library's collection</p>
				</div>

				<!-- Form to add a book to book catalogue -->
				<div class="content-card mx-auto" style="max-width: 700px;">
					<div class="card-body">
						<form action="${pageContext.request.contextPath}/books"
							method="post">
							<input type="hidden" name="action" value="add" />
							<c:if test="${not empty error}">
							    <div class="alert alert-danger">${error}</div>
							</c:if>
							
							<div class="mb-3">
								<label class="form-label">ISBN</label> <input type="text"
									name="isbn" class="form-control" placeholder="Enter book ISBN"
									required>
							</div>

							<div class="mb-3">
								<label class="form-label">Title</label> <input type="text"
									name="title" class="form-control"
									placeholder="Enter book title" required>
							</div>

							<div class="mb-3">
								<label class="form-label">Author</label> <input type="text"
									name="author" class="form-control"
									placeholder="Enter author name" required>
							</div>

							<div class="mb-3">
								<label class="form-label">Genre</label> <input type="text"
									name="genre" class="form-control"
									placeholder="Enter book genre">
							</div>

							<div class="mb-3">
								<label class="form-label">Year</label> <input type="number"
									name="year" class="form-control"
									placeholder="Enter publication year" required>
							</div>

							<div class="d-flex justify-content-between mt-4">
								<a href="${pageContext.request.contextPath}/books?action=list"
									class="btn btn-secondary"> ← Back to List </a>
								<button type="submit" class="btn btn-primary">💾 Save
									Book</button>
							</div>
						</form>
					</div>
				</div>

			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
