package com.packt.example.pop.oauth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

/**
 * This class is in charge of generating Key Pair for confirmation of token authenticity.
 * This is the Issuer per the RFC 7800
 */
@Component
class PoPTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalData = new HashMap<>();
        DefaultOAuth2AccessToken defaultAccessToken = (DefaultOAuth2AccessToken) accessToken;

        KeyPair keyPair = createRSA256KeyPair();

        JWK clientJwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .privateKey((RSAPrivateKey) keyPair.getPrivate())
            .keyID(UUID.randomUUID().toString()).build();

        // creates the additional claim to hold confirmation data
        // As per specification, this attribute should be CNF with a JWK structure inside it
        // because the CNF attribute can be used to carry other types of confirmation members (defined at RFC7800#3.1)
        additionalData.put("access_token_key", clientJwk.toJSONString());
        defaultAccessToken.setAdditionalInformation(additionalData);

        return defaultAccessToken;
    }

    /**
     * Generates the an RSA256 KeyPair that should be used by the Client to sign requests to Resource Server
     * @return
     */
    private KeyPair createRSA256KeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
