package com.packt.example.googleconnect.openid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packt.example.googleconnect.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OpenIdTokenServices implements ClientTokenServices {

    @Autowired
    private OpenIDAuthenticationRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource,
        Authentication authentication) {
        if (authentication == null) return null;

        User user = (User) authentication.getPrincipal();
        Optional<OpenIDAuthentication> openIDAuthentication = repository.findByUser(user);

        if (openIDAuthentication.isPresent() && openIDAuthentication.get().hasExpired()) {
            return null;
        }

        return new DefaultOAuth2AccessToken(openIDAuthentication.get().getToken());
    }

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource,
                                Authentication authentication, OAuth2AccessToken accessToken) {

    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails resource,
        Authentication authentication) {
        throw new UnsupportedOperationException("Operação não suportada");
    }
}
