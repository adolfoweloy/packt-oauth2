package com.packt.example.jweserver.oauth.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwkSignature {

    private String base64EncodedKey;

    public String getBase64EncodedKey() {
        return base64EncodedKey;
    }

    public void setBase64EncodedKey(String base64EncodedKey) {
        this.base64EncodedKey = base64EncodedKey;
    }

}
