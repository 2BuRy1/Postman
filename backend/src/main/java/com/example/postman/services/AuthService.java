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

@Service
public class AuthService {

    private final BasicUserRepository basicUserRepository;


    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    public AuthService(BasicUserRepository basicUserRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.basicUserRepository = basicUserRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }




    public String register(BasicUser basicUser){

        if(basicUserRepository.existsByUsername(basicUser.getUsername())) throw new BadCredentialsException("Username is already in use");

        basicUser.setPassword(passwordEncoder.encode(basicUser.getPassword()));




        System.out.println(basicUser.getPassword());

            //TODO jwt service to get jwt token

            basicUserRepository.save(basicUser);

        return "token-registration";




    }


    public String login(BasicUser basicUser){

        System.out.println("entered 1");




        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        basicUser.getUsername(),
                        basicUser.getPassword()
                )
        );
        System.out.println("enetered 2");


        basicUserRepository.findBasicUserByUsername(basicUser.getUsername()).orElseThrow(() -> new UsernameNotFoundException("no user with such username"));


        return "logged-token";
    }

}
