package com.packt.example.googleconnect.oauth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class OpenIdTokenServices implements ClientTokenServices {

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource,
                                            Authentication authentication) {
        return null;
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
