package com.kmits.projects.speechemotionrecognition.controllers;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class AppUSerController {

    @Autowired
    private AppUserService appUserService;

    @GetMapping("get-self")
    public AppUser getSelf(){
        return appUserService.getSelf();
    }
}
