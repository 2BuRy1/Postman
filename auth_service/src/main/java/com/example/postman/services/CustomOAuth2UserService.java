package com.example.postman.services;

import com.example.postman.models.OAuthUser;
import com.example.postman.repositories.OAuthUserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final OAuthUserRepository oAuthUserRepository;

    public CustomOAuth2UserService(OAuthUserRepository oAuthUserRepository) {
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        OAuthUser user = oAuthUserRepository.findByProviderId(oidcUser.getName())
                .orElseGet(() -> {
                    OAuthUser newUser = OAuthUser.builder()
                            .providerId(oidcUser.getName())
                            .provider(userRequest.getClientRegistration().getRegistrationId())
                            .name(oidcUser.getFullName())
                            .email(oidcUser.getEmail())
                            .avatarUri(oidcUser.getPicture())
                            .build();
                    return oAuthUserRepository.save(newUser);
                });

        return (OidcUser) user;
    }
}