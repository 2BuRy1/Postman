package com.example.postman.services;

import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.OAuthUserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuthUserRepository oAuthUserRepository;

    public CustomOAuth2UserService(OAuthUserRepository oAuthUserRepository) {
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthUser user = OAuthUser.builder()
                .provider(registrationId)
                .providerId(oAuth2User.getAttribute("id").toString())
                .name(oAuth2User.getAttribute("name"))
                .email(oAuth2User.getAttribute("email"))
                .avatarUri(oAuth2User.getAttribute("avatar_url"))
                .build();

        oAuthUserRepository.findByProviderId(user.getProviderId())
                .orElseGet(() -> oAuthUserRepository.save(user));

        return user;
    }
}