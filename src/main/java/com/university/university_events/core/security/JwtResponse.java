package com.university.university_events.core.security;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;
    private boolean mfaRequired;

    public JwtResponse(String accessToken, Long id, String username, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.mfaRequired = false;
    }

    public JwtResponse(Long id, String username, List<String> roles, boolean mfaRequired) {
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.mfaRequired = mfaRequired;
        this.token = null;
    }
}