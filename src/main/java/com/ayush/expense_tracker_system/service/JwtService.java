package com.ayush.expense_tracker_system.service;

import com.ayush.expense_tracker_system.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class    JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public String generateJwtToken(User user){
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                    .issuedAt(new Date())
                    .expiration(Date.from(
                            Instant.now().plusSeconds(15)
                    ))
                .signWith(generateKey())
                .compact();
    }

     private SecretKey generateKey(){
        return Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(secretKey)
        );
    }

    public Claims extractClaims(String jwtToken){
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                ;
    }
}
