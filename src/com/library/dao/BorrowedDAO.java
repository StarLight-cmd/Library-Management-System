package com.library.dao;

import com.library.model.Borrowed;
import com.library.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowedDAO {

	// Borrowed Data access object used to perform CRUD operations for Borrowed db records
	// Borrowing a book
	public boolean borrowBook(int userId, String isbn) {
	    String sqlCheck = "SELECT COUNT(*) FROM Borrowed WHERE isbn = ? AND status = 'Borrowed'";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement psStmt = conn.prepareStatement(sqlCheck)) {
	        psStmt.setString(1, isbn);
	        ResultSet rs = psStmt.executeQuery();
	        if (rs.next() && rs.getInt(1) > 0) {
	            return false; 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }

	    String sqlInsert = "INSERT INTO Borrowed (user_id, isbn, borrowed_date, status) VALUES (?, ?, CURDATE(), 'Borrowed')";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement psStmt = conn.prepareStatement(sqlInsert)) {
	        psStmt.setInt(1, userId);
	        psStmt.setString(2, isbn);
	        return psStmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	// Getting the list of books borrowed by a user
    public List<Borrowed> getBorrowedBooksByUser(int userId) {
        List<Borrowed> borrowedList = new ArrayList<>();
        String sqlSelect = "SELECT * FROM Borrowed WHERE user_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {

            psStmt.setInt(1, userId);
            ResultSet rs = psStmt.executeQuery();

            while (rs.next()) {
                Borrowed b = new Borrowed();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setIsbn(rs.getString("isbn"));
                b.setBorrowedDate(rs.getDate("borrowed_date"));
                b.setReturnDate(rs.getDate("return_date"));
                b.setStatus(rs.getString("status"));
                borrowedList.add(b);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowedList;
    }

    // returning a book
    public boolean returnBook(int id) {
        String sqlUpdate = "UPDATE Borrowed SET return_date = CURDATE(), status = 'Returned' WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlUpdate)) {

            psStmt.setInt(1, id);
            return psStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
