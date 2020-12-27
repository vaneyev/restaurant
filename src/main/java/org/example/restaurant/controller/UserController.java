package org.example.restaurant.controller;

import org.example.restaurant.model.User;
import org.example.restaurant.service.JpaUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class UserController {

    @GetMapping
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal JpaUserDetails user) {
        return ResponseEntity.ok(new User(user.getId(), user.getUsername(), user.isAdmin()));
    }
}