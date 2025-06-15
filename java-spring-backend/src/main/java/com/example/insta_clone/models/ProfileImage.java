package com.example.insta_clone.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "profile_images")
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
@NoArgsConstructor
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private byte[] data;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
