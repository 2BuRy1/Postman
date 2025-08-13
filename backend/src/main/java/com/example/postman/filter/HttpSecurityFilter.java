package com.example.postman.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class HttpSecurityFilter {



    private final CorsConfigurationSource corsConfigurationSource;

    private final AuthenticationProvider authenticationProvider;

    @Autowired
    public HttpSecurityFilter(CorsConfigurationSource corsConfigurationSource, AuthenticationProvider authenticationProvider) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    SecurityFilterChain httpSecurity(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2.loginPage("http://localhost:3000").defaultSuccessUrl("/auth-success", true))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/form-login", "/login", "/form-register", "/form-registration").permitAll()
                        .anyRequest().authenticated()
                ).authenticationProvider(authenticationProvider);



        return http.build();
    }




}
