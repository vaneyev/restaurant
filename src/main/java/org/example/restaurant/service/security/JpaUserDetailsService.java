package org.example.restaurant.service.security;

import lombok.RequiredArgsConstructor;
import org.example.restaurant.model.User;
import org.example.restaurant.model.security.JpaUserDetails;
import org.example.restaurant.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service("userDetailsService")
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = getUser(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User %s not found.", username));
        }
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.get().isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));
        return new JpaUserDetails(user.get(), authorities);
    }

    public Optional<User> getUser(String username) {
        return userRepository.findByName(username);
    }
}