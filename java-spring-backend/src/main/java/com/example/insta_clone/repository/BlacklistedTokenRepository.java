package com.example.insta_clone.repository;

import com.example.insta_clone.models.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    boolean existsByTokenId(String tokenId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") Date now);
}

