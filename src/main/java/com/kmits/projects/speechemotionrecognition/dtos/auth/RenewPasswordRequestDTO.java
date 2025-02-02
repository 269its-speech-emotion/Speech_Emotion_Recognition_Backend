package com.kmits.projects.speechemotionrecognition.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenewPasswordRequestDTO {
    private String email;
    private String verificationCode;
    private String newPassword;
}
