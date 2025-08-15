package com.example.postman.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {


    @Value("${jwt.secret}")
    String jwtSecret;


    public String createAccessTokenForOauth(String id, String provider) {


        return Jwts.builder()
                .subject(id)
                .claim("provider", provider)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .compact();

    }


    public String createBaseAccessToken(String username) {


        return Jwts.builder()
                .subject(username)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .compact();
    }


    public String createOathRefreshToken(String id, String provider) {
        return Jwts.builder()
                .subject(id)
                .claim("provider", provider)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5))
                .compact();

    }


    public String createBaseRefreshToken(String username ){


        return Jwts.builder()
                .subject(username)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 5))
                .compact();

    }



    public boolean validateToken(String token){

        return extractAllClaims(token).getExpiration().before(new Date());


    }



    public List<String> getOauthSubjects(String token){


        Claims claims = extractAllClaims(token);

        List<String> list = new ArrayList<>();

        list.add(claims.getSubject());
        list.add((String) claims.get("provider"));

        return list;


    }

    public String getBaseSubject(String token){
        return extractAllClaims(token).getSubject();
    }


    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSigningKey())
                .build().parseSignedClaims(token).getBody();


    }


private Key getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}

}
