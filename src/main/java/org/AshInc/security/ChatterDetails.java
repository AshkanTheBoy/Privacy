package org.AshInc.security;

import org.AshInc.model.Chatter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

// Implements Spring Security's UserDetails interface for Chatter entities
public class ChatterDetails implements UserDetails {

    private Chatter chatter;  // Holds the Chatter entity

    // Constructor that initializes ChatterDetails with a Chatter instance
    public ChatterDetails(Chatter chatter) {
        this.chatter = chatter;  // Sets the chatter
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returns the authorities granted to the user
        return Collections.singleton(new SimpleGrantedAuthority(chatter.getRole()));  // Creates a single authority based on the chatter's role
    }

    @Override
    public String getPassword() {
        // Retrieves the hashed password of the chatter
        return chatter.getPasswordHash();  // Returns the hashed password
    }

    @Override
    public String getUsername() {
        // Retrieves the chatter's login username
        return chatter.getLogin();  // Returns the login of the chatter
    }

    @Override
    public boolean isAccountNonExpired() {
        // Indicates whether the account is expired
        return true;  // Always returns true, meaning the account is not expired
    }

    @Override
    public boolean isAccountNonLocked() {
        // Indicates whether the account is locked
        return true;  // Always returns true, meaning the account is not locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Indicates whether the credentials are expired
        return true;  // Always returns true, meaning credentials are not expired
    }

    @Override
    public boolean isEnabled() {
        // Indicates whether the account is enabled
        return true;  // Always returns true, meaning the account is enabled
    }
}
