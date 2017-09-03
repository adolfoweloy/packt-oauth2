package com.packt.example.popresourceserver.oauth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

@Service
public class PoPRemoteTokenServices implements ResourceServerTokenServices {

    @Autowired
    private RemoteResourceServerProperties resourceServerProperties;

    private AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    private final RestTemplate restTemplate;

    public PoPRemoteTokenServices() {
        restTemplate = new RestTemplate();
        ((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException,
            InvalidTokenException {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();

        try {
            JWSObject jwsObject = JWSObject.parse(accessToken);
            Payload payload = jwsObject.getPayload();
            JSONObject jsonObject = payload.toJSONObject();

            formData.add("token", (String) jsonObject.get("at"));
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getAuthorizationHeader(
                resourceServerProperties.getClientId(), resourceServerProperties.getClientSecret()));
            Map<String, Object> map = postForMap(resourceServerProperties.getCheckTokenUri(), formData, headers);

            if (map.containsKey("error")) { throw new InvalidTokenException(accessToken); }

            if (!Boolean.TRUE.equals(map.get("active"))) {
                throw new InvalidTokenException(accessToken);
            }

            JWK jwk = JWK.parse((String) map.get("access_token_key"));
            JWSVerifier verifier = new RSASSAVerifier((RSAKey) jwk);
            if (!jwsObject.verify(verifier)) {
                throw new InvalidTokenException("Client hasn't possession of given token");
            }

            return tokenConverter.extractAuthentication(map);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    private String getAuthorizationHeader(String clientID, String clientSecret) {
        String creds = String.format("%s:%s", clientID, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        @SuppressWarnings("rawtypes")
        Map map = restTemplate.exchange(path, HttpMethod.POST,
                new HttpEntity<MultiValueMap<String, String>>(formData, headers), Map.class).getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = map;
        return result;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    public void setTokenConverter(AccessTokenConverter tokenConverter) {
        this.tokenConverter = tokenConverter;
    }

}
