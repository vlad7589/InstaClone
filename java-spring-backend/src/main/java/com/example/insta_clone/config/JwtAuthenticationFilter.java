package com.example.insta_clone.config;

import com.example.insta_clone.models.User;
import com.example.insta_clone.services.TokenBlacklistService;
import com.example.insta_clone.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtConfiguration jwtConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (StringUtils.hasText(token)) {
                boolean isValid = false;
                try {
                    isValid = jwtConfig.validateToken(token);
                } catch (Exception e) {
                    logger.warn("Invalid JWT token: {}", e);
                }

                if(blacklistService.isBlacklisted(token)){
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been revoked");
                    return;
                }

                if (isValid) {
                    Long userId = null;
                    try {
                        userId = jwtConfig.getUserIdFromToken(token);
                        if (userId == null) {
                            logger.warn("User ID extracted from token is null");
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } catch (Exception e) {
                        logger.warn("Error extracting user ID from token: {}", e);
                        filterChain.doFilter(request, response);
                        return;
                    }

                    User user = userService.findById(userId);

                    if (user != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        user, null, Collections.emptyList());

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    } else {
                        logger.warn("No user found with ID: {}" + userId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();  // Added trim() to remove any whitespace
        }
        return null;
    }
}