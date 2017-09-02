package com.packt.example.pop.oauth;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.nimbusds.jose.jwk.JWK;

/**
 * This extension, allows to return KeyPair to the client and store just the public key.
 */
public class PoPTokenStore extends InMemoryTokenStore {

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        JWK jwk = (JWK) token.getAdditionalInformation().get("access_token_key");
        token.getAdditionalInformation().put("access_token_key", jwk.toJSONObject());

        DefaultOAuth2AccessToken copy = new DefaultOAuth2AccessToken(token);
        copy.getAdditionalInformation().put("access_token_key", jwk.toPublicJWK().toJSONObject());

        super.storeAccessToken(copy, authentication);
    }


}
