package com.example.insta_clone.config;

import com.example.insta_clone.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Configuration
public class JwtConfiguration {
    @Value("${jwt.secret}")
    private String secret;

    public SecretKey getSignedKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user){
        SecretKey key = getSignedKey();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .issuedAt(new Date())
                .id(UUID.randomUUID().toString())
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(getSignedKey())
                    .build().parseSignedClaims(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSignedKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Long.parseLong(claims.getSubject());
    }


}
