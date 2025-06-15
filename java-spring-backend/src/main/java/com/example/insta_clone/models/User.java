package com.example.insta_clone.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq_gen")
    @SequenceGenerator(
            name = "user_id_seq_gen",
            sequenceName = "user_id_seq",
            allocationSize = 1
    )
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPost> userPosts = new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private ProfileImage profileImage;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_liked_posts",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<UserPost> likedPosts = new HashSet<>();

    public void likePost(UserPost post) {
        likedPosts.add(post);
        post.getLikedByUsers().add(this);
    }

    public void unlikePost(UserPost post) {
        likedPosts.remove(post);
        post.getLikedByUsers().remove(this);
    }
}
