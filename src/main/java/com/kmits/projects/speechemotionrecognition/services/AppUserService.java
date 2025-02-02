package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.dtos.appUser.AppUserResponseDTO;
import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
    @Autowired
    private AppUserRepository appUserRepository;

    public AppUserResponseDTO getSelf(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserResponseDTO response = new AppUserResponseDTO();
        try{
            AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

            AppUser appUser = appUserRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userPrincipal.getId()));

            response.setStatusCode(200);
            response.setMessage("AppUser retrieved successfully.");
            response.setUsername(appUser.getUsername());
            response.setEmail(appUser.getEmail());
            response.setAudioRecordings(Utils.mapAppUserToAppUserDTO(appUser).getAudioRecordings());

            return response;
        }catch (EntityNotFoundException e){
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

            return response;
        }
    }


    public void deleteAppUser(Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        appUserRepository.delete(appUser);
    }
}
