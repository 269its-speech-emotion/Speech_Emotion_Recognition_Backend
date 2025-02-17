package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.dtos.appUser.AppUserResponseDTO;
import com.kmits.projects.speechemotionrecognition.dtos.auth.DeleteAppUserRequestDTO;
import com.kmits.projects.speechemotionrecognition.dtos.auth.DeleteAppUserResponseDTO;
import com.kmits.projects.speechemotionrecognition.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AppUSerController {

    private final AppUserService appUserService;

    @GetMapping("get-self")
    public ResponseEntity<AppUserResponseDTO> getSelf(){
        AppUserResponseDTO response = appUserService.getSelf();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/delete-self/{id}")
    public ResponseEntity<DeleteAppUserResponseDTO> deleteSelf(@PathVariable DeleteAppUserRequestDTO request){
        DeleteAppUserResponseDTO response = appUserService.deleteAppUser(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
