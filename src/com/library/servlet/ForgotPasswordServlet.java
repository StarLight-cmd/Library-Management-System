package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.util.EmailUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.regex.Pattern;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String email = request.getParameter("email");

        // Static OTP for demo — ideally generated randomly and stored in session
        final String STATIC_OTP = "6768";

        if ("sendOtp".equals(action)) {
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Please enter your email to receive an OTP.");
                request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
                return;
            }

            // Validate email format
            String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            if (!Pattern.matches(emailRegex, email)) {
                request.setAttribute("error", "Please enter a valid email address.");
                request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
                return;
            }

            // Send OTP email
            EmailUtil.sendEmail(email, "Library Password Reset OTP",
                    "Dear user,\n\nYour OTP for password reset is: " + STATIC_OTP +
                            "\n\nWarm Regards,\nLibrary Management System Team");

            request.setAttribute("message", "OTP sent to your email. Please check your inbox.");
            request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
            return;
        }

        if ("resetPassword".equals(action)) {
            String newPassword = request.getParameter("newPassword");
            String otp = request.getParameter("otp");

            // Validate inputs
            String validationError = validateInputs(email, newPassword);
            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
                return;
            }

            // Verify OTP
            if (!STATIC_OTP.equals(otp)) {
                request.setAttribute("error", "Invalid OTP. Please check your email and try again.");
                request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
                return;
            }

            // Reset password
            if (userDAO.resetPassword(email, newPassword)) {
                request.setAttribute("message", "Password updated successfully! You may now log in.");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Failed to reset password. Please verify your email.");
                request.getRequestDispatcher("/jsp/forgotPassword.jsp").forward(request, response);
            }
        }
    }

    private String validateInputs(String email, String password) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            return "Please enter a valid email address.";
        }

        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$";
        if (password == null || !Pattern.matches(passwordRegex, password)) {
            return "Password must be at least 8 characters long, contain one uppercase letter, one number, and one special character.";
        }

        return null;
    }
}
