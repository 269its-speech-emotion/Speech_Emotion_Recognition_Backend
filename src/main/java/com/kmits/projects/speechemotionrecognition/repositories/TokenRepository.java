package com.kmits.projects.speechemotionrecognition.repositories;

import com.kmits.projects.speechemotionrecognition.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("""
        SELECT t FROM Token t
        WHERE t.appUser.id = :userId
        AND (t.expired = false AND t.revoked = false)
        """)
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);

}
