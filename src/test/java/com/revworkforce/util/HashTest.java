package com.revworkforce.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashTest {
    public static void main(String[] args) {
        String hash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String password = "password";

        boolean matches = BCrypt.checkpw(password, hash);
        System.out.println("Matches 'password': " + matches);

        boolean matchesTrimmed = BCrypt.checkpw("password", hash.trim());
        System.out.println("Matches 'password' with trimmed hash: " + matchesTrimmed);

        String genHash = BCrypt.hashpw("password", BCrypt.gensalt(12));
        System.out.println("New Generated Hash: " + genHash);
    }
}
