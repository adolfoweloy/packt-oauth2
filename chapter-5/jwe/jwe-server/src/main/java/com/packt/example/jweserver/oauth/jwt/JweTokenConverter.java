package com.packt.example.jweserver.oauth.jwt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;

public class JweTokenConverter {

    public String encode(String payload) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
            Payload payloadObject = new Payload(payload);

            JWEObject jweObject = new JWEObject(header, payloadObject);
            jweObject.encrypt(new DirectEncrypter(key));

            return jweObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
