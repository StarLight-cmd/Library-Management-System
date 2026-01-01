package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.regex.Pattern;

// communicates with the user dao to interact with the database
//URL mapping
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    // userDAO used to perform database operations
    private UserDAO userDAO;

    // initializes user dao
    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    // handles authentication post requests such as login, registration, and password resets
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                request.setAttribute("error", "Email and password cannot be empty.");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                return;
            }

            User user = userDAO.login(email, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                if (user.isAdmin()) {
                    response.sendRedirect("jsp/adminDashboard.jsp");
                } else {
                    response.sendRedirect("jsp/userDashboard.jsp");
                }
            } else {
                request.setAttribute("error", "Invalid email or password.");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            }

        } else if ("register".equals(action)) {
            // capture form input
            String fullname = request.getParameter("fullname");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // perform input validation
            String validationError = validateRegistration(fullname, email, password);

            if (validationError != null) {
                request.setAttribute("error", validationError);
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
                return;
            }

            // create user model if validation passes
            User user = new User();
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPassword(password);
            user.setAdmin(false);

            if (userDAO.register(user)) {
                request.setAttribute("message", "Registration successful! Please login.");
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Registration failed. Email may already exist.");
                request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            }

        } else if ("forgot".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/jsp/forgotPassword.jsp");
        }
    }

    // helper method to validate registration fields
    private String validateRegistration(String fullname, String email, String password) {
        // full name validation
        if (fullname == null || fullname.trim().isEmpty()) {
            return "Full name cannot be empty.";
        }

        // ensure name does not contain numbers or special characters
        if (!fullname.matches("^[A-Za-z ]+$")) {
            return "Full name cannot contain numbers or special characters.";
        }
        
        // email validation (basic regex)
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            return "Please enter a valid email address.";
        }

        // password validation
        // requires at least 8 characters, one special character, one digit, and one uppercase letter
        String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$";
        if (password == null || !Pattern.matches(passwordRegex, password)) {
            return "Password must be at least 8 characters long and contain one uppercase letter, one number, and one special character.";
        }

        return null; // no validation errors
    }
}
