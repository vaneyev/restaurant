package org.example.restaurant.service.security;

import lombok.RequiredArgsConstructor;
import org.example.restaurant.model.User;
import org.example.restaurant.model.security.JpaUserDetails;
import org.example.restaurant.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service("userDetailsService")
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found.", username)));
        return new JpaUserDetails(user, user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet()));
    }

    @Service
    @RequiredArgsConstructor
    public static class UserService {

        private final UserRepository userRepository;

        @Cacheable("user")
        public Optional<User> getUserByName(String username) {
            return userRepository.findByName(username);
        }
    }
}
