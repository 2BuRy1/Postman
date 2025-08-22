package com.example.postman.filter;

import com.example.postman.models.BasicUser;
import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.BasicUserRepository;
import com.example.postman.repositories.OAuthUserRepository;
import com.example.postman.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtBaseFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final OAuthUserRepository oAuthUserRepository;
    private final BasicUserRepository basicUserRepository;

    @Autowired
    public JwtBaseFilter(JwtService jwtService, OAuthUserRepository oAuthUserRepository, BasicUserRepository basicUserRepository) {
        this.jwtService = jwtService;
        this.oAuthUserRepository = oAuthUserRepository;
        this.basicUserRepository = basicUserRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Optional<Cookie> authTypeOptional = findCookie(cookies, "AUTH_TYPE");
        Optional<Cookie> accessTokenOptional = findCookie(cookies, "ACCESS_TOKEN");




        if (authTypeOptional.isEmpty() || accessTokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }


        String authType = authTypeOptional.get().getValue();
        String accessToken = accessTokenOptional.get().getValue();
        boolean valid = jwtService.validateToken(accessToken);

        if (!valid) {
            if ("/auth".equals(request.getRequestURI())) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            return;
        }

        if ("oauth".equals(authType)) {
            handleOAuthAuthentication(cookies, accessToken, filterChain);
        } else if ("base".equals(authType)) {
            handleBasicAuthentication(cookies, accessToken, response);
        }

        filterChain.doFilter(request, response);
    }

    private void handleOAuthAuthentication(Cookie[] cookies, String accessToken, FilterChain filterChain) {


        System.out.println("entered in ouath authentication");


        try {
            Optional<Cookie> refreshTokenOptional = findCookie(cookies, "REFRESH_TOKEN");

            if (refreshTokenOptional.isEmpty()) {
                return;
            }


            System.out.println("refresh cookie is here");

            String validToken = accessToken;


            List<String> oauthSubjects = jwtService.getOauthSubjects(validToken);
            if (oauthSubjects.size() < 2) {
                System.out.println("not many args");
                return;
            }

            Optional<OAuthUser> oAuthUserOptional = oAuthUserRepository.findOAuthUserByProviderIdAndProvider(
                    oauthSubjects.get(0), oauthSubjects.get(1));

            System.out.println("trying to authentocate");


            if (oAuthUserOptional.isPresent()) {
                OAuthUser user = oAuthUserOptional.get();
                Authentication auth = new OAuth2AuthenticationToken(
                        user,
                        user.getAuthorities(),
                        user.getProviderId()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            System.err.println("OAuth authentication error: " + e.getMessage());
        }
    }

    private void handleBasicAuthentication(Cookie[] cookies, String accessToken, HttpServletResponse response) {
        try {


            System.out.println("entered");

            String validToken = accessToken;

            String username = jwtService.getBaseSubject(validToken);
            if (username == null) {
                return;
            }



            Optional<BasicUser> basicUserOptional = basicUserRepository.findBasicUserByUsername(username);

            if (basicUserOptional.isPresent()) {
                BasicUser user = basicUserOptional.get();
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        user.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            System.err.println("Basic authentication error: " + e.getMessage());
        }
    }

    private Optional<Cookie> findCookie(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }


}

