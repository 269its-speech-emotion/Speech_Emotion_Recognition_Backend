package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;
import com.kmits.projects.speechemotionrecognition.requests.audiorecording.GetAudioRecordingResponse;
import com.kmits.projects.speechemotionrecognition.services.AudioRecordingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audio-recordings")
public class AudioRecordingController {

    @Autowired
    private AudioRecordingService audioRecordingService;

    @PostMapping("/add")
    public AudioRecording addAudioRecording(@RequestBody AudioRecording request){
        return audioRecordingService.addAudioRecording(request);
    }

    @GetMapping("/get")
    public List<GetAudioRecordingResponse> getAudioRecordings(){
        return audioRecordingService.getAudioRecordings();
    }

    @PostMapping("/update/{id}")
    public AudioRecording setSelf(@RequestBody AudioRecording putRequest, @PathVariable Long id) {
        putRequest.setId(id);
        return audioRecordingService.setAudioRecording(putRequest);
    }

    @DeleteMapping("delete/{id}")
    public void deleteAudioRecording(@PathVariable Long id){
        audioRecordingService.deleteAudioRecording(id);
    }


}
