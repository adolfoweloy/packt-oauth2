package com.packt.example.resourceserverjwt.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        JwtTokenStore store = new JwtTokenStore(accessTokenConverter());
        return store;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setVerifier(verifier());
        converter.setAccessTokenConverter(defaultAccessTokenConverter());
        converter.setSigningKey("non-prod-signature");
        return converter;
    }

    @Bean
    public DefaultAccessTokenConverter defaultAccessTokenConverter() {
        DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        tokenConverter.setUserTokenConverter(userAuthenticationConverter());
        return tokenConverter;
    }

    @Bean
    public UserAuthenticationConverter userAuthenticationConverter() {
        DefaultUserAuthenticationConverter converter
            = new DefaultUserAuthenticationConverter();
        converter.setUserDetailsService(userDetailsService);
        return converter;
    }

    @Bean
    public SignatureVerifier verifier() {
        return new MacSigner("non-prod-signature");
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources)
            throws Exception {
        resources.tokenStore(tokenStore());
        resources.tokenServices(tokenServices());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .requestMatchers().antMatchers("/api/**");
    }
}
