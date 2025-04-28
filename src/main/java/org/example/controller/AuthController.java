package org.example.controller;

import org.example.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    // Username → List of roles
    private static final Map<String, List<String>> USERS = Map.of(
            "deep", List.of("admin", "dev"),
            "varsha", List.of("dev"),
            "john", List.of("admin")
    );

    //  Username → Password
    private static final Map<String, String> USER_PASSWORDS = Map.of(
            "deep", "admin123",
            "varsha", "dev123",
            "john", "admin321"
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        //  Validate username and password
        if (USER_PASSWORDS.containsKey(username) && USER_PASSWORDS.get(username).equals(password)) {
            List<String> roles = USERS.get(username);
            String token = jwtUtil.generateToken(username, roles);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", username,
                    "roles", roles
            ));
        }

        // Invalid credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
