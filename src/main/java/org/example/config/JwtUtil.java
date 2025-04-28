package org.example.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "YourVerySecretKeyThatShouldBeLongEnough1234!"; // Keep this secret and long
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Generate token with username and list of roles
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)           // "sub" = username
                .claim("roles", roles)           //  Add list of roles
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 1)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //  Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token); // Will throw exception if token invalid
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    //  Get claims (username, roles, etc.)
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
