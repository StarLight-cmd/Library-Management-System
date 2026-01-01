<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Forgot Password : Library Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Bootstrap -->
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" />

<!-- Custom LMS Styling -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/site.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/auth.css" />
</head>

<body class="auth-container">
	<div class="auth-box">
		<div class="auth-header">
			<h1 class="auth-title">Library Management System</h1>
			<p class="auth-subtitle">Reset your password</p>
		</div>

		<!-- User Feedback based on input -->
		<c:if test="${not empty error}">
			<div class="alert alert-danger text-center">${error}</div>
		</c:if>
		<c:if test="${not empty message}">
			<div class="alert alert-success text-center">${message}</div>
		</c:if>

		<form action="${pageContext.request.contextPath}/forgot-password"
			method="post">

			<div class="form-group mb-3">
				<label for="email" class="form-label">Email Address:</label> <input
					type="email" id="email" name="email" class="form-control" required />
				<button type="submit" name="action" value="sendOtp"
					class="auth-btn w-100 mt-2">SEND OTP</button>
			</div>

			<div class="form-group mb-3">
				<label for="otp" class="form-label">Enter OTP:</label> <input
					type="text" id="otp" name="otp" class="form-control" />
			</div>

			<div class="form-group mb-3">
				<label for="newPassword" class="form-label">New Password:</label>
				<div class="password-container">
					<input type="password" id="newPassword" name="newPassword"
						class="form-control" />
					<button type="button" class="toggle-password"
						onclick="togglePassword()">🧿</button>
				</div>
			</div>

			<button type="submit" name="action" value="resetPassword"
				class="auth-btn w-100">RESET PASSWORD</button>

			<div class="auth-links">
				<a href="${pageContext.request.contextPath}/jsp/login.jsp"
					class="auth-link">Back to Login</a>
			</div>
		</form>
	</div>

	<script>
		function togglePassword() {
			const passwordField = document.getElementById('newPassword');
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
