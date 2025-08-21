package com.example.notification_service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class JwtService {


    @Value("${jwt.secret}")
    private String jwtSecret;


    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getBody();

            boolean isExpired = claims.getExpiration().before(new Date());

            return !isExpired;

        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }


    public String getBaseSubject(String token){
        return extractAllClaims(token).getSubject();
    }


    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigningKey())
                .build().parseSignedClaims(token).getBody();


    }


    public List<String> getOauthSubjects(String token){


        Claims claims = extractAllClaims(token);

        List<String> list = new ArrayList<>();

        list.add(claims.getSubject());
        list.add((String) claims.get("provider"));

        return list;


    }


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


}
