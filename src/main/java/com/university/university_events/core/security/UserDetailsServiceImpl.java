package com.university.university_events.core.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.university.university_events.users.model.UserEntity;
import com.university.university_events.users.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
            String.format("User '%s' not found", username)
        ));
        return UserPrincipal.build(user);
    }
}
