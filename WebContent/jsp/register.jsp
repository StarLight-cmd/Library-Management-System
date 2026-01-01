<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Register : Library Management System</title>

<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" />

<!-- Custom LMS CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/auth.css" />
</head>

<body class="auth-container">
	<div class="auth-box">
		<div class="auth-header">
			<h1 class="auth-title">Library Management System</h1>
			<p class="auth-subtitle">Create your account</p>
		</div>

		<!-- User feedback and validation output-->
		<c:if test="${not empty error}">
			<div class="alert alert-danger text-center">${error}</div>
		</c:if>

		<c:if test="${not empty message}">
			<div class="alert alert-success text-center">${message}</div>
		</c:if>

		<!-- Patron Registration Form -->
		<form method="POST" action="${pageContext.request.contextPath}/auth">
			<input type="hidden" name="action" value="register" />

			<div class="form-group">
				<label for="fullname" class="form-label">Full Name:</label> <input
					type="text" id="fullname" name="fullname" class="form-control"
					value="${param.fullname}" required />
			</div>

			<div class="form-group">
				<label for="email" class="form-label">Email:</label> <input
					type="email" id="email" name="email" class="form-control"
					value="${param.email}" required />
			</div>

			<div class="form-group">
				<label for="password" class="form-label">Password:</label>
				<div class="password-container">
					<input type="password" id="password" name="password"
						class="form-control" required />
					<button type="button" class="toggle-password"
						onclick="togglePassword()">🧿</button>
				</div>
			</div>

			<button type="submit" class="auth-btn">REGISTER</button>

			<div class="auth-links">
				<a href="${pageContext.request.contextPath}/jsp/login.jsp"
					class="auth-link">Back to Login</a>
			</div>
		</form>
	</div>

	<script>
		// Password visibility on/off feature
		function togglePassword() {
			const passwordField = document.getElementById('password');
			const toggleBtn = document.querySelector('.toggle-password');

			if (passwordField.type === 'password') {
				passwordField.type = 'text';
				toggleBtn.textContent = '🙈';
			} else {
				passwordField.type = 'password';
				toggleBtn.textContent = '🧿';
			}
		}
	</script>
</body>
</html>
