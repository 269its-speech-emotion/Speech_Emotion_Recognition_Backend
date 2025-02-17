package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.dtos.AppUserDTO;
import com.kmits.projects.speechemotionrecognition.dtos.auth.*;
import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import com.kmits.projects.speechemotionrecognition.entities.Token;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.repositories.TokenRepository;
import com.kmits.projects.speechemotionrecognition.utils.Utils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailNotificationService notificationService;

    final int VERIFICATION_CODE_VALIDITY_TIMES_IN_MIN = 15;

    public SignUpResponseDTO signup(SignUpRequestDTO request){
        SignUpResponseDTO signUpResponseDTO = new SignUpResponseDTO();

        try {
            if(appUserRepository.findByEmail(request.getEmail()).isPresent()){
                signUpResponseDTO.setStatusCode(220);
                signUpResponseDTO.setMessage("This email is already in use");
                return signUpResponseDTO;
            }

            if(appUserRepository.findByUsername(request.getUsername()).isPresent()){
                signUpResponseDTO.setStatusCode(220);
                signUpResponseDTO.setMessage("This username is already in use");
                return signUpResponseDTO;
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
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());

        LogInResponseDTO response = new LogInResponseDTO();

        if(optionalAppUser.isPresent()){
            var appUser = optionalAppUser.get();
            if(appUser.isEnabled()) {
                try {
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );

                    String generatedJWTToken = jwtService.generateToken(appUser);

                    revokeAllUserTokens(appUser);

                    var token = saveAppUserJWTToken(appUser, generatedJWTToken);

                    tokenRepository.save(token);

                    response.setStatusCode(200);
                    response.setMessage("Login successful");
                    response.setToken(generatedJWTToken);
                    response.setExpiresIn(jwtService.getJwtExpirationTime());

                    return response;

                } catch (AuthenticationException e) {
                    // invalid email or password
                    response.setStatusCode(203);
                    response.setMessage("Invalid email or password");
                    response.setToken(null);
                    response.setExpiresIn(0);
                    return response;
                }
            }else { // user account not enabled
                response.setStatusCode(210);
                response.setMessage("Account not verified. Please verify your account");
                return response;
            }

        }else { // user with non-existing email
            response.setStatusCode(220);
            response.setMessage("User not found with email: "+request.getEmail());
            return response;
        }
    }

    public VerificationResponseDTO verifyAppUser(VerificationRequestDTO request){
        Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(request.getEmail());
        VerificationResponseDTO response = new VerificationResponseDTO();
        if(optionalAppUser.isPresent()) {
            AppUser appUser = optionalAppUser.get();
            if(!appUser.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                if(appUser.getVerificationCode().equals(request.getVerificationCode())){
                    appUser.setEnabled(true);
                    appUser.setVerificationCode(null);
                    appUser.setVerificationCodeExpiresAt(null);
                    appUserRepository.save(appUser);

                    response.setStatusCode(200);
                    response.setMessage("Your account is enabled. you can log in now !!!");
                }else {
                    response.setStatusCode(210);
                    response.setMessage("Provided verification code is wrong");
                }
            }else {
                response.setStatusCode(220);
                response.setMessage("Verification code: "+request.getVerificationCode()+" is expired");
            }
        }else { // non-existing account with this email
            response.setStatusCode(230);
            response.setMessage("User not found with email: "+request.getEmail());
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

    private void revokeAllUserTokens(AppUser appUser){
        var validUserTokens = tokenRepository.findAllValidTokensByUser(appUser.getId());
        if (validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private static Token saveAppUserJWTToken(AppUser appUser, String generatedJWTToken) {
        return Token.builder()
                .appUser(appUser)
                .token(generatedJWTToken)
                .revoked(false)
                .expired(false)
                .build();
    }

}
