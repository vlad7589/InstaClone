package com.example.insta_clone.services;

import com.example.insta_clone.config.JwtConfiguration;
import com.example.insta_clone.models.BlacklistedToken;
import com.example.insta_clone.repository.BlacklistedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private BlacklistedTokenRepository tokenRepository;

    @Transactional
    public void addToBlacklist(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtConfiguration.getSignedKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenId = claims.getId(); // Extract JTI correctly
            Date expiryDate = claims.getExpiration();

            if (expiryDate == null) {
                throw new IllegalArgumentException("Token does not contain an expiration date.");
            }

            tokenRepository.save(new BlacklistedToken(tokenId, expiryDate));
        } catch (Exception e) {
        }
    }

    @Transactional
    public boolean isBlacklisted(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtConfiguration.getSignedKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String tokenId = claims.getId();
            return tokenRepository.existsByTokenId(tokenId);
        } catch (Exception e) {
            return true;
        }
    }
}
