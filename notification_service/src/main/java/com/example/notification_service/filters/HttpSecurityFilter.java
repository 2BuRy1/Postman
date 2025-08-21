package com.example.notification_service.filters;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityFilter {

    private final CorsConfigurationSource corsConfigurationSource;

    private final JwtBaseFilter jwtBaseFilter;

    @Autowired
    public HttpSecurityFilter(CorsConfigurationSource corsConfigurationSource, JwtBaseFilter jwtBaseFilter) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtBaseFilter = jwtBaseFilter;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(customizer -> customizer.configurationSource(
                        corsConfigurationSource
                ))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> {
                            request.anyRequest().authenticated();

                        }

                )
                .addFilterBefore(jwtBaseFilter, UsernamePasswordAuthenticationFilter.class)
                .build();



    }
}
