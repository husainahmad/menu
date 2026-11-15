package com.harmoni.pos.component;

import com.harmoni.pos.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



/**
 * Utility class for handling JWT operations such as token generation, extraction, and validation.
 */
@Getter
@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username contained in the token
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
