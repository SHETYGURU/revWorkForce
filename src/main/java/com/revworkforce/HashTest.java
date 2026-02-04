package com.revworkforce;

import org.mindrot.jbcrypt.BCrypt;

public class HashTest {
    public static void main(String[] args) {
        String dbHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "password";

        System.out.println("Testing hash...");
        boolean match = BCrypt.checkpw(password, dbHash);
        System.out.println("Match: " + match);

        if (!match) {
            System.out.println("Generating new hash for 'password' with default cost...");
            String newHash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            System.out.println("New Hash: " + newHash);
        }
    }
}
