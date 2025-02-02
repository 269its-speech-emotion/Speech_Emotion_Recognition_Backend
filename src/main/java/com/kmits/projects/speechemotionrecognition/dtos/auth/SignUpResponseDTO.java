package com.kmits.projects.speechemotionrecognition.dtos.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kmits.projects.speechemotionrecognition.dtos.AppUserDTO;
import com.kmits.projects.speechemotionrecognition.dtos.AudioRecordingDTO;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignUpResponseDTO {
    private int statusCode;
    private String message;

    private String token;
    private Role role;
    private String expirationTime;

    private AppUserDTO appUserDTO;
    private List<AudioRecordingDTO> audioRecordingList;
}
