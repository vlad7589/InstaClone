package com.example.insta_clone.repository;

import com.example.insta_clone.models.UserPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<UserPost, Long> {
    List<UserPost> findByUser_Id(Long userId);
}
