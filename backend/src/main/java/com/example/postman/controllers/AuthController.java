package com.example.postman.controllers;

import com.example.postman.models.BasicUser;
import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.BasicUserRepository;
import com.example.postman.services.AuthService;
import com.example.postman.services.JwtService;
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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {


    private final String frontendUrl = "http://localhost:3000";


    private boolean cookiesSecure = false;


    private final AuthService authService;


    private final JwtService jwtService;


    @Autowired
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }


    @PostMapping("/form-registration")
    public ResponseEntity<Map<String, String>> register(@RequestBody BasicUser basicUser, HttpServletResponse response) {

        System.out.println("priver register");

        Map<String, String> tokens = authService.register(basicUser);

        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "base")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.get("access"))
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.get("refresh"))
                .httpOnly(true)
                .secure(false)
                .path("/auth")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();




        response.setHeader("Set-Cookie", accessCookie.toString());
        response.setHeader("Set-Cookie", refreshCookie.toString());
        response.setHeader("Set-Cookie", authType.toString());

        return ResponseEntity.ok(Map.of("status", "success"));


    }


    @PostMapping("/form-login")
    public ResponseEntity<?> login(@RequestBody BasicUser basicUser, HttpServletResponse response) {
        System.out.println("privet login");
        Map<String, String> tokens = authService.login(basicUser);



        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "base")
                .httpOnly(false)
                .secure(false) // для localhost
                .path("/")
                .sameSite("Lax")
                .build();

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokens.get("access"))
                .httpOnly(false)
                .secure(false) // для localhost
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokens.get("refresh"))
                .httpOnly(true)
                .secure(false) // для localhost
                .path("/auth")
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

        System.out.println(principal.getAttributes());



        OAuthUser oAuthUser = null;

        if (registrationId.equals("google")) {

            oAuthUser = OAuthUser.builder()
                    .email(principal.getAttribute("email"))
                    .login(principal.getAttribute("name"))
                    .provider("google")
                    .providerId(principal.getAttribute("sub"))
                    .avatarUri(principal.getAttribute("picture")).build();

            System.out.println(oAuthUser);



        } else if (registrationId.equals("github")) {

             oAuthUser = OAuthUser.builder()
                    .email(principal.getAttribute("email"))
                    .login(principal.getAttribute("name"))
                    .provider("github")
                    .providerId(principal.getAttribute("id").toString())
                    .avatarUri(principal.getAttribute("avatar_url")).build();

            System.out.println(oAuthUser);

        }
        else{
            return (ResponseEntity<Void>) ResponseEntity.badRequest();

        }

        String accessToken = jwtService.createAccessTokenForOauth(oAuthUser.getProviderId(), oAuthUser.getProvider());
        String refreshToken = jwtService.createOathRefreshToken(oAuthUser.getProviderId(), oAuthUser.getProvider());


        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .maxAge(60 * 15)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(60L * 60 * 24 * 30)
                .sameSite("Lax")
                .build();

        ResponseCookie authType = ResponseCookie.from("AUTH_TYPE", "oauth").build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        response.addHeader("Set-Cookie",  authType.toString());


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
