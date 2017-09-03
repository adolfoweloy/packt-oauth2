package com.packt.example.popclient.oauth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONObject;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;

@Component
public class HttpRequestWithPoPSignatureInterceptor
    implements ClientHttpRequestInterceptor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {
        OAuth2ClientContext clientContext = applicationContext.getBean(OAuth2ClientContext.class);
        OAuth2AccessToken accessToken = clientContext.getAccessToken();

        request.getHeaders().set("Authorization", "Bearer " + createJws(accessToken));
        return execution.execute(request, body);
    }

    private String createJws(OAuth2AccessToken accessToken) {
        try {
            // signing the request with RSA in such a way that the resource server
            // can validates that this client possesses the private key issued by the authorization server.
            JWK jwk = JWK.parse((String) accessToken.getAdditionalInformation().get("access_token_key"));

            Map<String, String> payloadContent = new HashMap<String, String>();
            payloadContent.put("at", accessToken.getValue());
            payloadContent.put("u", "localhost:9000");
            JSONObject json = new JSONObject(payloadContent);

            RSASSASigner signer = new RSASSASigner((RSAKey) jwk);
            JWSObject jwsObject = new JWSObject(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID()).build(),
                new Payload(json));

            jwsObject.sign(signer);
            return jwsObject.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
