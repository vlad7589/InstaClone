package com.example.insta_clone.services;

import com.example.insta_clone.models.User;
import com.example.insta_clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User registerNewUser(User user){
        return userRepository.save(user);
    }
    @Transactional
    public User userByUserLogin(String userLogin){
         if(userLogin.contains("@")) return userRepository.findUserByEmail(userLogin);
         else if(userLogin.matches("[0-9]+")) return userRepository.findUserByPhoneNumber(userLogin);
         else return userRepository.findUserByUsername(userLogin);
    }

    public User findById(Long id){
        return userRepository.findById(id).get();
    }
}
