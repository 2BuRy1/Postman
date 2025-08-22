package com.example.notification_service.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.notification_service.models.BasicUser;
import com.example.notification_service.models.OAuthUser;
import com.example.notification_service.services.JwtService;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class JwtBaseFilter extends OncePerRequestFilter {



    private final JwtService jwtService;


    @Autowired
    public JwtBaseFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();


        Optional<Cookie> accessCookieOptional = findCookie(cookies, "ACCESS_TOKEN");


        Optional<Cookie> authTypeOptional = findCookie(cookies, "AUTH_TYPE");




        if(accessCookieOptional.isEmpty() || authTypeOptional.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        }



        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }



        String accessToken = accessCookieOptional.get().getValue();


        if(!jwtService.validateToken(accessToken)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;

        }

        String autType = authTypeOptional.get().getValue();


        System.out.println("we are here");
        System.out.println(autType);


        if ("oauth".equals(autType)){
            authenticateViaOAuth(accessToken);

        }
        else if("base".equals(autType)){;
            authenticateViaBasic(accessToken);
        }

        System.out.println("missed?");
        filterChain.doFilter(request, response);

    }


    public void authenticateViaOAuth(String access){

        List<String> subjects = jwtService.getOauthSubjects(access);


        if(subjects.size() != 2){
            return;
        }

        System.out.println("via oauth");
        OAuthUser oAuthUser = OAuthUser.builder().
                providerId(subjects.get(0))
                .provider(subjects.get(1))
                .build();



            OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oAuthUser, null, oAuthUser.getProviderId());

            SecurityContextHolder.getContext().setAuthentication(token);

        System.out.println(SecurityContextHolder.getContext().getAuthentication());

    }

    public void authenticateViaBasic(String access){

        String username = jwtService.getBaseSubject(access);


        BasicUser basicUser = new BasicUser();
        basicUser.setUsername(username);

        Authentication auth = new UsernamePasswordAuthenticationToken(basicUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        System.out.println("authenticated!!!✅");
    SecurityContextHolder.getContext().setAuthentication(auth);

    System.out.println(SecurityContextHolder.getContext().getAuthentication());

    }



    private Optional<Cookie> findCookie(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .findFirst();
    }
}
