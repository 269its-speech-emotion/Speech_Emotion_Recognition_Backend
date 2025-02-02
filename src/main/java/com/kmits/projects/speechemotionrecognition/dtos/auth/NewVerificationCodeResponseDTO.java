package com.kmits.projects.speechemotionrecognition.dtos.auth;

import lombok.Data;

@Data
public class NewVerificationCodeResponseDTO {
    private int statusCode;
    private String message;
    private String newVerificationCode;
}
