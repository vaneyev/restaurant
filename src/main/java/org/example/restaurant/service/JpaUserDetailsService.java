package org.example.restaurant.service;

import org.example.restaurant.model.User;
import org.example.restaurant.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Service("userDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    private final ApplicationContext applicationContext;

    private JpaUserDetailsService self;

    @PostConstruct
    private void init() {
        self = applicationContext.getBean(JpaUserDetailsService.class);
    }

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository, ApplicationContext applicationContext) {
        this.userRepository = userRepository;
        this.applicationContext = applicationContext;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = self.getUser(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User %s not found.", username));
        }
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(user.get().isAdmin() ? "ROLE_ADMIN" : "ROLE_USER"));
        return new JpaUserDetails(user.get(), authorities);
    }

    @Cacheable("users")
    public Optional<User> getUser(String username) {
        return userRepository.findByName(username);
    }
}
