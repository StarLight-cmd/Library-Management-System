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
<title>Edit Book - Library Management System</title>
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
				<a class="nav-link" href="${pageContext.request.contextPath}/logout">Logout</a>
			</div>
		</div>
	</nav>

	<div class="container my-5">
		<div class="dashboard-header text-center mb-4">
			<h1 class="fade-in fw-bold">Edit Book Details</h1>
			<p class="lead">Update information for this book</p>
		</div>

		<div class="content-card mx-auto" style="max-width: 700px;">
			<div class="card-body">
				<form action="${pageContext.request.contextPath}/books"
					method="post">
					
					<c:if test="${not empty error}">
					    <div class="alert alert-danger">${error}</div>
					</c:if>
					
					<input type="hidden" name="action" value="update" /> <input
						type="hidden" name="isbn" value="${book.isbn}" />

					<div class="mb-3">
						<label class="form-label">Title</label> <input type="text"
							name="title" value="${book.title}" class="form-control" required>
					</div>

					<div class="mb-3">
						<label class="form-label">Author</label> <input type="text"
							name="author" value="${book.author}" class="form-control"
							required>
					</div>

					<div class="mb-3">
						<label class="form-label">Genre</label> <input type="text"
							name="genre" value="${book.genre}" class="form-control">
					</div>

					<div class="mb-3">
						<label class="form-label">Year</label> <input type="number"
							name="year" value="${book.year}" class="form-control">
					</div>

					<div class="d-flex justify-content-between mt-4">
						<a href="${pageContext.request.contextPath}/books?action=list"
							class="btn btn-secondary">🚪 Back to List</a>
						<button type="submit" class="btn btn-primary">💾 Update
							Book</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
