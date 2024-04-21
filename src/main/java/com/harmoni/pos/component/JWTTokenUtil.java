package com.harmoni.pos.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class JWTTokenUtil  {
    private static final Logger log = LoggerFactory.getLogger(JWTTokenUtil.class);
    @Value("${harmoni.menu.jwt.secret}")
    private String secret;
    private static final long TIME = TimeUnit.DAYS.toMillis(1);

    private static String staticSecret;
    @Value("${harmoni.menu.jwt.secret}")
    public void setStaticSecret() {
        JWTTokenUtil.staticSecret = secret;
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() * TIME))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(staticSecret)))
                .compact();
    }

    public static String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(staticSecret)))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public static boolean isValidToken(String token) {
         Jwts.parser()
                 .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(staticSecret)))
                 .build()
                 .parse(token);
         return true;
    }

}
