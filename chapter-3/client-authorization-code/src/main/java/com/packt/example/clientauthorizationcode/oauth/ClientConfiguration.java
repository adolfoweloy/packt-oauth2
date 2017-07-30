package com.packt.example.clientauthorizationcode.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
public class ClientConfiguration {

    @Autowired
    private ClientTokenServices clientTokenServices;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;

    @Bean
    public OAuth2ProtectedResourceDetails authorizationCode() {
        AuthorizationCodeResourceDetails detailsForBookserver = new AuthorizationCodeResourceDetails();

        //@formatter:off
        detailsForBookserver.setId("oauth2server");
        detailsForBookserver.setTokenName("oauth_token");
        detailsForBookserver.setClientId("clientapp");
        detailsForBookserver.setClientSecret("123456");
        detailsForBookserver.setAccessTokenUri("http://localhost:8080/oauth/token");
        detailsForBookserver.setUserAuthorizationUri("http://localhost:8080/oauth/authorize");
        detailsForBookserver.setScope(Arrays.asList("read_profile"));
        detailsForBookserver.setPreEstablishedRedirectUri(("http://localhost:9000/callback"));
        detailsForBookserver.setUseCurrentUri(false);
        detailsForBookserver.setClientAuthenticationScheme(AuthenticationScheme.header);
        //@formatter:on

        return detailsForBookserver;
    }

    @Bean
    public OAuth2RestTemplate oauth2RestTemplate() {

        OAuth2ProtectedResourceDetails resourceDetails = authorizationCode();

        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails,
                oauth2ClientContext);

        AccessTokenProviderChain provider = new AccessTokenProviderChain(
                Arrays.asList(new AuthorizationCodeAccessTokenProvider()));

        provider.setClientTokenServices(clientTokenServices);
        template.setAccessTokenProvider(provider);

        return template;
    }

}
