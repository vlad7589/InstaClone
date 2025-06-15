package com.example.insta_clone.controllers;

import com.example.insta_clone.dto.UserPostDto;
import com.example.insta_clone.models.UserPost;
import com.example.insta_clone.models.ProfileImage;
import com.example.insta_clone.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/images")
public class ImageApiController {

    private final ImageService imageService;

    @Autowired
    public ImageApiController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload/{userId}/{desc}")
    @Transactional(readOnly = false)
    public ResponseEntity<String> uploadImage(
            @PathVariable("userId") Long userId,
            @PathVariable("desc") String desc,
            @RequestParam("file")MultipartFile file
            ){
        try {
            imageService.uploadImage(userId, file, desc);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving image: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserPostDto>> getUserImages(@PathVariable Long userId) throws IOException {
        List<UserPost> userPosts = imageService.getUserImages(userId);

        List<UserPostDto> userPostDtos = userPosts.stream()
                .map(UserPostDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(userPostDtos);
    }

    @PatchMapping("/{postId}/{userId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable("postId") Long postId,
            @PathVariable("userId") Long userId
    ) {
        imageService.likePost(userId, postId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{postId}/{userId}/unlike")
    public ResponseEntity<Void> unlikePost(
            @PathVariable("postId") Long postId,
            @PathVariable("userId") Long userId
    ) {
        imageService.unlikePost(userId, postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/likes/count")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Long postId){
        int likesCount = imageService.getPostLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/{postId}/{userId}/likes/check")
    public ResponseEntity<Boolean> isLikedByUser(
            @PathVariable("postId") Long postId,
            @PathVariable("userId") Long userId
    ) {
        boolean isLiked = imageService.isPostLikedByUser(userId, postId);
        return ResponseEntity.ok(isLiked);
    }

    @PostMapping("/upload/profile-image/{userId}")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("file")MultipartFile file){
        Logger.getAnonymousLogger().info("Uploading Image");
        try {
            imageService.uploadProfileImage(userId, file);
            return ResponseEntity.ok("Profile image uploaded successfully");
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving profile image: " + e.getMessage());
        }
    }

    @GetMapping("/user/profile-image/{userId}")
    public ResponseEntity<Map<String, Object>> getProfileImage(@PathVariable Long userId) throws IOException {
        ProfileImage profileImage = imageService.getProfileImage(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", profileImage.getId());
        response.put("name", profileImage.getName());
        response.put("data", Base64.getEncoder().encodeToString(profileImage.getData()));

        return ResponseEntity.ok(response);
    }
}
