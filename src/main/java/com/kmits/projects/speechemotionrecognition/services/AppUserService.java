package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.dtos.appUser.AppUserResponseDTO;
import com.kmits.projects.speechemotionrecognition.dtos.auth.DeleteAppUserRequestDTO;
import com.kmits.projects.speechemotionrecognition.dtos.auth.DeleteAppUserResponseDTO;
import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserResponseDTO getSelf(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserResponseDTO response = new AppUserResponseDTO();
        try{
            AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

            Optional<AppUser> optionalAppUser = appUserRepository.findById(userPrincipal.getId());

            if(optionalAppUser.isPresent()){
                var appUser = optionalAppUser.get();

                response.setStatusCode(200);
                response.setMessage("AppUser retrieved successfully.");
                response.setUsername(appUser.getUsername());
                response.setEmail(appUser.getEmail());
                response.setAudioRecordings(Utils.mapAppUserToAppUserDTO(appUser).getAudioRecordings());
            }else{
                response.setStatusCode(210);
                response.setMessage("This user does not exist.");
            }
        }catch (EntityNotFoundException e){
            response.setStatusCode(220);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    public DeleteAppUserResponseDTO deleteAppUser(DeleteAppUserRequestDTO request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

        var response = new DeleteAppUserResponseDTO();

        Optional<AppUser> optionalAppUser = appUserRepository.findById(request.getId());

        if(optionalAppUser.isPresent()){
            appUserRepository.delete(optionalAppUser.get());

            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        }else {
            response.setStatusCode(210);
            response.setMessage("User not found");
        }

        return response;
    }
}
