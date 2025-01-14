package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import com.kmits.projects.speechemotionrecognition.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private AppUserRepository appUserRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "+email));

        return AppUserDetails.build(appUser);
    }
}
