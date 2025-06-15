package com.example.insta_clone.services;

import com.example.insta_clone.models.UserPost;
import com.example.insta_clone.models.ProfileImage;
import com.example.insta_clone.models.User;
import com.example.insta_clone.repository.ImageRepository;
import com.example.insta_clone.repository.ProfileImageRepository;
import com.example.insta_clone.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    @Value("classpath:static/default_profile.png")
    private Resource defaultProfile;

    @Autowired
    public ImageService(ImageRepository imageRepository,
                        UserRepository userRepository,
                        ProfileImageRepository profileImageRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.profileImageRepository = profileImageRepository;
    }

    @Transactional
    public ProfileImage uploadProfileImage(Long userId, MultipartFile file) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        ProfileImage profileImage = profileImageRepository.findByUser_Id(userId).orElse(new ProfileImage());

        if(profileImage.getId() != null){
            java.util.logging.Logger.getAnonymousLogger().info("Updating existing profile image for user:" + userId);
        } else {
            java.util.logging.Logger.getAnonymousLogger().info("Creating new profile image for user:" + userId);
        }

        profileImage.setName(file.getOriginalFilename());
        profileImage.setData(file.getBytes());
        profileImage.setUser(user);

        return profileImageRepository.save(profileImage);
    }

    public ProfileImage getProfileImage(Long userId) throws IOException {
        Optional<ProfileImage> profileImage = profileImageRepository.findByUser_Id(userId);
        if(profileImage.isPresent())
            return profileImage.get();
        else return new ProfileImage(null,
                "default_profile",
                defaultProfile.getInputStream().readAllBytes(),
                userRepository.findById(userId).get());
    }
    @Transactional(readOnly = false)
    public UserPost uploadImage(Long userId, MultipartFile file, String desc) throws IOException{
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserPost userPost = new UserPost();
        userPost.setName(file.getOriginalFilename());
        userPost.setData(file.getBytes());
        userPost.setDescription(desc);
        userPost.setUser(user);
        userPost.setComments(new ArrayList<>());
        return imageRepository.save(userPost);
    }

    public List<UserPost> getUserImages(Long userId) throws IOException {
        return imageRepository.findByUser_Id(userId);
    }

    @Transactional
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserPost post = imageRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        log.info("Before like - Post {} like count: {}", postId, post.getLikesCount());
        log.info("Is already liked by user {}: {}", userId, post.isLikedByUser(user));

        if (!post.isLikedByUser(user)) {
            user.likePost(post);
            userRepository.save(user);
            imageRepository.save(post);
            log.info("After like - Post {} like count: {}", postId, post.getLikesCount());
        }
    }

    @Transactional
    public void unlikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPost post = imageRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (post.isLikedByUser(user)) {
            user.unlikePost(post);
            userRepository.save(user);
        }
    }

    public boolean isPostLikedByUser(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserPost post = imageRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return post.isLikedByUser(user);
    }

    public int getPostLikesCount(Long postId) {
        UserPost post = imageRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return post.getLikesCount();
    }
}
