package com.example.insta_clone.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "blacklisted_token")
@Getter
@Setter
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tokenId; // JTI

    @Column(nullable = false)
    private Date expiryDate;

    public BlacklistedToken() {}

    public BlacklistedToken(String tokenId, Date expiryDate) {
        this.tokenId = tokenId;
        this.expiryDate = expiryDate;
    }

}
