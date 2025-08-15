package com.example.postman.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {



    @PostMapping("/lal")
    public ResponseEntity<String> lal(){
        System.out.println("got request");
        return ResponseEntity.ok("meow");
    }


}
