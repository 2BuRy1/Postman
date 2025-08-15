package com.example.postman.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {



    @PostMapping("/check_auth")
    public ResponseEntity<Map<String, String>> checkAuth(){
        System.out.println("got request");
        return ResponseEntity.ok(Map.of("auth", "success"));
    }


}
