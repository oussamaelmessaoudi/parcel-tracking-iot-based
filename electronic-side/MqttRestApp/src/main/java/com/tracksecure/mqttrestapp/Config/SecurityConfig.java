package com.tracksecure.mqttrestapp.Config ;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;


import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        // Your permitted paths for Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // --- THIS IS THE CHANGE ---
                        .requestMatchers("/hello").permitAll() // <-- Make this endpoint public

                        // Define rules for your other endpoints
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers("/user/**").hasAnyRole("admin", "user")

                        // Fallback: All other requests must be authenticated
                        .anyRequest().authenticated())
                .csrf(c -> c.disable())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    // --- ADD THIS NEW BEAN ---
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                // Check for OIDC-specific authority
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    Map<String, Object> realmAccess = oidcAuth.getIdToken().getClaim("realm_access");

                    if (realmAccess != null) {
                        // Extract roles from realm_access.roles
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        mappedAuthorities.addAll(roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toSet()));
                    }
                }
                // Check for standard OAuth2 authority
                else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                    Map<String, Object> attributes = oauth2Auth.getAttributes();
                    Map<String, Object> realmAccess = (Map<String, Object>) attributes.get("realm_access");

                    if (realmAccess != null) {
                        // Extract roles from realm_access.roles
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        mappedAuthorities.addAll(roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toSet()));
                    }
                }
            });

            return mappedAuthorities;
        };
    }
}