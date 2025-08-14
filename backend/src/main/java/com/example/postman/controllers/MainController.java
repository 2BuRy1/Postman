package com.example.postman.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {



    @GetMapping
    public void lal(){
        System.out.println(",eopw");
    }


}
