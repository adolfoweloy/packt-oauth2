package com.packt.example.pop.oauth;

import java.text.ParseException;

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

        try {
            JWK jwk = JWK.parse((String) token.getAdditionalInformation().get("access_token_key"));
            token.getAdditionalInformation().put("access_token_key", jwk.toJSONString());

            DefaultOAuth2AccessToken copy = new DefaultOAuth2AccessToken(token);
            copy.getAdditionalInformation().put("access_token_key", jwk.toPublicJWK().toJSONString());

            super.storeAccessToken(copy, authentication);
        } catch (ParseException e) {
            throw new RuntimeException(
                "Error while trying to parse JWK access_token_key at PoPTokenStore.", e);
        }
    }


}
