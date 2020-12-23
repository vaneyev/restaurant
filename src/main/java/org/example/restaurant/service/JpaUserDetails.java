package org.example.restaurant.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JpaUserDetails extends User {

    public JpaUserDetails(Collection<? extends GrantedAuthority> authorities) {
        super("user", "{noop}pass", authorities);
    }
}
