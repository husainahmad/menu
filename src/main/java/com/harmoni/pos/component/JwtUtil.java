package com.harmoni.pos.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * Utility class for handling JWT operations such as token generation, extraction, and validation.
 */
@Getter
@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    @Value("${harmoni.menu.jwt.secret}")
    private String secretKey;

    @Value("${harmoni.menu.jwt.expired.time}")
    private long expiredTime;

    /**
     * Generates a JWT token for the specified username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT token as a String
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expiredTime)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validates the JWT token against the provided username.
     *
     * @param token the JWT token to validate
     * @param username the username to compare with the token's subject
     * @return true if the token is valid and matches the username, false otherwise
     */
    public boolean validateToken(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    /**
     * Extracts the JWT token from a Bearer authorization header.
     *
     * @param authorization the Bearer authorization header
     * @return the JWT token string
     */
    public String getTokenFromBearer(String authorization) {
        return authorization.substring(7);
    }

}
