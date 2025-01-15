package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.repositories.AudioRecordingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AudioRecordingService {
    @Autowired
    private AudioRecordingRepository audioRecordingRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public AudioRecording addAudioRecording(AudioRecording request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

        AppUser appUser = appUserRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userPrincipal.getId()));

        AudioRecording audioRecording = new AudioRecording();
        audioRecording.setGeneratedTitle(request.getGeneratedTitle());
        audioRecording.setUrl(request.getUrl());
        audioRecording.setAppUser(appUser);

        return audioRecordingRepository.save(audioRecording);
    }


}
