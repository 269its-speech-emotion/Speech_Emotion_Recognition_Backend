package com.kmits.projects.speechemotionrecognition.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AudioRecordingDTO {
    private Long id;
    private String generatedTitle;
    private String url;
    private AppUserDTO appUser;
}
