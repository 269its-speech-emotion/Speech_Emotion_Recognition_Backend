package com.kmits.projects.speechemotionrecognition.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kmits.projects.speechemotionrecognition.entities.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserDTO {
    private Long id;
    private String username;
    private String email;
    private String VerificationCode;
    private Role role;
    private boolean enabled;
    private List<AudioRecordingDTO> audioRecordings = new ArrayList<>();

}
