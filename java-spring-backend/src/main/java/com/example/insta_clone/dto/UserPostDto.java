package com.example.insta_clone.dto;

import com.example.insta_clone.models.UserPost;
import lombok.Data;

import java.util.Base64;
import java.util.List;

@Data
public class UserPostDto {
    private Long id;
    private String name;
    private String description;
    private Integer countLikes;
    private List<String> comments;
    private String data;
    private String contentType;

    public UserPostDto(UserPost post) {
        this.id = post.getId();
        this.name = post.getName();
        this.description = post.getDescription();
        this.comments = post.getComments();

        if(post.getData() != null) {
            this.data = Base64.getEncoder().encodeToString(post.getData());

            this.contentType = "image/jpg";
        }
    }
}

