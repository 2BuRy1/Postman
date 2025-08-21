package com.example.notification_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {


    @GetMapping("/test_not")
    public ResponseEntity<String> subscribe(){


        //TODO connect library for WEB-PUSH notifications


        return ResponseEntity.ok("meow");
    }


}
