package com.example.postman.controllers;

import com.example.postman.exceptions.InvalidJwt;
import com.example.postman.models.BasicUser;
import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.BasicUserRepository;
import com.example.postman.repositories.OAuthUserRepository;
import com.example.postman.services.AuthService;
import com.example.postman.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {


    private final String frontendUrl = "http://localhost:3000";


    private boolean cookiesSecure = false;


    private final AuthService authService;

    private final OAuthUserRepository oAuthUserRepository;


    private final JwtService jwtService;


    @Autowired
    public AuthController(AuthService authService, OAuthUserRepository oAuthUserRepository, JwtService jwtService) {
        this.authService = authService;
        this.oAuthUserRepository = oAuthUserRepository;
        this.jwtService = jwtService;
    }


    @PostMapping("/form-register")
    public ResponseEntity<Map<String, String>> register(@RequestBody BasicUser basicUser, HttpServletResponse response) {

        System.out.println("priver register");

        Map<String, String> tokens = authService.register(basicUser);

        System.out.println(tokens.get("access"));

        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "base")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.get("access"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 5 )
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.get("refresh"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();




        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", authType.toString());

        return ResponseEntity.ok(Map.of("status", "success"));


    }


    @PostMapping("/form-login")
    public ResponseEntity<?> login(@RequestBody BasicUser basicUser, HttpServletResponse response) {
        System.out.println("privet login");
        Map<String, String> tokens = authService.login(basicUser);



        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "base")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.get("access"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 5 )
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.get("refresh"))
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie", authType.toString());

        return ResponseEntity.ok(Map.of("status", "success"));
    }



    @GetMapping("/auth-success")
    public ResponseEntity<Void> handleAuthSuccess(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                  HttpServletResponse response, OAuth2AuthenticationToken oAuth2AuthenticationToken, ClientRegistration clientRegistration) {
        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();



        OAuthUser oAuthUser = (OAuthUser) principal;

        String accessToken = jwtService.createAccessTokenForOauth(oAuthUser.getProviderId(), oAuthUser.getProvider());
        String refreshToken = jwtService.createOathRefreshToken(oAuthUser.getProviderId(), oAuthUser.getProvider());

        if(!oAuthUserRepository.existsOAuthUserByProviderIdAndProvider(oAuthUser.getProviderId(), oAuthUser.getProvider())) oAuthUserRepository.save(oAuthUser);


        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 5 )
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();

        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "oauth").build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie",  authType.toString());


        return ResponseEntity.status(302)
                .location(URI.create(frontendUrl + "/login"))
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


    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> auth(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, InvalidJwt {

        System.out.println("entered");
        Cookie[] cookies = request.getCookies();


        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "REFRESH_TOKEN".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);


        String authType = Arrays.stream(cookies)
                .filter(cookie -> "AUTH_TYPE".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);


        if (!jwtService.validateToken(refreshToken)) {
            System.out.println(refreshToken);
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(Map.of("status", "unauthorized"));
        }


        String token = "";
        if(authType.equals("oauth")){
            List<String> oauthSubjects = jwtService.getOauthSubjects(refreshToken);
            if (oauthSubjects.size() < 2) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(Map.of("status", "unauthorized"));
            }

            token = jwtService.createAccessTokenForOauth(oauthSubjects.get(0), oauthSubjects.get(1));

        }
        else if(authType.equals("basic")){
            String username = jwtService.getBaseSubject(refreshToken);
            if (username == null) {
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                        .body(Map.of("status", "unauthorized"));
            }

             token = jwtService.createBaseAccessToken(username);
        }

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());




        return ResponseEntity. ok(Map.of("status", "success"));


    }
}
