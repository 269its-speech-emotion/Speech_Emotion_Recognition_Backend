package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AppUser signup(AppUser request){
        if(appUserRepository.findByEmail(request.getEmail()).isPresent()){
            throw new IllegalArgumentException("This email is already in use");
        }

        if(appUserRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalArgumentException("This username is already in use");
        }

        AppUser appUser = new AppUser();
        appUser.setEmail(request.getEmail());
        appUser.setUsername(request.getUsername());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setEnabled(false);
        appUser.setRole(Role.ROLE_USER);
        appUser.setAudioRecordings(request.getAudioRecordings());

        return appUserRepository.save(appUser);
    }

    public AppUser login(AppUser request){
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: "+request.getEmail()));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return appUser;
    }

}
