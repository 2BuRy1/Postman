package com.example.postman.controllers;

import com.example.postman.models.BasicUser;
import com.example.postman.repositories.BasicUserRepository;
import com.example.postman.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {



    private final String frontendUrl = "http://localhost:3000";


    private boolean cookiesSecure  = false;


    private final AuthService authService;


    @Autowired
    public AuthController(AuthService authService, BasicUserRepository basicUserRepository){
        this.authService = authService;
    }


    @PostMapping("/form-registration")
    public ResponseEntity<Map<String, String>> register(@RequestBody BasicUser basicUser){

        System.out.println("priver register");

        String token = authService.register(basicUser);

        return ResponseEntity.ok(Map.of("token", token));


    }


    @PostMapping("/form-login")
    public ResponseEntity<?> login(@RequestBody BasicUser basicUser) {
        System.out.println("privet login");
        String token = authService.login(basicUser);
        return ResponseEntity.ok(Map.of("token", token));
    }


    @GetMapping("/noauth")
    public ResponseEntity<?> noAuth() {
        Map<String, String> body = new HashMap<>();
        body.put("message", "unauthorized");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }


    @GetMapping("/auth-success")
    public ResponseEntity<Void> handleAuthSuccess(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                  HttpServletResponse response, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        System.out.println(oAuth2AuthenticationToken.getCredentials());

        String subject = null;
        Map<String, Object> claims = new HashMap<>();
        System.out.println(principal.getAttributes());
    //TODO jwtService
       // Map<String, String> tokens = jwtService.generateTokens(claims, subject);
//        String accessToken = tokens.get("access_token");
//        String refreshToken = tokens.get("refresh_token");

        String accessToken = "access";
        String refreshToken = "refresh";
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/auth")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.status(302)
                .location(URI.create(frontendUrl))
                .build();
    }

    @GetMapping("/logout-and-redirect")
    public ResponseEntity<Void> logoutAndRedirect(HttpServletResponse response) {
        ResponseCookie deleteAccess = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(cookiesSecure)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ResponseCookie deleteRefresh = ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(cookiesSecure)
                .path("/auth")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", deleteAccess.toString());
        response.addHeader("Set-Cookie", deleteRefresh.toString());

        return ResponseEntity.status(302)
                .header("Location", frontendUrl)
                .build();
    }
}
