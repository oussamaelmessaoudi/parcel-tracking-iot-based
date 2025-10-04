package com.tracksecure.iotgatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
         httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest()
                        .permitAll());
        return httpSecurity.build();
    }
}
