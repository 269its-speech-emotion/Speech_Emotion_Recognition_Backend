package com.kmits.projects.speechemotionrecognition.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "audio_recordings")
@Getter
@Setter
public class AudioRecording {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "generated_title", nullable = false)
    private String generatedTitle;

    @Column(name = "url", nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "predicted_emotion_type")
    public PredictedEmotionType predictedEmotionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_assessment")
    public UserAssessment userAssessment;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    @JsonBackReference
    private AppUser appUser;

}
