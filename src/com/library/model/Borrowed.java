package com.library.model;

import java.sql.Date;

public class Borrowed {
	// Borrowed entity maps to borrowed table
    private int id;
    private int userId;
    private String isbn;
    private Date borrowedDate;
    private Date returnDate;
    private String status;
    
    // Borrowed getters and setters
    public int getId() { 
    	return id; 
    	}
    
    public void setId(int id) { 
    	this.id = id; 
    	}

    public int getUserId() { 
    	return userId; 
    	}
    
    public void setUserId(int userId) { 
    	this.userId = userId; 
    	}

    public String getIsbn() { 
    	return isbn; 
    	}
    
    public void setIsbn(String isbn) { 
    	this.isbn = isbn; 
    	}

    public Date getBorrowedDate() { 
    	return borrowedDate; 
    	}
    
    public void setBorrowedDate(Date borrowedDate) { 
    	this.borrowedDate = borrowedDate; 
    	}

    public Date getReturnDate() { 
    	return returnDate; 
    	}
    
    public void setReturnDate(Date returnDate) { 
    	this.returnDate = returnDate; 
    	}

    public String getStatus() {
    	return status; 
    	}
    
    public void setStatus(String status) { 
    	this.status = status; 
    	}
}
