package com.library.servlet;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import com.library.model.User;

@WebServlet("/Fines")
public class FinesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3307/lms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final double DAILY_FINE_RATE = 2.50;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("Fines - Action: " + action);
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied. Please login as admin.");
            return;
        }
        
        try {
            if (action == null) {
                viewFinesPage(request, response);
                return;
            }
            
            switch (action) {
                case "userFines":
                    getUserFines(request, response);
                    break;
                case "allFines":
                    getAllFines(request, response);
                    break;
                case "paymentHistory":
                    getPaymentHistory(request, response);
                    break;
                case "calculateOverdueFines":
                    calculateOverdueFines(request, response); 
                    break;
                case "viewFines":
                    viewFinesPage(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("Fines POST - Action: " + action);
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access denied. Please login as admin.");
            return;
        }
        
        try {
            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action parameter is required");
                return;
            }
            
            switch (action) {
                case "processPayment":
                    processPayment(request, response);
                    break;
                case "adjustFine":
                    adjustFine(request, response);
                    break;
                case "waiveFine":
                    waiveFine(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void viewFinesPage(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT f.id, u.fullname as userFullName, u.email as userEmail, " +
                        "b.title as bookTitle, f.amount, f.overdue_days as overdueDays, " +
                        "f.fine_date as fineDate, f.due_date as dueDate, f.status " +
                        "FROM fines f " +
                        "JOIN users u ON f.user_id = u.id " +
                        "JOIN borrowed br ON f.borrow_id = br.id " +
                        "JOIN books b ON br.isbn = b.isbn " +
                        "ORDER BY f.fine_date DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            List<Map<String, Object>> finesList = new ArrayList<>();
            int totalFinesCount = 0;
            int pendingFinesCount = 0;
            int paidFinesCount = 0;
            double totalFinesAmount = 0;
            
            while (rs.next()) {
                Map<String, Object> fine = new HashMap<>();
                fine.put("id", rs.getInt("id"));
                fine.put("userFullName", rs.getString("userFullName"));
                fine.put("userEmail", rs.getString("userEmail"));
                fine.put("bookTitle", rs.getString("bookTitle"));
                fine.put("amount", rs.getDouble("amount"));
                fine.put("overdueDays", rs.getInt("overdueDays"));
                fine.put("fineDate", rs.getDate("fineDate"));
                fine.put("dueDate", rs.getDate("dueDate"));
                fine.put("status", rs.getString("status"));
                
                finesList.add(fine);
                
                totalFinesCount++;
                totalFinesAmount += rs.getDouble("amount");
                
                if ("pending".equals(rs.getString("status"))) {
                    pendingFinesCount++;
                } else if ("paid".equals(rs.getString("status"))) {
                    paidFinesCount++;
                }
            }
            
            request.setAttribute("finesList", finesList);
            request.setAttribute("totalFinesCount", totalFinesCount);
            request.setAttribute("pendingFinesCount", pendingFinesCount);
            request.setAttribute("paidFinesCount", paidFinesCount);
            request.setAttribute("totalFinesAmount", totalFinesAmount);
            
            request.getRequestDispatcher("/jsp/fines.jsp").forward(request, response);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void getUserFines(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT f.id, f.borrow_id, b.title, f.amount, f.overdue_days, " +
                        "f.fine_date, f.due_date, f.status, f.created_at " +
                        "FROM fines f " +
                        "JOIN borrowed br ON f.borrow_id = br.id " +
                        "JOIN books b ON br.isbn = b.isbn " +
                        "WHERE f.user_id = ? " +
                        "ORDER BY f.fine_date DESC";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getId());
            rs = pstmt.executeQuery();
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<div class='card dashboard-card'>");
            out.println("<div class='card-header'><i class='fas fa-money-bill-wave me-2'></i>My Fines</div>");
            out.println("<div class='card-body'>");
            
            out.println("<div class='table-responsive'>");
            out.println("<table class='data-table'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Book</th>");
            out.println("<th>Fine Amount</th>");
            out.println("<th>Overdue Days</th>");
            out.println("<th>Fine Date</th>");
            out.println("<th>Due Date</th>");
            out.println("<th>Status</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            double totalPending = 0;
            int fineCount = 0;
            
            while (rs.next()) {
                fineCount++;
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                
                if ("pending".equals(status)) {
                    totalPending += amount;
                }
                
                out.println("<tr>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td><strong>R " + String.format("%.2f", amount) + "</strong></td>");
                out.println("<td>" + rs.getInt("overdue_days") + " days</td>");
                out.println("<td>" + rs.getDate("fine_date") + "</td>");
                out.println("<td>" + rs.getDate("due_date") + "</td>");
                out.println("<td>");
                if ("pending".equals(status)) {
                    out.println("<span class='badge bg-warning'>Pending</span>");
                } else if ("paid".equals(status)) {
                    out.println("<span class='badge bg-success'>Paid</span>");
                } else if ("waived".equals(status)) {
                    out.println("<span class='badge bg-info'>Waived</span>");
                }
                out.println("</td>");
                out.println("</tr>");
            }
            
            if (fineCount == 0) {
                out.println("<tr><td colspan='6' class='text-center py-4'>No fines found</td></tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            
            if (totalPending > 0) {
                out.println("<div class='alert alert-warning mt-3'>");
                out.println("<strong>Total Pending Fines: R " + String.format("%.2f", totalPending) + "</strong>");
                out.println("<br><small>Please visit the library front desk to settle your fines.</small>");
                out.println("</div>");
            }
            
            out.println("</div></div>");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void getAllFines(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql = "SELECT f.id, u.fullname, u.email, b.title, f.amount, " +
                        "f.overdue_days, f.fine_date, f.due_date, f.status, " +
                        "f.created_at, br.borrowed_date, br.return_date " +
                        "FROM fines f " +
                        "JOIN users u ON f.user_id = u.id " +
                        "JOIN borrowed br ON f.borrow_id = br.id " +
                        "JOIN books b ON br.isbn = b.isbn " +
                        "ORDER BY f.fine_date DESC, f.status ASC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<div class='card dashboard-card'>");
            out.println("<div class='card-header'><i class='fas fa-money-bill-wave me-2'></i>Fines Management</div>");
            out.println("<div class='card-body'>");
            
            out.println("<div class='row mb-4'>");
            out.println("<div class='col-md-3'>");
            out.println("<div class='stat-card-report'>");
            out.println("<span class='stat-number-report' id='totalFines'>0</span>");
            out.println("<span class='stat-label-report'>Total Fines</span>");
            out.println("</div></div>");
            out.println("<div class='col-md-3'>");
            out.println("<div class='stat-card-report'>");
            out.println("<span class='stat-number-report' id='pendingFines'>0</span>");
            out.println("<span class='stat-label-report'>Pending</span>");
            out.println("</div></div>");
            out.println("<div class='col-md-3'>");
            out.println("<div class='stat-card-report'>");
            out.println("<span class='stat-number-report' id='paidFines'>0</span>");
            out.println("<span class='stat-label-report'>Paid</span>");
            out.println("</div></div>");
            out.println("<div class='col-md-3'>");
            out.println("<div class='stat-card-report'>");
            out.println("<span class='stat-number-report' id='totalAmount'>R 0</span>");
            out.println("<span class='stat-label-report'>Total Amount</span>");
            out.println("</div></div>");
            out.println("</div>");
            
            out.println("<div class='table-responsive'>");
            out.println("<table class='data-table'>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>User</th>");
            out.println("<th>Book</th>");
            out.println("<th>Amount</th>");
            out.println("<th>Overdue Days</th>");
            out.println("<th>Fine Date</th>");
            out.println("<th>Status</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            double totalAmount = 0;
            double pendingAmount = 0;
            double paidAmount = 0;
            int totalFines = 0;
            int pendingFines = 0;
            int paidFines = 0;
            
            while (rs.next()) {
                totalFines++;
                double amount = rs.getDouble("amount");
                String status = rs.getString("status");
                
                totalAmount += amount;
                
                if ("pending".equals(status)) {
                    pendingFines++;
                    pendingAmount += amount;
                } else if ("paid".equals(status)) {
                    paidFines++;
                    paidAmount += amount;
                }
                
                out.println("<tr>");
                out.println("<td>");
                out.println("<div><strong>" + rs.getString("fullname") + "</strong></div>");
                out.println("<div><small class='text-muted'>" + rs.getString("email") + "</small></div>");
                out.println("</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td><strong>R " + String.format("%.2f", amount) + "</strong></td>");
                out.println("<td>" + rs.getInt("overdue_days") + " days</td>");
                out.println("<td>" + rs.getDate("fine_date") + "</td>");
                out.println("<td>");
                if ("pending".equals(status)) {
                    out.println("<span class='badge bg-warning'>Pending</span>");
                } else if ("paid".equals(status)) {
                    out.println("<span class='badge bg-success'>Paid</span>");
                } else if ("waived".equals(status)) {
                    out.println("<span class='badge bg-info'>Waived</span>");
                }
                out.println("</td>");
                out.println("<td>");
                if ("pending".equals(status)) {
                    out.println("<button class='btn btn-sm btn-success me-1' onclick='showPaymentModal(" + rs.getInt("id") + ", " + amount + ", \"" + rs.getString("fullname") + "\")'>");
                    out.println("<i class='fas fa-credit-card'></i> Pay");
                    out.println("</button>");
                    out.println("<button class='btn btn-sm btn-warning me-1' onclick='showAdjustModal(" + rs.getInt("id") + ", " + amount + ", \"" + rs.getString("fullname") + "\")'>");
                    out.println("<i class='fas fa-edit'></i> Adjust");
                    out.println("</button>");
                    out.println("<button class='btn btn-sm btn-info' onclick='waiveFine(" + rs.getInt("id") + ", \"" + rs.getString("fullname") + "\")'>");
                    out.println("<i class='fas fa-hand-holding-usd'></i> Waive");
                    out.println("</button>");
                } else {
                    out.println("<span class='text-muted'>No actions</span>");
                }
                out.println("</td>");
                out.println("</tr>");
            }
            
            if (totalFines == 0) {
                out.println("<tr><td colspan='7' class='text-center py-4'>No fines found</td></tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            
            out.println("</div></div>");
            
            out.println("<script>");
            out.println("document.getElementById('totalFines').textContent = '" + totalFines + "';");
            out.println("document.getElementById('pendingFines').textContent = '" + pendingFines + "';");
            out.println("document.getElementById('paidFines').textContent = '" + paidFines + "';");
            out.println("document.getElementById('totalAmount').textContent = 'R " + String.format("%.2f", totalAmount) + "';");
            out.println("</script>");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void processPayment(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }
        
        int fineId = Integer.parseInt(request.getParameter("fineId"));
        double amountPaid = Double.parseDouble(request.getParameter("amountPaid"));
        String paymentMethod = request.getParameter("paymentMethod");
        String notes = request.getParameter("notes");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);
            
            String paymentSql = "INSERT INTO payments (fine_id, user_id, amount_paid, payment_method, " +
                               "transaction_id, receipt_number, status, processed_by, notes) " +
                               "SELECT ?, user_id, ?, ?, CONCAT('PAY_', UUID()), CONCAT('RCPT_', DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s')), " +
                               "'completed', ?, ? FROM fines WHERE id = ?";
            
            pstmt = conn.prepareStatement(paymentSql);
            pstmt.setInt(1, fineId);
            pstmt.setDouble(2, amountPaid);
            pstmt.setString(3, paymentMethod);
            pstmt.setInt(4, user.getId());
            pstmt.setString(5, notes);
            pstmt.setInt(6, fineId);
            pstmt.executeUpdate();
            pstmt.close();
            
            String fineSql = "UPDATE fines SET status = 'paid', updated_at = NOW() WHERE id = ?";
            pstmt = conn.prepareStatement(fineSql);
            pstmt.setInt(1, fineId);
            pstmt.executeUpdate();
            
            conn.commit();
            
            response.sendRedirect(request.getContextPath() + "/Fines?action=viewFines");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void calculateOverdueFines(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            System.out.println("=== Starting Fines Calculation ===");
            
            String debugSql = "SELECT br.id, br.user_id, b.title, br.due_date, " +
                             "DATEDIFF(CURDATE(), br.due_date) as overdue_days, " +
                             "br.return_date, br.status " +
                             "FROM borrowed br " +
                             "JOIN books b ON br.isbn = b.isbn " +
                             "WHERE br.return_date IS NULL " +
                             "AND br.due_date < CURDATE() " +
                             "ORDER BY br.due_date";
            
            pstmt = conn.prepareStatement(debugSql);
            rs = pstmt.executeQuery();
            
            System.out.println("=== Potential Fines Candidates ===");
            while (rs.next()) {
                System.out.println("Borrow ID: " + rs.getInt("id") + 
                                 ", User: " + rs.getInt("user_id") + 
                                 ", Title: " + rs.getString("title") + 
                                 ", Due: " + rs.getDate("due_date") + 
                                 ", Overdue: " + rs.getInt("overdue_days") + " days" +
                                 ", Status: " + rs.getString("status"));
            }
            rs.close();
            pstmt.close();
            
            String sql = "SELECT br.id, br.user_id, br.borrowed_date, br.due_date, " +
                        "DATEDIFF(CURDATE(), br.due_date) as overdue_days, " +
                        "b.title " +
                        "FROM borrowed br " +
                        "JOIN books b ON br.isbn = b.isbn " +
                        "WHERE br.return_date IS NULL " +
                        "AND br.due_date < CURDATE() " +
                        "AND br.status != 'RETURNED' " +
                        "AND NOT EXISTS (SELECT 1 FROM fines f WHERE f.borrow_id = br.id) " +
                        "AND DATEDIFF(CURDATE(), br.due_date) > 0";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            int finesCreated = 0;
            
            while (rs.next()) {
                int overdueDays = rs.getInt("overdue_days");
                double fineAmount = overdueDays * DAILY_FINE_RATE;
                
                System.out.println("Creating fine: BorrowID=" + rs.getInt("id") + 
                                 ", User=" + rs.getInt("user_id") + 
                                 ", Amount=R" + fineAmount + 
                                 ", Days=" + overdueDays);
                
                String insertSql = "INSERT INTO fines (borrow_id, user_id, amount, daily_fine_rate, " +
                                 "overdue_days, fine_date, due_date, status) VALUES (?, ?, ?, ?, ?, CURDATE(), ?, 'pending')";
                
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, rs.getInt("id"));
                insertStmt.setInt(2, rs.getInt("user_id"));
                insertStmt.setDouble(3, fineAmount);
                insertStmt.setDouble(4, DAILY_FINE_RATE);
                insertStmt.setInt(5, overdueDays);
                insertStmt.setDate(6, rs.getDate("due_date"));
                insertStmt.executeUpdate();
                insertStmt.close();
                
                finesCreated++;
            }
            
            System.out.println("=== Fines Created: " + finesCreated + " ===");
            
            response.sendRedirect(request.getContextPath() + "/Fines?action=viewFines");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void adjustFine(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }
        
        int fineId = Integer.parseInt(request.getParameter("fineId"));
        double newAmount = Double.parseDouble(request.getParameter("newAmount"));
        String reason = request.getParameter("reason");
        String notes = request.getParameter("notes");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);
            
            String selectSql = "SELECT amount FROM fines WHERE id = ?";
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setInt(1, fineId);
            rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Fine not found");
                return;
            }
            
            double previousAmount = rs.getDouble("amount");
            pstmt.close();
            
            String updateSql = "UPDATE fines SET amount = ?, updated_at = NOW() WHERE id = ?";
            pstmt = conn.prepareStatement(updateSql);
            pstmt.setDouble(1, newAmount);
            pstmt.setInt(2, fineId);
            pstmt.executeUpdate();
            pstmt.close();
            
            String adjustSql = "INSERT INTO fine_adjustments (fine_id, adjusted_by, previous_amount, new_amount, adjustment_reason, notes) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(adjustSql);
            pstmt.setInt(1, fineId);
            pstmt.setInt(2, user.getId());
            pstmt.setDouble(3, previousAmount);
            pstmt.setDouble(4, newAmount);
            pstmt.setString(5, reason);
            pstmt.setString(6, notes);
            pstmt.executeUpdate();
            
            conn.commit();
            
            response.sendRedirect(request.getContextPath() + "/Fines?action=viewFines");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void waiveFine(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }
        
        int fineId = Integer.parseInt(request.getParameter("fineId"));
        String reason = request.getParameter("reason");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);
            
            String fineSql = "UPDATE fines SET status = 'waived', updated_at = NOW() WHERE id = ?";
            pstmt = conn.prepareStatement(fineSql);
            pstmt.setInt(1, fineId);
            pstmt.executeUpdate();
            pstmt.close();
            
            String paymentSql = "INSERT INTO payments (fine_id, user_id, amount_paid, payment_method, " +
                               "transaction_id, receipt_number, status, processed_by, notes) " +
                               "SELECT ?, user_id, 0.00, 'waiver', CONCAT('WAIVE_', UUID()), CONCAT('WAIVE_', DATE_FORMAT(NOW(), '%Y%m%d_%H%i%s')), " +
                               "'completed', ?, ? FROM fines WHERE id = ?";
            
            pstmt = conn.prepareStatement(paymentSql);
            pstmt.setInt(1, fineId);
            pstmt.setInt(2, user.getId());
            pstmt.setString(3, "Fine waived: " + reason);
            pstmt.setInt(4, fineId);
            pstmt.executeUpdate();
            
            conn.commit();
            
            response.sendRedirect(request.getContextPath() + "/Fines?action=viewFines");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }

    private void getPaymentHistory(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            String sql;
            if (user != null && user.isAdmin()) {
                sql = "SELECT p.id, u.fullname, b.title, p.amount_paid, p.payment_method, " +
                      "p.payment_date, p.transaction_id, p.receipt_number, p.status, " +
                      "admin.fullname as processed_by, p.notes " +
                      "FROM payments p " +
                      "JOIN fines f ON p.fine_id = f.id " +
                      "JOIN users u ON p.user_id = u.id " +
                      "JOIN borrowed br ON f.borrow_id = br.id " +
                      "JOIN books b ON br.isbn = b.isbn " +
                      "LEFT JOIN users admin ON p.processed_by = admin.id " +
                      "ORDER BY p.payment_date DESC";
                pstmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT p.id, b.title, p.amount_paid, p.payment_method, " +
                      "p.payment_date, p.transaction_id, p.receipt_number, p.status, p.notes " +
                      "FROM payments p " +
                      "JOIN fines f ON p.fine_id = f.id " +
                      "JOIN borrowed br ON f.borrow_id = br.id " +
                      "JOIN books b ON br.isbn = b.isbn " +
                      "WHERE p.user_id = ? " +
                      "ORDER BY p.payment_date DESC";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, user.getId());
            }
            
            rs = pstmt.executeQuery();
            
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<div class='card dashboard-card'>");
            if (user != null && user.isAdmin()) {
                out.println("<div class='card-header'><i class='fas fa-history me-2'></i>All Payment History</div>");
            } else {
                out.println("<div class='card-header'><i class='fas fa-history me-2'></i>My Payment History</div>");
            }
            out.println("<div class='card-body'>");
            
            out.println("<div class='table-responsive'>");
            out.println("<table class='data-table'>");
            out.println("<thead>");
            out.println("<tr>");
            if (user != null && user.isAdmin()) {
                out.println("<th>User</th>");
            }
            out.println("<th>Book</th>");
            out.println("<th>Amount Paid</th>");
            out.println("<th>Payment Method</th>");
            out.println("<th>Payment Date</th>");
            out.println("<th>Receipt Number</th>");
            out.println("<th>Status</th>");
            if (user != null && user.isAdmin()) {
                out.println("<th>Processed By</th>");
            }
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            int paymentCount = 0;
            double totalPaid = 0;
            
            while (rs.next()) {
                paymentCount++;
                double amountPaid = rs.getDouble("amount_paid");
                totalPaid += amountPaid;
                
                out.println("<tr>");
                if (user != null && user.isAdmin()) {
                    out.println("<td>" + rs.getString("fullname") + "</td>");
                }
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td><strong>R " + String.format("%.2f", amountPaid) + "</strong></td>");
                out.println("<td>" + rs.getString("payment_method") + "</td>");
                out.println("<td>" + rs.getTimestamp("payment_date") + "</td>");
                out.println("<td>" + rs.getString("receipt_number") + "</td>");
                out.println("<td><span class='badge bg-success'>" + rs.getString("status") + "</span></td>");
                if (user != null && user.isAdmin()) {
                    out.println("<td>" + (rs.getString("processed_by") != null ? rs.getString("processed_by") : "System") + "</td>");
                }
                out.println("</tr>");
            }
            
            if (paymentCount == 0) {
                out.println("<tr><td colspan='" + (user != null && user.isAdmin() ? 8 : 6) + "' class='text-center py-4'>No payment history found</td></tr>");
            }
            
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            
            if (paymentCount > 0) {
                out.println("<div class='mt-3 p-3 bg-light rounded'>");
                out.println("<strong>Total Payments: " + paymentCount + " | Total Amount: R " + String.format("%.2f", totalPaid) + "</strong>");
                out.println("</div>");
            }
            
            out.println("</div></div>");
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found", e);
        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        }
    }
}