package org.example.restaurant.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JpaUserDetails extends User {

    public JpaUserDetails(org.example.restaurant.model.User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), authorities);
    }
}
