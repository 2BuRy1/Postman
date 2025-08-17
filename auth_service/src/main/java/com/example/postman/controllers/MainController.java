package com.example.postman.controllers;

import com.example.postman.models.BasicUser;
import com.example.postman.models.OAuthUser;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
public class MainController {


    @GetMapping("/auth_check")
    public ResponseEntity<Map<String, String>> checkAuth( ) {

        return ResponseEntity.ok(Map.of("auth", "success"));
    }

    @GetMapping("/image")
    public ResponseEntity<Map<String, String>> getImage(@AuthenticationPrincipal OAuthUser oAuthUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication instanceof OAuth2AuthenticationToken) {

                return ResponseEntity.ok(Map.of("image", oAuthUser.getAvatarUri()));

        }

        return ResponseEntity.ok(Map.of("image", "https://risovach.ru/upload/2015/03/mem/udivlenie_75793101_orig_.jpg"));
    }


    }
