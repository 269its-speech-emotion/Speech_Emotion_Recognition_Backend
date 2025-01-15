package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.requests.user.LoginResponse;
import com.kmits.projects.speechemotionrecognition.services.AuthenticationService;
import com.kmits.projects.speechemotionrecognition.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JWTService jwtService;


    @PostMapping("/signup")
    public ResponseEntity<AppUser> signup(@RequestBody AppUser request){
        return ResponseEntity.ok(authenticationService.signup(request));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AppUser request){
        AppUser authenticatedAppUser = authenticationService.login(request);
        String generatedToken = jwtService.generateToken(authenticatedAppUser);
        LoginResponse loginResponse = new LoginResponse(generatedToken, jwtService.getJwtExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }


    @RequestMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody AppUser appUser){
        try {
            authenticationService.verifyAppUser(appUser);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
