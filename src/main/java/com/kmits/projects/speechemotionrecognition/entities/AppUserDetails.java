package com.kmits.projects.speechemotionrecognition.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class AppUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private List<AudioRecording> audioRecordings;

    public AppUserDetails(
            Long id,
            String username,
            String email,
            String password,
            Role role,
            List<AudioRecording> audioRecordings
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.audioRecordings = audioRecordings;
    }

    public static AppUserDetails build(AppUser appUser){
        return new AppUserDetails(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getEmail(),
                appUser.getPassword(),
                appUser.getRole(),
                appUser.getAudioRecordings()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "AppUserDetails{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", audioRecordings=" + audioRecordings +
                '}';
    }
}
