package com.packt.example.pop.oauth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PoPTokenEnhancer popTokenEnhancer;

    private TokenStore tokenStore = new InMemoryTokenStore();

    @Bean
    public DefaultTokenServices defaultServerTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenEnhancer(popTokenEnhancer);
        tokenServices.setTokenStore(tokenStore);
        return tokenServices;
    }

    @Bean
    public ResourceServerTokenServices resourceServerTokenServices() {
        return new PoPResourceServerTokenServices(defaultServerTokenServices(), tokenStore);
    }

    @Bean
    public CheckTokenEndpoint checkTokenEndpoint() {
        return new CheckTokenEndpoint(resourceServerTokenServices());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(popTokenEnhancer));

        endpoints
            .tokenServices(defaultServerTokenServices())
            .tokenStore(tokenStore)
            .tokenEnhancer(tokenEnhancerChain);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("hasAuthority('introspection')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
            .inMemory()
            .withClient("clientapp").secret("123456")
            .scopes("read_profile")
            .authorizedGrantTypes("authorization_code")
        .and()
            .withClient("resource-server").secret("123")
            .authorities("introspection");
    }

}
