package com.kmits.projects.speechemotionrecognition.repositories;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AudioRecording;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioRecordingRepository extends JpaRepository<AudioRecording, Long> {
    List<AudioRecording> findByAppUser(AppUser appUser);
    List<AudioRecording> findAll();
    Optional<AudioRecording> findById(Long id);

}
