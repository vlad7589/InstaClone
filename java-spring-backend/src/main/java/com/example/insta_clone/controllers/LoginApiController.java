package com.example.insta_clone.controllers;

import com.example.insta_clone.config.JwtConfiguration;
import com.example.insta_clone.models.Login;
import com.example.insta_clone.models.User;
import com.example.insta_clone.services.TokenBlacklistService;
import com.example.insta_clone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class LoginApiController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfiguration jwtConfiguration;
    private final TokenBlacklistService blacklistService;

    @Autowired
    public LoginApiController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtConfiguration jwtConfiguration,
            TokenBlacklistService blacklistService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtConfiguration = jwtConfiguration;
        this.blacklistService = blacklistService;
    }

    @PostMapping(path = "/user/login")
    public ResponseEntity<?> authenticateUser(@RequestBody Login login){
        User user = userService.userByUserLogin(login.getIdentifier());
        if(user != null && BCrypt.checkpw(login.getPassword(), user.getPassword())) {
            String token = jwtConfiguration.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("phoneNumber", user.getPhoneNumber());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/user/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            blacklistService.addToBlacklist(token);
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid authorization header");
    }
}
