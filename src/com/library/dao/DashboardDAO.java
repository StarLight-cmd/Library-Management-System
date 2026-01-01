package com.library.dao;

import com.library.util.DBUtil;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardDAO {

    // CRUD Operations to get Admin report stats for LMS

    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) AS total FROM Books";
        return getCount(sql);
    }

    public int getActiveUsers() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE is_admin = 0";
        return getCount(sql);
    }

    public int getBooksBorrowedToday() {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed WHERE DATE(borrowed_date) = CURDATE() AND status = 'borrowed'";
        return getCount(sql);
    }

    public int getOverdueBooks() {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed " +
                     "WHERE (status = 'borrowed' AND DATEDIFF(CURDATE(), borrowed_date) > 7) ";
        return getCount(sql);
    }

    public int getCurrentlyBorrowedBooks() {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed WHERE status = 'borrowed'";
        return getCount(sql);
    }

    public int getAvailableBooks() {
        String sql = "SELECT COUNT(*) AS total FROM Books " +
                     "WHERE isbn NOT IN (SELECT isbn FROM Borrowed WHERE status = 'borrowed')";
        return getCount(sql);
    }


    // CRUD operations to get user report stats

    public int getBorrowedCountByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed WHERE user_id = ? AND status = 'borrowed'";
        return getCountWithParam(sql, userId);
    }

    public int getOverdueCountByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed " +
                     "WHERE user_id = ? AND status = 'borrowed' AND return_date < CURDATE()";
        return getCountWithParam(sql, userId);
    }

    public int getBooksReadByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM Borrowed WHERE user_id = ? AND status = 'returned'";
        return getCountWithParam(sql, userId);
    }

    public int getAvailableBooksForUser() {
        String sql = "SELECT COUNT(*) AS total FROM Books " +
                     "WHERE isbn NOT IN (SELECT isbn FROM Borrowed WHERE status = 'borrowed')";
        return getCount(sql);
    }

    public Map<String, Integer> getUserStats(int userId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("borrowed", getBorrowedCountByUser(userId));
        stats.put("overdue", getOverdueCountByUser(userId));
        stats.put("available", getAvailableBooksForUser());
        stats.put("read", getBooksReadByUser(userId));
        return stats;
    }


    // Stats helper methods for stats

    private int getCount(String sql) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
            	return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + sql);
            e.printStackTrace();
        }
        return 0;
    }

    private int getCountWithParam(String sql, int param) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, param);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
            	return rs.getInt("total");
            	}
        } catch (SQLException e) {
            System.out.println("Error executing query with param: " + sql);
            e.printStackTrace();
        }
        return 0;
    }
}
