package com.library.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

// url mapping for logout url
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	// handles user logouts via get requests
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); 
        if (session != null) {
            session.invalidate(); 
        }
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
    }
}
