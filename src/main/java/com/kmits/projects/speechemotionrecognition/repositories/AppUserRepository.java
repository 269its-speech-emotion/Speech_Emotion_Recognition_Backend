package com.kmits.projects.speechemotionrecognition.repositories;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findById(Long id);
}
