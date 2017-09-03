package com.packt.example.popresourceserver.oauth;

import java.io.UnsupportedEncodingException;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("packt.oauth.resource-server")
public class RemoteResourceServerProperties {

    private String clientId;

    private String clientSecret;

    private String checkTokenUri;

    public String getAuthorizationHeader() {
        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCheckTokenUri() {
        return checkTokenUri;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setCheckTokenUri(String checkTokenUri) {
        this.checkTokenUri = checkTokenUri;
    }

}
