package org.example.restaurant.controller;

import org.example.restaurant.model.User;
import org.example.restaurant.repository.UserRepository;
import org.example.restaurant.to.UserTo;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<UserTo> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findByName(authentication.getName());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new UserTo(
                user.get().getId(),
                authentication.getName(),
                authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")))
        );
    }
}
