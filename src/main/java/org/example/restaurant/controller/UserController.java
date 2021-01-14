package org.example.restaurant.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.User;
import org.example.restaurant.model.security.JpaUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class UserController {

    @GetMapping
    public User getProfile(@AuthenticationPrincipal JpaUserDetails user) {
        log.info("Getting user details of {}", user.getUsername());
        return new User(user.getId(), user.getUsername(), user.isAdmin());
    }
}
