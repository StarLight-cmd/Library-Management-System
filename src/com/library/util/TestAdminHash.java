package com.library.util;

public class TestAdminHash {
	// used to test password hashing and hash admin password.
    public static void main(String[] args) {
        String password = "123"; 
        String hashed = PasswordUtil.hashPassword(password);
        System.out.println("Hashed password: " + hashed);
    }
}
