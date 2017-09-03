package com.packt.example.pop.oauth;

import java.text.ParseException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.nimbusds.jose.jwk.JWK;

public class PoPResourceServerTokenServices implements ResourceServerTokenServices {

    private TokenStore tokenStore;
    private ResourceServerTokenServices delegate;

    public PoPResourceServerTokenServices(ResourceServerTokenServices delegate, TokenStore tokenStore) {
        this.delegate = delegate;
        this.tokenStore = tokenStore;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException,
            InvalidTokenException {
        return delegate.loadAuthentication(accessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);

        if (token == null) return null;

        OAuth2AccessToken copy = new DefaultOAuth2AccessToken(token);
        String accessTokenKey = (String) token.getAdditionalInformation().get("access_token_key");

        // this allows for not retrieving private key to resource server
        try {
            JWK jwk = JWK.parse(accessTokenKey);
            copy.getAdditionalInformation().put("access_token_key", jwk.toPublicJWK().toJSONString());

            return copy;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
