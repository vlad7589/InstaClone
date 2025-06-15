package com.example.insta_clone.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_posts")
@ToString
@EqualsAndHashCode(of = "id")
public class UserPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private List<String> comments;

    @ToString.Exclude
    @Lob
    private byte[] data;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @ManyToMany(mappedBy = "likedPosts", fetch = FetchType.LAZY)
    private Set<User> likedByUsers = new HashSet<>();

    public int getLikesCount() {
        return likedByUsers.size();
    }

    public boolean isLikedByUser(User user) {
        return likedByUsers.contains(user);
    }
}