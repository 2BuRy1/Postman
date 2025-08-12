package com.example.postman.models;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OAuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String login;

    @Column(nullable = false, unique = true)
    private Long providerId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String avatarUri;

    @Column(nullable = true)
    private String email;




}
