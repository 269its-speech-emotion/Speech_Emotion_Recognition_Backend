package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.requests.user.LoginResponse;
import com.kmits.projects.speechemotionrecognition.services.AuthenticationService;
import com.kmits.projects.speechemotionrecognition.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JWTService jwtService;

    public ResponseEntity<AppUser> signup(@RequestBody AppUser request){
        return ResponseEntity.ok(authenticationService.signup(request));
    }

    public ResponseEntity<LoginResponse> login(@RequestBody AppUser request){
        AppUser authenticatedAppUser = authenticationService.login(request);
        String generatedToken = jwtService.generateToken(authenticatedAppUser);
        LoginResponse loginResponse = new LoginResponse(generatedToken, jwtService.getJwtExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
