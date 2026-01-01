package com.library.dao;

import com.library.util.PasswordUtil;
import com.library.model.User;
import com.library.util.DBUtil;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
	// user data access object used to perform user CRUD operations
	// registering a user
	public boolean register(User user) {
	    String sqlCheck = "SELECT COUNT(*) FROM Users WHERE email = ?";
	    String sqlInsert = "INSERT INTO Users (fullname, email, password, is_admin) VALUES (?, ?, ?, ?)";

	    try (Connection conn = DBUtil.getConnection()) {
	       
	        try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
	            checkStmt.setString(1, user.getEmail());
	            ResultSet rs = checkStmt.executeQuery();
	            if (rs.next() && rs.getInt(1) > 0) {
	                System.out.println("Email already exists.");
	                return false; 
	            }
	        }

	       
	        try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
	            String hashedPassword = PasswordUtil.hashPassword(user.getPassword());

	            insertStmt.setString(1, user.getFullname());
	            insertStmt.setString(2, user.getEmail());
	            insertStmt.setString(3, hashedPassword);
	            insertStmt.setBoolean(4, user.isAdmin());

	            return insertStmt.executeUpdate() > 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	// Logging in a user
    public User login(String email, String plainPassword) {
        String sqlSelect = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {

            psStmt.setString(1, email);
            ResultSet rs = psStmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (PasswordUtil.checkPassword(plainPassword, hashedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullname(rs.getString("fullname"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(hashedPassword);
                    user.setAdmin(rs.getBoolean("is_admin"));
                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Reseting a users password
    public boolean resetPassword(String email, String newPassword) {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        String sqlUpdate = "UPDATE Users SET password = ? WHERE email = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlUpdate)) {

            psStmt.setString(1, hashedPassword);
            psStmt.setString(2, email);
            return psStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // listing all users in the library management system
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sqlSelect = "SELECT * FROM Users";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlSelect)) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullname(rs.getString("fullname"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setAdmin(rs.getBoolean("is_admin"));
                users.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // Getting a user by their user id
    public User getUserById(int id) {
        String sqlSelect = "SELECT * FROM Users WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psStmt = conn.prepareStatement(sqlSelect)) {

            psStmt.setInt(1, id);
            ResultSet rs = psStmt.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullname(rs.getString("fullname"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setAdmin(rs.getBoolean("is_admin"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
