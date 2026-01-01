package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

// url mapping to books url
// The books servlet handles operations related to books of the LMS.
// Communicates with the book dao
@WebServlet("/books")
public class BookServlet extends HttpServlet {
	
	// Book dao used for book database operations
    private BookDAO bookDAO;

    // initializes book dao
    @Override
    public void init() {
        bookDAO = new BookDAO();
    }

    // Hanles get requests to book url.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";  
        }

        switch (action) {
            case "add":
                request.getRequestDispatcher("/jsp/books/addBook.jsp").forward(request, response);
                break;

            case "edit":
                String isbn = request.getParameter("isbn");
                Book book = bookDAO.getBookByIsbn(isbn);
                request.setAttribute("book", book);
                request.getRequestDispatcher("/jsp/books/editBook.jsp").forward(request, response);
                break;

            case "delete":
                isbn = request.getParameter("isbn");
                bookDAO.deleteBook(isbn);
                response.sendRedirect(request.getContextPath() + "/books");
                break;

            case "search":
            	String keyword = request.getParameter("keyword");
                if (keyword == null || keyword.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/books");
                    return;
                }
                
                List<Book> searchResults = bookDAO.searchBooks(keyword);
                request.setAttribute("books", searchResults);
                request.getRequestDispatcher("/jsp/books/listBooks.jsp").forward(request, response);
                break;
                
            case "listUserBooks":
                List<Book> availableBooks = bookDAO.getAvailableBooks();
                request.setAttribute("books", availableBooks);
                request.getRequestDispatcher("/jsp/books/userBooks.jsp").forward(request, response);
                break;
                
            case "searchUserBooks":
                String keyword2 = request.getParameter("keyword");
                if (keyword2 == null || keyword2.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/books?action=listUserBooks");
                    return;
                }

                List<Book> userSearchResults = bookDAO.searchAvailableBooks(keyword2);
                request.setAttribute("books", userSearchResults);
                request.getRequestDispatcher("/jsp/books/userBooks.jsp").forward(request, response);
                break;

            default:
                List<Book> listBooks = bookDAO.getAllBooks();
                request.setAttribute("books", listBooks);
                request.getRequestDispatcher("/jsp/books/listBooks.jsp").forward(request, response);
                break;
        }
    }

    // Handles post requests to book url
    // adding, updating book forms
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action) || "update".equals(action)) {
            
            String isbn = request.getParameter("isbn");
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String genre = request.getParameter("genre");
            String yearStr = request.getParameter("year");

            String errorMessage = validateBookInput(isbn, title, author, genre, yearStr, action);

            if (errorMessage != null) {
                request.setAttribute("error", errorMessage);
                request.setAttribute("book", new Book(isbn, title, author, genre,(yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : 0));

                if ("add".equals(action)) {
                    request.getRequestDispatcher("/jsp/books/addBook.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/jsp/books/editBook.jsp").forward(request, response);
                }
                return;
            }

            int year = Integer.parseInt(yearStr);

            Book book = new Book(isbn, title, author, genre, year);

            boolean success;
            if ("add".equals(action)) {
                if (bookDAO.getBookByIsbn(isbn) != null) {
                    request.setAttribute("error", "A book with this ISBN already exists.");
                    request.setAttribute("book", book);
                    request.getRequestDispatcher("/jsp/books/addBook.jsp").forward(request, response);
                    return;
                }
                success = bookDAO.addBook(book);
            } else {
                success = bookDAO.updateBook(book);
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/books");
            } else {
                request.setAttribute("error", "An unexpected error occurred while saving the book.");
                request.setAttribute("book", book);
                request.getRequestDispatcher("/jsp/books/" + ("add".equals(action) ? "addBook.jsp" : "editBook.jsp")).forward(request, response);
            }
        }
    }

    
    private String validateBookInput(String isbn, String title, String author, String genre, String yearStr, String action) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return "ISBN cannot be empty.";
        }

        if (!isbn.matches("\\d{10}|\\d{13}")) {
            return "ISBN must be 10 or 13 digits.";
        }

        if (title == null || title.trim().isEmpty()) {
            return "Title cannot be empty.";
        }

        if (title.length() > 225) {
            return "Title is too long (max 225 characters).";
        }

        if (author == null || author.trim().isEmpty()) {
            return "Author cannot be empty.";
        }

        if (genre == null || genre.trim().isEmpty()) {
            return "Genre cannot be empty.";
        }

        if (yearStr == null || yearStr.trim().isEmpty()) {
            return "Year cannot be empty.";
        }

        try {
            int year = Integer.parseInt(yearStr);
            int currentYear = java.time.Year.now().getValue();
            if (year < 1000 || year > currentYear) {
                return "Year must be between 1000 and " + currentYear + ".";
            }
        } catch (NumberFormatException e) {
            return "Year must be a valid number.";
        }

        return null; 
    }

}
