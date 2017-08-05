package com.packt.example.clientresttemplate.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthorizationCodeTokenService {

    public String getAuthorizationEndpoint() {

        String endpoint = "http://localhost:8080/oauth/authorize";

        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("client_id", "clientapp");
        authParameters.put("response_type", "code");
        authParameters.put("redirect_uri",
                getEncodedUrl("http://localhost:9000/callback"));
        authParameters.put("scope", getEncodedUrl("read_profile"));

        return buildUrl(endpoint, authParameters);
    }

    private String buildUrl(String endpoint, Map<String, String> parameters) {
        //@formatter:off
        List<String> paramList = new ArrayList<>(parameters.size());

        parameters.forEach((name, value) -> {
            paramList.add(name + "=" + value);
        });

        return endpoint + "?" + paramList.stream()
              .reduce((a, b) -> a + "&" + b).get();
        //@formatter:on
    }

    private String getEncodedUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public OAuth2Token getToken(String authorizationCode) {
        RestTemplate rest = new RestTemplate();
        String authBase64 = encodeCredentials("clientapp", "123456");

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                getBody(authorizationCode), getHeader(authBase64),
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));

        ResponseEntity<OAuth2Token> responseEntity = rest.exchange(
                requestEntity, OAuth2Token.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }

        throw new RuntimeException("error trying to retrieve access token");
    }

    public String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = new String(Base64.getEncoder().encode(
                credentials.getBytes()));

        return encoded;
    }

    private MultiValueMap<String, String> getBody(String authorizationCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("scope", "read_profile");
        formData.add("code", authorizationCode);
        formData.add("redirect_uri", "http://localhost:9000/callback");
        return formData;
    }

    private HttpHeaders getHeader(String clientAuthentication) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Authorization", "Basic " + clientAuthentication);

        return httpHeaders;
    }

}
