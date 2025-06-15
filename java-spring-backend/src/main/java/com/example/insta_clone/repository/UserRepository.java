package com.example.insta_clone.repository;

import com.example.insta_clone.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional
    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);

}
