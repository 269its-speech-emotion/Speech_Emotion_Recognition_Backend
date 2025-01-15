package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser getSelf(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

        return appUserRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userPrincipal.getId()));
    }
}
