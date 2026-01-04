package com.sliit.healthins.config;

import com.sliit.healthins.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // For CUSTOMER and POLICYHOLDER roles, allow login if user exists (they can activate later)
        // For other roles, require isActive to be true
        if (user.getRole() == com.sliit.healthins.model.Role.CUSTOMER || 
            user.getRole() == com.sliit.healthins.model.Role.POLICYHOLDER) {
            // Customers should be able to log in if they exist in database
            // If inactive, we'll auto-activate them on first login
            return true;
        }
        return user.isActive();
    }

    public User getUser() {
        return user;
    }
}
