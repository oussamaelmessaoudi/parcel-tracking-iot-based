package com.tracksecure.mqttrestapp.Config;

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
                        // Public endpoints - NO AUTHENTICATION REQUIRED
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/health").hasRole("admin")
                        .requestMatchers("/api/sensor/**").hasRole("admin")  // CHANGED: Make all sensor endpoints public

                        // All other requests require authentication
                        .anyRequest().authenticated())
                .csrf(c -> c.disable())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    Map<String, Object> realmAccess = oidcAuth.getIdToken().getClaim("realm_access");

                    if (realmAccess != null) {
                        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                        mappedAuthorities.addAll(roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toSet()));
                    }
                }
                else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                    Map<String, Object> attributes = oauth2Auth.getAttributes();
                    Map<String, Object> realmAccess = (Map<String, Object>) attributes.get("realm_access");

                    if (realmAccess != null) {
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