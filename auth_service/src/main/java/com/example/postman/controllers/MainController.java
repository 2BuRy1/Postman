package com.example.postman.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MainController {



    @GetMapping("/auth_check")
    public ResponseEntity<Map<String, String>> checkAuth(){
        System.out.println("got request");
        return ResponseEntity.ok(Map.of("auth", "success"));
    }

    @GetMapping("button_pizdec")
    public ResponseEntity<Map<String, String>> increment(@RequestParam("button") Integer buttonVal, HttpServletRequest request){

        var session = request.getSession();

        if(session.getAttribute("button") == null) session.setAttribute("button", buttonVal);


        session.setAttribute("button",  (Integer) session.getAttribute("button") + 1);


        return ResponseEntity.ok(Map.of("value", session.getAttribute("button").toString()));


    }






}
