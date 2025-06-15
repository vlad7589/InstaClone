package com.example.insta_clone.repository;

import com.example.insta_clone.models.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByUser_Id(Long userId);
}
