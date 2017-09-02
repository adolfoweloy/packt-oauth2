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
 */
@Component
public class PoPTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalData = new HashMap<>();
        DefaultOAuth2AccessToken defaultAccessToken = (DefaultOAuth2AccessToken) accessToken;

        try {
            // generates the key pair that should be used by the Client to sign requests to Resource Server
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();

            JWK clientJwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();

            // creates the additional claim to hold confirmation data
            // (which the jwk conveys one type of token authenticity confirmation)
            // cnf can be used to carry other types of confirmation members as per RFC7800#3.1
            additionalData.put("access_token_key", clientJwk);

            defaultAccessToken.setTokenType("PoP");

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        defaultAccessToken.setAdditionalInformation(additionalData);

        return defaultAccessToken;
    }

}
