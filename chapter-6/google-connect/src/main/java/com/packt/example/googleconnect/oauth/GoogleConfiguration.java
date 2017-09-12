package com.packt.example.googleconnect.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;

@Configuration
@EnableOAuth2Client
public class GoogleConfiguration {

    @Autowired
    private OpenIdTokenServices clientTokenServices;

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId("client_id");
        details.setClientSecret("secret");

        // URLs retrieved from https://accounts.google.com/.well-known/openid-configuration
        details.setAccessTokenUri("https://www.googleapis.com/oauth2/v4/token");
        details.setPreEstablishedRedirectUri("http://localhost:8080/callback");
        details.setScope(Arrays.asList("openid", "email", "profile"));
        details.setUseCurrentUri(false);
        return details;
    }

    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ClientContext context) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails(), context);
        restTemplate.setAccessTokenProvider(getAccessTokenProvider());
        return restTemplate;
    }

    private AccessTokenProvider getAccessTokenProvider() {
        AccessTokenProviderChain providerChain = new AccessTokenProviderChain(
                Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
        providerChain.setClientTokenServices(clientTokenServices);
        return providerChain;
    }


}
