package com.packt.example.popresourceserver.validator;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

@Component
public class PoPTokenValidator {

    public void validateJWSToken(JWSObject jws, String accessTokenKey) {
        try {
            JWK jwk = JWK.parse(accessTokenKey);
            JWSVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
            if (!jws.verify(verifier)) {
                throw new InvalidTokenException("Client hasn't possession of given token");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}