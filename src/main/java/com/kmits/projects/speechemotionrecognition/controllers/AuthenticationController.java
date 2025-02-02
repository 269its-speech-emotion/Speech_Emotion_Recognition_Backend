package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.dtos.auth.*;
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
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody SignUpRequestDTO request){
        SignUpResponseDTO signUpResponseDTO = authenticationService.signup(request);
        return ResponseEntity.status(signUpResponseDTO.getStatusCode()).body(signUpResponseDTO);
    }


    @PostMapping("/login")
    public ResponseEntity<LogInResponseDTO> login(@RequestBody LogInRequestDTO request){
        LogInResponseDTO response = authenticationService.login(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<VerificationResponseDTO> verifyUser(@RequestBody VerificationRequestDTO request){
        VerificationResponseDTO response = authenticationService.verifyAppUser(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("request-verification-code")
    public ResponseEntity<NewVerificationCodeResponseDTO> requestNewVerificationCode(@RequestBody NewVerificationCodeRequestDTO request){
        NewVerificationCodeResponseDTO response = authenticationService.requestVerificationCode(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/renew-password")
    public ResponseEntity<RenewPasswordResponseDTO> renewPassword(@RequestBody RenewPasswordRequestDTO request){
        RenewPasswordResponseDTO response = authenticationService.renewPassword(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}