package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

// url mapping to users url
@WebServlet("/users")
public class UserServlet extends HttpServlet {
	// userDAO for database operations
    private UserDAO userDAO;

    // initializes user DAO
    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    // handles GET requests to user url
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
        	action = "list";
        	}

        switch (action) {
            case "details":
                int id = Integer.parseInt(request.getParameter("id"));
                User user = userDAO.getUserById(id);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/jsp/users/userDetails.jsp").forward(request, response);
                break;

            default:
                List<User> users = userDAO.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("/jsp/users/listUsers.jsp").forward(request, response);
                break;
        }
    }
}
