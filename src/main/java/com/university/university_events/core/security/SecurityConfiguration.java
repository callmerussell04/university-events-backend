package com.university.university_events.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

import com.university.university_events.core.configuration.Constants;
import com.university.university_events.users.model.UserRole;
import com.university.university_events.core.security.UserDetailsServiceImpl;

import lombok.NoArgsConstructor;

@Configuration
@EnableWebSecurity
@NoArgsConstructor
public class SecurityConfiguration {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //httpSecurity.headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        httpSecurity.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);

        httpSecurity.authorizeHttpRequests(requests -> requests
                .requestMatchers(Constants.API_URL + "/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, Constants.API_URL + "/user/*/device-token").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(HttpMethod.GET, Constants.API_URL + "/event/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(HttpMethod.GET, Constants.API_URL + "/survey/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(Constants.API_URL + "/survey/take-survey").hasRole(UserRole.STUDENT.name())
                .requestMatchers(HttpMethod.GET, Constants.API_URL + "/invitation/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(HttpMethod.PUT, Constants.API_URL + "/invitation/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(HttpMethod.GET, Constants.API_URL + "/user/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name(), UserRole.STUDENT.name())
                .requestMatchers(Constants.API_URL + "/user/**").hasRole(UserRole.ADMIN.name())
                .requestMatchers(Constants.API_URL + "/**").hasAnyRole(UserRole.ADMIN.name(), UserRole.EMPLOYEE.name())
                .anyRequest().authenticated());

        httpSecurity.authenticationProvider(authenticationProvider());
        httpSecurity.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
