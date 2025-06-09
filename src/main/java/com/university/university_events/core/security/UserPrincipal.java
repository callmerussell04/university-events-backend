package com.university.university_events.core.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.university.university_events.users.model.UserEntity;

public class UserPrincipal implements UserDetails {
    private final long id;
    private final String username;
    private final String password;
    private final Set<? extends GrantedAuthority> roles;
    private final boolean active;

    public UserPrincipal(UserEntity user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roles = Set.of(user.getRole());
        this.active = true;
    }

     public static UserPrincipal build(UserEntity user) {
        return new UserPrincipal(user);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }
}
