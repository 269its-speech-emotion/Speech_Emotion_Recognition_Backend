package com.kmits.projects.speechemotionrecognition.requests.audiorecording;

import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;
import com.kmits.projects.speechemotionrecognition.entities.PredictedEmotionType;
import com.kmits.projects.speechemotionrecognition.entities.UserAssessment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAudioRecordingResponse {
    private long id;
    private String generatedTitle;
    private String url;
    public PredictedEmotionType predictedEmotionType;
    public UserAssessment userAssessment;

    public GetAudioRecordingResponse(AudioRecording audioRecording){
        if(audioRecording != null){
            setId(audioRecording.getId());
            setGeneratedTitle(audioRecording.getGeneratedTitle());
            setUrl(audioRecording.getUrl());
            setPredictedEmotionType(audioRecording.getPredictedEmotionType());
            setUserAssessment(audioRecording.getUserAssessment());
        }
    }
}
