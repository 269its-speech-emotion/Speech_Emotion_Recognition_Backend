package com.kmits.projects.speechemotionrecognition.utils;

import com.kmits.projects.speechemotionrecognition.dtos.AppUserDTO;
import com.kmits.projects.speechemotionrecognition.dtos.AudioRecordingDTO;
import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;

public class Utils {

    public static AppUserDTO mapAppUserToAppUserDTO(AppUser appUser){
        AppUserDTO appUserDTO = new AppUserDTO();

        appUserDTO.setId(appUser.getId());
        appUserDTO.setUsername(appUser.getUsername());
        appUserDTO.setEmail(appUser.getEmail());
        appUserDTO.setRole(appUser.getRole());
        appUserDTO.setEnabled(appUser.isEnabled());
        appUserDTO.setVerificationCode(appUser.getVerificationCode());

        return appUserDTO;
    }


    public static AudioRecordingDTO mapAudioRecordingToAudioRecordingDTO(AudioRecording audioRecording){
        AudioRecordingDTO audioRecordingDTO = new AudioRecordingDTO();

        audioRecordingDTO.setId(audioRecording.getId());
        audioRecordingDTO.setGeneratedTitle(audioRecording.getGeneratedTitle());
        audioRecordingDTO.setUrl(audioRecording.getUrl());
        audioRecordingDTO.setAppUser(Utils.mapAppUserToAppUserDTO(audioRecording.getAppUser()));

        return audioRecordingDTO;
    }

}
