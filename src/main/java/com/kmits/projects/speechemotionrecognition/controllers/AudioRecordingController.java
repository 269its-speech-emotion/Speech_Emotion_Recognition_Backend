package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;
import com.kmits.projects.speechemotionrecognition.services.AudioRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audio-recordings")
public class AudioRecordingController {

    @Autowired
    private AudioRecordingService audioRecordingService;

    @PostMapping("/add")
    public AudioRecording addAudioRecording(@RequestBody AudioRecording request){
        return audioRecordingService.addAudioRecording(request);
    }
}
