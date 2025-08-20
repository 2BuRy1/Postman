package com.example.postman.controllers;

import com.example.postman.models.BasicUser;
import com.example.postman.models.NotificationProducer;
import com.example.postman.models.NotificationSubscriber;
import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.NotificationProducerRepository;
import com.example.postman.repositories.OAuthUserRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class MainController {


    @Autowired
    private NotificationProducerRepository notificationProducerRepository;


    @Autowired
    private OAuthUserRepository repository;


    @GetMapping("/auth_check")
    public ResponseEntity<Map<String, String>> checkAuth( ) {

        return ResponseEntity.ok(Map.of("auth", "success"));
    }

    @GetMapping("/image")
    public ResponseEntity<Map<String, String>> getImage(@AuthenticationPrincipal Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();




        if (authentication.getPrincipal() instanceof OAuth2User user) {


            System.out.println(user);

            Optional<OAuthUser> casted;

            if(!(authentication.getPrincipal() instanceof DefaultOAuth2User)) {
              casted = Optional.of((OAuthUser) user);
            }
            else{
                casted = repository.findByProviderId(String.valueOf((Integer)((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("id")));
            }


            return ResponseEntity.ok(Map.of("image", casted.get().getAvatarUri()));

        }

        return ResponseEntity.ok(Map.of("image", "https://sun9-23.userapi.com/s/v1/if2/8-iRVp5dL179aNIJQphYWD7op5PM9aHxbWXHJ8vTB-yzD-6z6e8d9VYxDkA_HQzT85cXb3_NL0Y1yeL8FV-U6Dl2.jpg?quality=96&as=32x24,48x36,72x54,108x81,160x120,240x180,360x270,480x360,540x405,604x453&from=bu"));
    }




    @GetMapping("/test_endpoint")
    public ResponseEntity<String> test(){

        NotificationProducer notificationProducer = new NotificationProducer();

        notificationProducer.setSubscribers(List.of(NotificationSubscriber.builder()
                        .name("meow")
                        .token("gau")
                .build()));


        notificationProducerRepository.save(notificationProducer);

        System.out.println(notificationProducer.toString());

        return ResponseEntity.ok(notificationProducer.toString());
    }


    }
