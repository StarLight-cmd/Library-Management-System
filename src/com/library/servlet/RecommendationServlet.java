package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

// URL mapping to LMS recommendation system
@WebServlet("/recommendations")
public class RecommendationServlet extends HttpServlet {
	// Book DAO for database operations
    private BookDAO bookDAO;

    // initializes bookDAO
    @Override
    public void init() {
        bookDAO = new BookDAO();
    }

    // Handles get requests to recommendation system
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute("user");

        if (user == null || user.isAdmin()) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        List<Book> suggestions = bookDAO.getBookSuggestions(user.getId());
        request.setAttribute("suggestedBooks", suggestions);
        request.getRequestDispatcher("/jsp/books/recommendations.jsp").forward(request, response);
    }
}
