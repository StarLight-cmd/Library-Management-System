package com.library.servlet;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import com.library.model.User;
import java.sql.Date;

@WebServlet("/Reports")
public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3307/lms";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        System.out.println("Reports action: " + action);
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }
        
        // Handle export requests
        if ("export".equals(action)) {
            try {
				handleExport(request, response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;
        }
        
        // If no action, show the main reports page
        if (action == null) {
            request.getRequestDispatcher("/jsp/reports.jsp").forward(request, response);
            return;
        }
        
        try {
            switch (action) {
                case "borrowHistory":
                    showBorrowHistoryReport(request, response);
                    break;
                case "overdueReport":
                    showOverdueReport(request, response);
                    break;
                case "popularBooks":
                    showPopularBooksReport(request, response);
                    break;
                case "fineCollection":
                    showFineCollectionReport(request, response);
                    break;
                default:
                    response.getWriter().write("<div class='alert alert-danger'>Invalid report type</div>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<div class='alert alert-danger'>Error: " + e.getMessage() + "</div>");
        }
    }
    
    private void handleExport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        String reportType = request.getParameter("reportType");
        String format = request.getParameter("format");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String search = request.getParameter("search");
        
        if ("csv".equals(format)) {
            exportToCSV(response, reportType, startDate, endDate, search);
        } else if ("pdf".equals(format)) {
            // For PDF export, you would typically use a library like iText
            // This is a simplified version that just shows the concept
            response.getWriter().write("<div class='alert alert-info'>PDF export feature coming soon</div>");
        }
    }
    
    private void exportToCSV(HttpServletResponse response, String reportType, String startDate, String endDate, String search) 
            throws IOException, SQLException {
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_" + 
                          new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(0)) + ".csv\"");
        
        PrintWriter out = response.getWriter();
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        try {
            switch (reportType) {
                case "borrowHistory":
                    exportBorrowHistoryCSV(out, conn, startDate, endDate, search);
                    break;
                case "overdueReport":
                    exportOverdueReportCSV(out, conn, startDate, endDate, search);
                    break;
                case "popularBooks":
                    exportPopularBooksCSV(out, conn, startDate, endDate, search);
                    break;
                case "fineCollection":
                    exportFineCollectionCSV(out, conn, startDate, endDate, search);
                    break;
            }
        } finally {
            if (conn != null) conn.close();
        }
    }
    
    private void exportBorrowHistoryCSV(PrintWriter out, Connection conn, String startDate, String endDate, String search) 
            throws SQLException {
        
        StringBuilder sql = new StringBuilder(
            "SELECT br.id, u.fullname, b.title, br.borrowed_date, br.due_date, br.return_date, br.status " +
            "FROM borrowed br " +
            "JOIN users u ON br.user_id = u.id " +
            "JOIN books b ON br.isbn = b.isbn "
        );
        
        List<String> conditions = new ArrayList<>();
        if (startDate != null && !startDate.isEmpty()) {
            conditions.add("br.borrowed_date >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            conditions.add("br.borrowed_date <= ?");
        }
        if (search != null && !search.isEmpty()) {
            conditions.add("(u.fullname LIKE ? OR b.title LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY br.borrowed_date DESC");
        
        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        if (startDate != null && !startDate.isEmpty()) {
            stmt.setString(paramIndex++, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            stmt.setString(paramIndex++, endDate);
        }
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        // Write CSV header
        out.println("ID,User Name,Book Title,Borrowed Date,Due Date,Return Date,Status");
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        while (rs.next()) {
            out.printf("%s,\"%s\",\"%s\",%s,%s,%s,%s%n",
                rs.getString("id"),
                rs.getString("fullname").replace("\"", "\"\""),
                rs.getString("title").replace("\"", "\"\""),
                rs.getDate("borrowed_date") != null ? df.format(rs.getDate("borrowed_date")) : "",
                rs.getDate("due_date") != null ? df.format(rs.getDate("due_date")) : "",
                rs.getDate("return_date") != null ? df.format(rs.getDate("return_date")) : "",
                rs.getString("status")
            );
        }
        
        rs.close();
        stmt.close();
    }
    
    private void exportOverdueReportCSV(PrintWriter out, Connection conn, String startDate, String endDate, String search) 
            throws SQLException {
        // Similar implementation for overdue report
        out.println("User Name,Book Title,Due Date,Days Overdue");
        // Add your export logic here
    }
    
    private void exportPopularBooksCSV(PrintWriter out, Connection conn, String startDate, String endDate, String search) 
            throws SQLException {
        // Similar implementation for popular books
        out.println("Rank,Book Title,Author,Borrow Count");
        // Add your export logic here
    }
    
    private void exportFineCollectionCSV(PrintWriter out, Connection conn, String startDate, String endDate, String search) 
            throws SQLException {
        // Similar implementation for fine collection
        out.println("User Name,Book Title,Amount,Status,Fine Date");
        // Add your export logic here
    }
    
    private void showBorrowHistoryReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String search = request.getParameter("search");
        
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        StringBuilder sql = new StringBuilder(
            "SELECT br.id, u.fullname, b.title, br.borrowed_date, br.due_date, br.return_date, br.status " +
            "FROM borrowed br " +
            "JOIN users u ON br.user_id = u.id " +
            "JOIN books b ON br.isbn = b.isbn "
        );
        
        List<String> conditions = new ArrayList<>();
        if (startDate != null && !startDate.isEmpty()) {
            conditions.add("br.borrowed_date >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            conditions.add("br.borrowed_date <= ?");
        }
        if (search != null && !search.isEmpty()) {
            conditions.add("(u.fullname LIKE ? OR b.title LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY br.borrowed_date DESC LIMIT 1000");
        
        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        if (startDate != null && !startDate.isEmpty()) {
            stmt.setString(paramIndex++, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            stmt.setString(paramIndex++, endDate);
        }
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        PrintWriter out = response.getWriter();
        out.println("<div class='card'>");
        out.println("<div class='card-header d-flex justify-content-between align-items-center'>");
        out.println("<h5>Borrow History Report</h5>");
        out.println("<div>");
        out.println("<button class='btn btn-sm btn-success me-2' onclick=\"exportReport('borrowHistory', 'csv')\">");
        out.println("<i class='fas fa-download me-1'></i>Export CSV");
        out.println("</button>");
        out.println("<button class='btn btn-sm btn-danger' onclick=\"exportReport('borrowHistory', 'pdf')\">");
        out.println("<i class='fas fa-file-pdf me-1'></i>Export PDF");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='card-body'>");
        
        // Search and Filter Form
        out.println("<div class='row mb-4'>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Start Date</label>");
        out.println("<input type='date' id='startDate' class='form-control' value='" + (startDate != null ? startDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>End Date</label>");
        out.println("<input type='date' id='endDate' class='form-control' value='" + (endDate != null ? endDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Search (User/Book)</label>");
        out.println("<div class='input-group'>");
        out.println("<input type='text' id='search' class='form-control' placeholder='Search...' value='" + (search != null ? search : "") + "'>");
        out.println("<button class='btn btn-primary' onclick=\"filterReport('borrowHistory')\">");
        out.println("<i class='fas fa-search'></i>");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='table-responsive'><table class='table table-striped'>");
        out.println("<thead><tr><th>User</th><th>Book</th><th>Borrow Date</th><th>Due Date</th><th>Return Date</th><th>Status</th></tr></thead>");
        out.println("<tbody>");
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int count = 0;
        
        while (rs.next()) {
            count++;
            out.println("<tr>");
            out.println("<td>" + rs.getString("fullname") + "</td>");
            out.println("<td>" + rs.getString("title") + "</td>");
            
            java.sql.Date borrowedDate = rs.getDate("borrowed_date");
            out.println("<td>" + (borrowedDate != null ? df.format(borrowedDate) : "-") + "</td>");
            
            java.sql.Date dueDate = rs.getDate("due_date");
            out.println("<td>" + (dueDate != null ? df.format(dueDate) : "-") + "</td>");
            
            java.sql.Date returnDate = rs.getDate("return_date");
            out.println("<td>" + (returnDate != null ? df.format(returnDate) : "-") + "</td>");
            
            String status = rs.getString("status");
            String badgeClass = "bg-primary";
            if ("OVERDUE".equals(status)) badgeClass = "bg-danger";
            else if ("RETURNED".equals(status)) badgeClass = "bg-success";
            
            out.println("<td><span class='badge " + badgeClass + "'>" + status + "</span></td>");
            out.println("</tr>");
        }
        
        if (count == 0) {
            out.println("<tr><td colspan='6' class='text-center'>No records found</td></tr>");
        }
        
        out.println("</tbody></table></div>");
        out.println("<div class='mt-3'><strong>Total Records: " + count + "</strong></div>");
        out.println("</div></div>");
        
        rs.close();
        stmt.close();
        conn.close();
    }
    
    private void showOverdueReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String search = request.getParameter("search");
        
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        StringBuilder sql = new StringBuilder(
            "SELECT u.fullname, b.title, br.due_date, DATEDIFF(CURDATE(), br.due_date) as days_overdue " +
            "FROM borrowed br " +
            "JOIN users u ON br.user_id = u.id " +
            "JOIN books b ON br.isbn = b.isbn " +
            "WHERE br.return_date IS NOT NULL AND DATEDIFF(br.return_date, br.borrowed_date) > 7 "
        );
        
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND br.due_date >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND br.due_date <= ?");
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (u.fullname LIKE ? OR b.title LIKE ?)");
        }
        
        sql.append(" ORDER BY days_overdue DESC");
        
        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        if (startDate != null && !startDate.isEmpty()) {
            stmt.setString(paramIndex++, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            stmt.setString(paramIndex++, endDate);
        }
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        PrintWriter out = response.getWriter();
        out.println("<div class='card'>");
        out.println("<div class='card-header d-flex justify-content-between align-items-center'>");
        out.println("<h5>Overdue Items Report</h5>");
        out.println("<div>");
        out.println("<button class='btn btn-sm btn-success me-2' onclick=\"exportReport('overdueReport', 'csv')\">");
        out.println("<i class='fas fa-download me-1'></i>Export CSV");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='card-body'>");
        
        // Search and Filter Form
        out.println("<div class='row mb-4'>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Start Date</label>");
        out.println("<input type='date' id='startDate' class='form-control' value='" + (startDate != null ? startDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>End Date</label>");
        out.println("<input type='date' id='endDate' class='form-control' value='" + (endDate != null ? endDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Search (User/Book)</label>");
        out.println("<div class='input-group'>");
        out.println("<input type='text' id='search' class='form-control' placeholder='Search...' value='" + (search != null ? search : "") + "'>");
        out.println("<button class='btn btn-primary' onclick=\"filterReport('overdueReport')\">");
        out.println("<i class='fas fa-search'></i>");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='table-responsive'><table class='table table-striped'>");
        out.println("<thead><tr><th>User</th><th>Book</th><th>Due Date</th><th>Days Overdue</th></tr></thead>");
        out.println("<tbody>");
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int count = 0;
        
        while (rs.next()) {
            count++;
            out.println("<tr>");
            out.println("<td>" + rs.getString("fullname") + "</td>");
            out.println("<td>" + rs.getString("title") + "</td>");
            
            java.sql.Date dueDate = rs.getDate("due_date");
            out.println("<td>" + (dueDate != null ? df.format(dueDate) : "-") + "</td>");
            
            out.println("<td><span class='badge bg-danger'>" + rs.getInt("days_overdue") + " days</span></td>");
            out.println("</tr>");
        }
        
        if (count == 0) {
            out.println("<tr><td colspan='4' class='text-center'>No overdue items found</td></tr>");
        }
        
        out.println("</tbody></table></div>");
        out.println("<div class='mt-3'><strong>Total Overdue Items: " + count + "</strong></div>");
        out.println("</div></div>");
        
        rs.close();
        stmt.close();
        conn.close();
    }
    
    private void showPopularBooksReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String search = request.getParameter("search");
        
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        StringBuilder sql = new StringBuilder(
            "SELECT b.title, b.author, COUNT(br.id) as borrow_count " +
            "FROM books b " +
            "LEFT JOIN borrowed br ON b.isbn = br.isbn "
        );
        
        List<String> conditions = new ArrayList<>();
        if (startDate != null && !startDate.isEmpty()) {
            conditions.add("br.borrowed_date >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            conditions.add("br.borrowed_date <= ?");
        }
        if (search != null && !search.isEmpty()) {
            conditions.add("(b.title LIKE ? OR b.author LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" GROUP BY b.isbn, b.title, b.author ");
        sql.append(" ORDER BY borrow_count DESC LIMIT 20");
        
        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        if (startDate != null && !startDate.isEmpty()) {
            stmt.setString(paramIndex++, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            stmt.setString(paramIndex++, endDate);
        }
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        PrintWriter out = response.getWriter();
        out.println("<div class='card'>");
        out.println("<div class='card-header d-flex justify-content-between align-items-center'>");
        out.println("<h5>Popular Books Report</h5>");
        out.println("<div>");
        out.println("<button class='btn btn-sm btn-success me-2' onclick=\"exportReport('popularBooks', 'csv')\">");
        out.println("<i class='fas fa-download me-1'></i>Export CSV");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='card-body'>");
        
        // Search and Filter Form
        out.println("<div class='row mb-4'>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Start Date</label>");
        out.println("<input type='date' id='startDate' class='form-control' value='" + (startDate != null ? startDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>End Date</label>");
        out.println("<input type='date' id='endDate' class='form-control' value='" + (endDate != null ? endDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Search (Title/Author)</label>");
        out.println("<div class='input-group'>");
        out.println("<input type='text' id='search' class='form-control' placeholder='Search...' value='" + (search != null ? search : "") + "'>");
        out.println("<button class='btn btn-primary' onclick=\"filterReport('popularBooks')\">");
        out.println("<i class='fas fa-search'></i>");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='table-responsive'><table class='table table-striped'>");
        out.println("<thead><tr><th>Rank</th><th>Book Title</th><th>Author</th><th>Borrow Count</th></tr></thead>");
        out.println("<tbody>");
        
        int rank = 0;
        while (rs.next()) {
            rank++;
            out.println("<tr>");
            out.println("<td><strong>" + rank + "</strong></td>");
            out.println("<td>" + rs.getString("title") + "</td>");
            out.println("<td>" + rs.getString("author") + "</td>");
            out.println("<td><span class='badge bg-success'>" + rs.getInt("borrow_count") + " times</span></td>");
            out.println("</tr>");
        }
        
        if (rank == 0) {
            out.println("<tr><td colspan='4' class='text-center'>No borrowing data found</td></tr>");
        }
        
        out.println("</tbody></table></div>");
        out.println("</div></div>");
        
        rs.close();
        stmt.close();
        conn.close();
    }
    
    private void showFineCollectionReport(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String search = request.getParameter("search");
        
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        
        StringBuilder sql = new StringBuilder(
            "SELECT u.fullname, b.title, f.amount, f.status, f.fine_date " +
            "FROM fines f " +
            "JOIN users u ON f.user_id = u.id " +
            "JOIN borrowed br ON f.borrow_id = br.id " +
            "JOIN books b ON br.isbn = b.isbn "
        );
        
        List<String> conditions = new ArrayList<>();
        if (startDate != null && !startDate.isEmpty()) {
            conditions.add("f.fine_date >= ?");
        }
        if (endDate != null && !endDate.isEmpty()) {
            conditions.add("f.fine_date <= ?");
        }
        if (search != null && !search.isEmpty()) {
            conditions.add("(u.fullname LIKE ? OR b.title LIKE ?)");
        }
        
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        sql.append(" ORDER BY f.fine_date DESC LIMIT 1000");
        
        PreparedStatement stmt = conn.prepareStatement(sql.toString());
        int paramIndex = 1;
        
        if (startDate != null && !startDate.isEmpty()) {
            stmt.setString(paramIndex++, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            stmt.setString(paramIndex++, endDate);
        }
        if (search != null && !search.isEmpty()) {
            String searchPattern = "%" + search + "%";
            stmt.setString(paramIndex++, searchPattern);
            stmt.setString(paramIndex++, searchPattern);
        }
        
        ResultSet rs = stmt.executeQuery();
        
        PrintWriter out = response.getWriter();
        out.println("<div class='card'>");
        out.println("<div class='card-header d-flex justify-content-between align-items-center'>");
        out.println("<h5>Fine Collection Report</h5>");
        out.println("<div>");
        out.println("<button class='btn btn-sm btn-success me-2' onclick=\"exportReport('fineCollection', 'csv')\">");
        out.println("<i class='fas fa-download me-1'></i>Export CSV");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("<div class='card-body'>");
        
        // Search and Filter Form
        out.println("<div class='row mb-4'>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Start Date</label>");
        out.println("<input type='date' id='startDate' class='form-control' value='" + (startDate != null ? startDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>End Date</label>");
        out.println("<input type='date' id='endDate' class='form-control' value='" + (endDate != null ? endDate : "") + "'>");
        out.println("</div>");
        out.println("<div class='col-md-4'>");
        out.println("<label class='form-label'>Search (User/Book)</label>");
        out.println("<div class='input-group'>");
        out.println("<input type='text' id='search' class='form-control' placeholder='Search...' value='" + (search != null ? search : "") + "'>");
        out.println("<button class='btn btn-primary' onclick=\"filterReport('fineCollection')\">");
        out.println("<i class='fas fa-search'></i>");
        out.println("</button>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='table-responsive'><table class='table table-striped'>");
        out.println("<thead><tr><th>User</th><th>Book</th><th>Amount</th><th>Status</th><th>Fine Date</th></tr></thead>");
        out.println("<tbody>");
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int count = 0;
        double total = 0;
        
        while (rs.next()) {
            count++;
            double amount = rs.getDouble("amount");
            total += amount;
            String status = rs.getString("status");
            
            out.println("<tr>");
            out.println("<td>" + rs.getString("fullname") + "</td>");
            out.println("<td>" + rs.getString("title") + "</td>");
            out.println("<td><strong>R " + String.format("%.2f", amount) + "</strong></td>");
            
            String badgeClass = "bg-warning";
            if ("paid".equals(status)) badgeClass = "bg-success";
            else if ("waived".equals(status)) badgeClass = "bg-info";
            
            out.println("<td><span class='badge " + badgeClass + "'>" + status + "</span></td>");
            
            java.sql.Date fineDate = rs.getDate("fine_date");
            out.println("<td>" + (fineDate != null ? df.format(fineDate) : "-") + "</td>");
            out.println("</tr>");
        }
        
        if (count == 0) {
            out.println("<tr><td colspan='5' class='text-center'>No fines found</td></tr>");
        }
        
        out.println("</tbody></table></div>");
        out.println("<div class='mt-3'><strong>Total Fines: R " + String.format("%.2f", total) + " (" + count + " records)</strong></div>");
        out.println("</div></div>");
        
        rs.close();
        stmt.close();
        conn.close();
    }
}