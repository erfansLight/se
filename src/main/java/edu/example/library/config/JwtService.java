package edu.example.library.config;

import edu.example.library.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.issuer}") String issuer,
                      @Value("${app.jwt.expirationMinutes}") long expirationMinutes) {
        this.secret = secret;
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    private Key key() {
        // Accept raw secret; if too short user must change it.
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(Long userId, String username, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .addClaims(Map.of(
                        "username", username,
                        "role", role.name()
                ))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
