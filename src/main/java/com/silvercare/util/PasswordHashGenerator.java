package com.silvercare.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate bcrypt hashes for migration
 * Run this once to get the hash for "123123"
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        String plainPassword = "admin123";
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        
        System.out.println("========================================");
        System.out.println("BCrypt Hash Generator");
        System.out.println("========================================");
        System.out.println("Plain password: " + plainPassword);
        System.out.println("BCrypt hash: " + hash);
        System.out.println("========================================");
        System.out.println("\nUse this hash in your SQL UPDATE statements!");
        System.out.println("\nSQL Example:");
        System.out.println("UPDATE customers SET password = '" + hash + "' WHERE password = '123123';");
    }
}
