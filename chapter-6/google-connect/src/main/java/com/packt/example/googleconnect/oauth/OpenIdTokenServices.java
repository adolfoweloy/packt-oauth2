package com.packt.example.googleconnect.oauth;

import com.packt.example.googleconnect.user.OpenIDAuthentication;
import com.packt.example.googleconnect.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class OpenIdTokenServices implements ClientTokenServices {

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource,
                                            Authentication authentication) {
        if (authentication == null) return null;

        User userDetails = (User) authentication.getPrincipal();
        OpenIDAuthentication openIDAuthentication = userDetails.getOpenIDAuthentication();

        if (openIDAuthentication.hasExpired()) {
            return null;
        }

        return new DefaultOAuth2AccessToken(userDetails.getToken());
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
