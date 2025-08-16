package com.example.postman.models;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OAuthUser implements OAuth2User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String providerId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String avatarUri;

    @Column(nullable = true)
    private String email;


    @Override
    public Map<String, Object> getAttributes() {
        return Map.of(
                "providerId", this.providerId,
                "provider", this.provider,
                "name", this.name,
                "avatarUri", this.avatarUri,
                "email", this.email



        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("User"));
    }

    @Override
    public String getName() {
        return this.name;
    }
}
