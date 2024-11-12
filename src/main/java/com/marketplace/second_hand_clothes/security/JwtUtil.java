package com.marketplace.second_hand_clothes.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private static final int EXPIRATION_IN_MILLISECONDS = 1000 * 60 * 60 * 4;

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_IN_MILLISECONDS))
                .signWith(Keys.hmacShaKeyFor(jwtSecretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts
                .parser()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecretKey.getBytes()))
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(Keys.hmacShaKeyFor(jwtSecretKey.getBytes())).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}

