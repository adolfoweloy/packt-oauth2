package com.packt.example.customclaimsjwt.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class ResourceOwnerUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;

    private ResourceOwner resourceOwner;

    public ResourceOwnerUserDetails(ResourceOwner resourceOwner) {
        this.resourceOwner = resourceOwner;
    }

    public String getEmail() {
        return resourceOwner.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return resourceOwner.getPassword();
    }

    @Override
    public String getUsername() {
        return resourceOwner.getUsername();
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
        return true;
    }

}
