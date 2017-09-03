package com.packt.example.popresourceserver.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("packt.oauth.resource-server")
public class RemoteResourceServerProperties {

    private String clientId;

    private String clientSecret;

    private String checkTokenUri;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCheckTokenUri() {
        return checkTokenUri;
    }

    public void setCheckTokenUri(String checkTokenUri) {
        this.checkTokenUri = checkTokenUri;
    }

}
