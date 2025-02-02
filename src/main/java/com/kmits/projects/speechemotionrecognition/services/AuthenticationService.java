package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.dtos.*;
import com.kmits.projects.speechemotionrecognition.dtos.auth.*;
import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.utils.Utils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

    final int VERIFICATION_CODE_VALIDITY_TIMES_IN_MIN = 15;

    public SignUpResponseDTO signup(SignUpRequestDTO request){
        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();
        try {
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

            //sendVerificationEmail(appUser);

            AppUser savedUser = appUserRepository.save(appUser);
            AppUserDTO userDTO = Utils.mapAppUserToAppUserDTO(savedUser);
            signUpResponseDTO.setStatusCode(200);
            signUpResponseDTO.setAppUserDTO(userDTO);
        } catch (IllegalArgumentException e) {
            signUpResponseDTO.setStatusCode(400);
            signUpResponseDTO.setMessage(e.getMessage());
        } catch (Exception error){
            signUpResponseDTO.setStatusCode(500);
            signUpResponseDTO.setMessage("Error occurred during user registration: "+ error.getMessage());
            System.out.println(error.getMessage());
        }

        return signUpResponseDTO;
    }



    public LogInResponseDTO login(LogInRequestDTO request){
        System.out.println(request.getEmail() + " "+request.getPassword());
        AppUser appUser = appUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: "+request.getEmail()));

        System.out.println(appUser.toString());

        if(!appUser.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        LogInResponseDTO response = new LogInResponseDTO();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            String generatedToken = jwtService.generateToken(appUser);

            response.setStatusCode(200);
            response.setMessage("Login successful");
            response.setToken(generatedToken);
            response.setExpiresIn(jwtService.getJwtExpirationTime());

            System.out.println(response.getStatusCode() + " "+response.getToken());

            return response;

        }catch (AuthenticationException e) {
            // invalid email or password
            response.setStatusCode(203);
            response.setMessage("Invalid email or password");
            response.setToken(null);
            response.setExpiresIn(0);
            return response;
        }

    }


    public VerificationResponseDTO verifyAppUser(VerificationRequestDTO request){
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());
        VerificationResponseDTO response = new VerificationResponseDTO();
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

                response.setStatusCode(200);
                response.setEnabled(true);
            }else {
                response.setStatusCode(400);
                response.setEnabled(false);
            }
        }else {
            response.setStatusCode(300);
            response.setEnabled(false);
        }
        return response;
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


    public NewVerificationCodeResponseDTO requestVerificationCode(NewVerificationCodeRequestDTO request){
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());

        NewVerificationCodeResponseDTO response = new NewVerificationCodeResponseDTO();

        if(optionalAppUser.isPresent()){
            AppUser appUser = optionalAppUser.get();
            appUser.setVerificationCode(generateVerificationCode());
            appUser.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(VERIFICATION_CODE_VALIDITY_TIMES_IN_MIN));
            appUserRepository.save(appUser);

            response.setStatusCode(200);
            response.setMessage("New verification code was generated and expired at :"+ appUser.getVerificationCodeExpiresAt());
            response.setNewVerificationCode(appUser.getVerificationCode());
        }else{
            response.setStatusCode(203);
            response.setMessage("Do not have an account with this email: "+request.getEmail());
        }

        return response;
    }


    public RenewPasswordResponseDTO renewPassword(RenewPasswordRequestDTO request) {
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());

        RenewPasswordResponseDTO response = new RenewPasswordResponseDTO();

        if(optionalAppUser.isPresent()){
            AppUser appUser = optionalAppUser.get();
            if(appUser.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code has expired");
            }
            if(appUser.getVerificationCode().equals(request.getVerificationCode())){
                appUser.setVerificationCode(null);
                appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
                appUserRepository.save(appUser);

                response.setStatusCode(200);
                response.setMessage("Password renewed successfully");
            }else {
                response.setStatusCode(205);
                response.setMessage("You provided a wrong verification code, please recheck");
            }
        }else {
            response.setStatusCode(208);
            response.setMessage("Provided verification code expired, please request a new one");
        }

        return response;
    }


}
