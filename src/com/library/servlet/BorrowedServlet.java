package com.library.servlet;

import com.library.dao.BorrowedDAO;
import com.library.model.Borrowed;
import com.library.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Handles operations for borrowing and returning books
// URL mappings
@WebServlet("/borrowed")
public class BorrowedServlet extends HttpServlet {

	// Creating borrowed data access object to interact with the database
    private BorrowedDAO borrowedDAO;

    @Override
    public void init() {
        borrowedDAO = new BorrowedDAO();
    }

    // Get method to handle requests to return borrowed books
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        // Returning book
        if ("return".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            borrowedDAO.returnBook(id);
        }

        // Getting list of borrowed books
        List<Borrowed> borrowedBooks = borrowedDAO.getBorrowedBooksByUser(user.getId());

        // Fine calculation for books returned after a seven day period
        for (Borrowed b : borrowedBooks) {
            try {
                LocalDate borrowedDate = b.getBorrowedDate().toLocalDate();
                LocalDate returnDate = (b.getReturnDate() != null) ? b.getReturnDate().toLocalDate(): LocalDate.now();

                long daysBetween = ChronoUnit.DAYS.between(borrowedDate, returnDate);
                if (daysBetween > 7) {
                    request.setAttribute("fine_" + b.getId(), 75.0);
                } else {
                    request.setAttribute("fine_" + b.getId(), 0.0);
                }
            } catch (Exception ignored) {}
        }

        request.setAttribute("borrowedBooks", borrowedBooks);
        request.getRequestDispatcher("/jsp/books/borrowedBooks.jsp").forward(request, response);
    }

    // Post method to handle the borrowing of books.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("jsp/login.jsp");
            return;
        }

        String isbn = request.getParameter("isbn");
        borrowedDAO.borrowBook(user.getId(), isbn);

        response.sendRedirect(request.getContextPath() + "/borrowed");
    }
}
