package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

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

    @Autowired
    private EmailNotificationService notificationService;

    int VERIFICATION_CODE_VALIDITY_TIMES_IN_MIN = 15;

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
        appUser.setVerificationCode(generateVerificationCode());
        appUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALIDITY_TIMES_IN_MIN));
        appUser.setEnabled(false);
        appUser.setRole(Role.ROLE_USER);
        appUser.setAudioRecordings(request.getAudioRecordings());

        //sendVerificationEmail(appUser);

        return appUserRepository.save(appUser);
    }


    public AppUser login(AppUser request){
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: "+request.getEmail()));

        if(!appUser.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return appUser;
    }


    public void verifyAppUser(AppUser request){
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());
        if(optionalAppUser.isPresent()){
            AppUser appUser = optionalAppUser.get();
            if(appUser.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code has expired");
            }
            if(appUser.getVerificationCode().equals(request.getVerificationCode())){
                appUser.setEnabled(true);
                appUser.setVerificationCode(null);
                appUser.setVerificationCodeExpiresAt(null);
                appUserRepository.save(appUser);
            }else {
                throw new RuntimeException("Invalid verification code");

            }
        }else {
            throw new RuntimeException("User not found");
        }
    }


    private void sendVerificationEmail(AppUser appUser){
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + appUser.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            notificationService.sendVerificationEmail(appUser.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error when sending the verification email");
        }
    }


    private String generateVerificationCode(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

}
