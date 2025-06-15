package com.example.insta_clone.controllers;

import com.example.insta_clone.models.User;
import com.example.insta_clone.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RegisterApiController {

    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public RegisterApiController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/user/register")
    public ResponseEntity<User> registerNewUser(@RequestBody User user){
        String hash_password = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash_password);

        User result = userService.registerNewUser(user);

        if(result != null) return new ResponseEntity<>(result, HttpStatus.CREATED);
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
