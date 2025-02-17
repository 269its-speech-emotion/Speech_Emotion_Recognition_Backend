package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.*;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import com.kmits.projects.speechemotionrecognition.repositories.AudioRecordingRepository;
import com.kmits.projects.speechemotionrecognition.requests.audiorecording.GetAudioRecordingResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AudioRecordingService {

    private final AudioRecordingRepository audioRecordingRepository;

    private final AppUserRepository appUserRepository;

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


    public List<GetAudioRecordingResponse> getAudioRecordings(){
        var audioRecordings = new ArrayList<GetAudioRecordingResponse>();
        for (AudioRecording audio : audioRecordingRepository.findAll())
            audioRecordings.add(new GetAudioRecordingResponse(audio));

        return audioRecordings;
    }


    public AudioRecording setAudioRecording(AudioRecording putRequest) {
        AudioRecording audioRecording = audioRecordingRepository.findById(putRequest.getId())
                .orElseThrow(() -> new EntityNotFoundException("Audio recording not found with ID: " + putRequest.getId()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails userPrincipal = (AppUserDetails) auth.getPrincipal();

        if(!Objects.equals(userPrincipal.getId(), audioRecording.getAppUser().getId())){
            throw new AccessDeniedException("You are not authorized to access this audio recording");
        }

        if (putRequest.getGeneratedTitle() != null && !putRequest.getGeneratedTitle().isEmpty()) {
            audioRecording.setGeneratedTitle(putRequest.getGeneratedTitle());
        } else {
            throw new IllegalArgumentException("GeneratedTitle cannot be null or empty");
        }

        if (putRequest.getUrl() != null && !putRequest.getUrl().isEmpty()) {
            audioRecording.setUrl(putRequest.getUrl());
        } else {
            throw new IllegalArgumentException("URL cannot be null or empty");
        }

        try {
            PredictedEmotionType predictedEmotionType = PredictedEmotionType.valueOf(putRequest.getPredictedEmotionType().toString());
            audioRecording.setPredictedEmotionType(predictedEmotionType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid PredictedEmotionType: " + putRequest.getPredictedEmotionType());
        }

        if (putRequest.getUserAssessment() != null) {

            try {
                UserAssessment userAssessment = UserAssessment.valueOf(putRequest.getUserAssessment().toString());
                audioRecording.setUserAssessment(userAssessment);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UserAssessment: " + putRequest.getUserAssessment());
            }
        }

        return audioRecordingRepository.save(audioRecording);
    }


    public void deleteAudioRecording(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        AppUserDetails userPrincipal= (AppUserDetails) auth.getPrincipal();

        AudioRecording audioRecording = audioRecordingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Audio recording found with id: "+id));

        if(!audioRecording.getAppUser().getId().equals(userPrincipal.getId())){
            throw new AccessDeniedException("You are not allowed to delete this audio recording");
        }

        audioRecordingRepository.delete(audioRecording);
    }
}