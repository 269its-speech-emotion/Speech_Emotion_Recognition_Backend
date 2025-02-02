package com.kmits.projects.speechemotionrecognition.dtos.appUser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kmits.projects.speechemotionrecognition.dtos.AudioRecordingDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUserResponseDTO {
    private int statusCode;
    private String message;
    private String username;
    private String email;
    private List<AudioRecordingDTO> audioRecordings = new ArrayList<>();
}
