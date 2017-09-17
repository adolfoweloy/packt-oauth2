package com.packt.example.facebookloginoauth2.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class FacebookClientService {

    @Autowired
    private FacebookProperties properties;

    public String getAppToken() {
        RestTemplate restTemplate = new RestTemplate();

        URI appTokenUri = URI.create(properties.getAppTokenUri());
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", properties.getClientId());
        params.add("client_secret", properties.getClientSecret());
        params.add("grant_type", "client_credentials");

        RequestEntity<AppToken> requestEntity = new RequestEntity<>(
            HttpMethod.GET,
            UriComponentsBuilder
                .fromUri(appTokenUri).queryParams(params).build().toUri());

        ResponseEntity<AppToken> result = restTemplate.exchange(
                requestEntity, AppToken.class);

        if (result.getStatusCode().is2xxSuccessful()) {
            return result.getBody().getAccessToken();
        }

        throw new RuntimeException("It wasn't possible to retrieve userInfo");
    }

    private static class AppToken {
        private String accessToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
