<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Login : Library Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- LMS Styling -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/auth.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/components.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/dashboard.css" />
</head>
<body>
	<div class="auth-container">
		<div class="auth-box">
			<h2 class="text-center mb-3 auth-title">Library Management System</h2>
			<p class="text-center text-muted">Please log in to continue</p>

			<!-- Validation Feedback -->
			<c:if test="${not empty error}">
				<div class="alert alert-danger text-center">${error}</div>
			</c:if>

			<c:if test="${not empty message}">
				<div class="alert alert-success text-center">${message}</div>
			</c:if>

			<form id="loginForm" action="${pageContext.request.contextPath}/auth"
				method="post">
				<input type="hidden" name="action" value="login" />

				<div class="mb-3">
					<label for="email" class="form-label">Email:</label> <input
						type="email" id="email" name="email" class="form-control"
						value="${param.email}" required />
					<div class="error-message text-danger small" id="emailError"></div>
				</div>

				<div class="mb-3">
					<label for="password" class="form-label">Password:</label>
					<div class="password-container" style="position: relative;">
						<input type="password" id="password" name="password"
							class="form-control" required />
						<button type="button" class="toggle-password"
							onclick="togglePassword()"
							style="position: absolute; right: 10px; top: 50%; transform: translateY(-50%); background: none; border: none;">
							🧿</button>
					</div>
					<div class="error-message text-danger small" id="passwordError"></div>
				</div>

				<button type="submit" id="loginBtn" class="auth-btn">Login</button>

				<div class="auth-links">
					<a href="${pageContext.request.contextPath}/jsp/register.jsp"
						class="auth-link">Create Account</a> <a
						href="${pageContext.request.contextPath}/jsp/forgotPassword.jsp"
						class="auth-link">Forgot Password?</a>
				</div>
			</form>
		</div>
	</div>

	<script>
		function togglePassword() {
			const passwordInput = document.getElementById("password");
			const toggleButton = document.querySelector(".toggle-password");

			if (passwordInput.type === "password") {
				passwordInput.type = "text";
				toggleButton.textContent = "🙈";
			} else {
				passwordInput.type = "password";
				toggleButton.textContent = "🧿";
			}
		}
	</script>
</body>
</html>
