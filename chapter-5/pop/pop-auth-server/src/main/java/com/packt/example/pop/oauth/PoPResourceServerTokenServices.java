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

/**
 * PoPResourceServerTokenServices was created to override the readAccessToken behavior
 * so that additional information about PoP Key Pair contains only the public key.
 */
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
        return copyWithPublicKey(token);

    }

    /**
     * this allows for not retrieving private key to resource server
     */
    private OAuth2AccessToken copyWithPublicKey(OAuth2AccessToken token) {
        try {
            JWK jwk = JWK.parse((String) token.getAdditionalInformation().get("access_token_key"));
            OAuth2AccessToken copy = new DefaultOAuth2AccessToken(token);
            copy.getAdditionalInformation().put("access_token_key", jwk.toPublicJWK().toJSONString());

            return copy;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
