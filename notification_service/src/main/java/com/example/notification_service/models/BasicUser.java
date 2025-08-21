package com.example.notification_service.models;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

//@Entity
@Data
public class BasicUser implements UserDetails {

//@Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;



    private String username;

    private String password;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.username;
    }
}
