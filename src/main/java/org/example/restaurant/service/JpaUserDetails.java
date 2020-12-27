package org.example.restaurant.service;

import org.example.restaurant.model.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JpaUserDetails extends org.springframework.security.core.userdetails.User {

    private final User user;

    public JpaUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getName(), user.getPassword(), authorities);
        this.user = user;
    }

    public Long getId() {
        return user.getId();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }
}
