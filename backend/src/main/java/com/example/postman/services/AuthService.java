package com.example.postman.services;

import com.example.postman.models.BasicUser;
import com.example.postman.repositories.BasicUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;
import java.util.Map;

@Service
public class AuthService {

    private final BasicUserRepository basicUserRepository;


    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(BasicUserRepository basicUserRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.basicUserRepository = basicUserRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }




    public Map<String, String> register(BasicUser basicUser){

        if(basicUserRepository.existsByUsername(basicUser.getUsername())) throw new BadCredentialsException("Username is already in use");

        basicUser.setPassword(passwordEncoder.encode(basicUser.getPassword()));




        System.out.println(basicUser.getPassword());


            basicUserRepository.save(basicUser);

        return Map.of("access", jwtService.createBaseAccessToken(basicUser.getUsername()), "refresh", jwtService.createBaseRefreshToken(basicUser.getUsername()));





    }


    public Map<String, String> login(BasicUser basicUser){

        basicUserRepository.findBasicUserByUsername(basicUser.getUsername()).orElseThrow(() -> new UsernameNotFoundException("no user with such username"));




        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        basicUser.getUsername(),
                        basicUser.getPassword()
                )
        );





        return Map.of("access", jwtService.createBaseAccessToken(basicUser.getUsername()), "refresh", jwtService.createBaseRefreshToken(basicUser.getUsername()));
    }

}
