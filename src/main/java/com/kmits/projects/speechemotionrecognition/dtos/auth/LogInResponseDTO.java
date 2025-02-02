package com.kmits.projects.speechemotionrecognition.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogInResponseDTO {
    private int statusCode;
    private String message;
    private String token;
    private long expiresIn;

}
