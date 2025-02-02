package com.kmits.projects.speechemotionrecognition.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResponseDTO {
    private int statusCode;
    private boolean enabled;
}