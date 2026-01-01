package com.library.dao;

import com.library.model.Book;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

	// Book data access object performs CRUD operations for books
	// adding a new book
    public boolean addBook(Book book) {
        String sqlInsert = "INSERT INTO books (isbn, title, author, genre, year) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlInsert)) {

            psStmt.setString(1, book.getIsbn());
            psStmt.setString(2, book.getTitle());
            psStmt.setString(3, book.getAuthor());
            psStmt.setString(4, book.getGenre());
            psStmt.setInt(5, book.getYear());
            return psStmt.executeUpdate() > 0;

        } catch (SQLException e) {
        	System.out.println("SQL Error while adding book to library management system:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    // getting all books from the database
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sqlSelect = "SELECT * FROM books";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelect)) {

            while (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getString("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setGenre(rs.getString("genre"));
                b.setYear(rs.getInt("year"));
                books.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Getting a specific book by isbn
    public Book getBookByIsbn(String isbn) {
        String sqlSelect = "SELECT * FROM books WHERE isbn=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {
            psStmt.setString(1, isbn);
            ResultSet rs = psStmt.executeQuery();
            if (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getString("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setGenre(rs.getString("genre"));
                b.setYear(rs.getInt("year"));
                return b;
            }
        } catch (SQLException e) {
        	System.out.println("SQL Error while searching for book in library management system:");
            System.out.println("Message: " + e.getMessage());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
        return null;
    }

    // Updating a book in the database
    public boolean updateBook(Book book) {
        String sqlUpdate = "UPDATE books SET title=?, author=?, genre=?, year=? WHERE isbn=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlUpdate)) {

            psStmt.setString(1, book.getTitle());
            psStmt.setString(2, book.getAuthor());
            psStmt.setString(3, book.getGenre());
            psStmt.setInt(4, book.getYear());
            psStmt.setString(5, book.getIsbn());
            return psStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // deleting a book in the database
    public boolean deleteBook(String isbn) {
        String sqlDelete = "DELETE FROM books WHERE isbn=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlDelete)) {
            psStmt.setString(1, isbn);
            return psStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Getting all books which are available for borrowing 
    public List<Book> getAvailableBooks() {
        List<Book> books = new ArrayList<>();
        String sqlSelect = "SELECT * FROM books WHERE isbn NOT IN (SELECT isbn FROM Borrowed WHERE status = 'Borrowed')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect);
             ResultSet rs = psStmt.executeQuery()) {

            while (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getString("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setGenre(rs.getString("genre"));
                b.setYear(rs.getInt("year"));
                books.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Searching for a book in the book catalogue
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sqlSelect = "SELECT * FROM Books WHERE isbn LIKE ? OR title LIKE ? OR author LIKE ? OR genre LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {

            String searchPattern = "%" + keyword + "%";
            psStmt.setString(1, searchPattern);
            psStmt.setString(2, searchPattern);
            psStmt.setString(3, searchPattern);
            psStmt.setString(4, searchPattern);

            ResultSet rs = psStmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("year")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
    
    // Recommending books to users based on borrowing history
    public List<Book> getBookSuggestions(int userId) {
        List<Book> suggestedBooks = new ArrayList<>();

        String sql = 
        		"SELECT * FROM Books " +
        	            "WHERE (genre IN (" +
        	            "   SELECT DISTINCT genre FROM Books " +
        	            "   WHERE isbn IN (SELECT isbn FROM Borrowed WHERE user_id = ?) " +
        	            "   AND genre IS NOT NULL" +
        	            ") OR author IN (" +
        	            "   SELECT DISTINCT author FROM Books " +
        	            "   WHERE isbn IN (SELECT isbn FROM Borrowed WHERE user_id = ?)" +
        	            ")) " +
        	            "AND isbn NOT IN (SELECT isbn FROM Borrowed WHERE user_id = ?) " +
        	            "ORDER BY year DESC " +
        	            "LIMIT 5";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sql)) {

            psStmt.setInt(1, userId);
            psStmt.setInt(2, userId);
            psStmt.setInt(3, userId);
            ResultSet rs = psStmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setIsbn(rs.getString("isbn"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setGenre(rs.getString("genre"));
                book.setYear(rs.getInt("year"));
                suggestedBooks.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggestedBooks;
    }
    
 // Searching only available books by isbn, title, author, or genre
    public List<Book> searchAvailableBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sqlSelect = 
            "SELECT * FROM books " +
            "WHERE isbn NOT IN (SELECT isbn FROM Borrowed WHERE status = 'Borrowed') " +
            "AND (title LIKE ? OR author LIKE ? OR genre LIKE ? OR isbn LIKE ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {

            String searchPattern = "%" + keyword + "%";
            psStmt.setString(1, searchPattern);
            psStmt.setString(2, searchPattern);
            psStmt.setString(3, searchPattern);
            psStmt.setString(4, searchPattern);

            ResultSet rs = psStmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("genre"),
                    rs.getInt("year")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }


}
