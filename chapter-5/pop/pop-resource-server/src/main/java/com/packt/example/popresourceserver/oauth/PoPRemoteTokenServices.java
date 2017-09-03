package com.packt.example.popresourceserver.oauth;

import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.packt.example.popresourceserver.validator.AccessTokenValidator;
import com.packt.example.popresourceserver.validator.PoPTokenValidator;

@Service
public class PoPRemoteTokenServices implements ResourceServerTokenServices {

    @Autowired
    private RemoteResourceServerProperties resourceServerProperties;

    @Autowired
    private AccessTokenValidator tokenValidator;

    @Autowired
    private PoPTokenValidator popTokenValidator;

    private final AccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
        throws AuthenticationException, InvalidTokenException {

        try {
            JWSObject jwsObject = JWSObject.parse(accessToken);
            Payload payload = jwsObject.getPayload();
            JSONObject jsonObject = payload.toJSONObject();

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
            formData.add("token", (String) jsonObject.get("at"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", resourceServerProperties.getAuthorizationHeader());

            Map<String, Object> tokenInfo = postForMap(resourceServerProperties.getCheckTokenUri(), formData, headers);

            tokenValidator.validateTokenInfo(tokenInfo);
            popTokenValidator.validateJWSToken(jwsObject, (String) tokenInfo.get("access_token_key"));

            return tokenConverter.extractAuthentication(tokenInfo);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
        return restTemplate.exchange(path, HttpMethod.POST,
            new HttpEntity<>(formData, headers), Map.class).getBody();
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

}
