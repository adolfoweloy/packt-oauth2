package com.packt.example.jweserver.oauth.authorization;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.packt.example.jweserver.oauth.jwt.JweTokenEnhancer;
import com.packt.example.jweserver.oauth.jwt.JweTokenSerializer;
import com.packt.example.jweserver.oauth.jwt.JwkSignature;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
            .tokenStore(jwtTokenStore())
            .tokenEnhancer(tokenEnhancer())
            .accessTokenConverter(accessTokenConverter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
            .inMemory()
            .withClient("clientapp")
            .secret("123456")
            .scopes("read_profile")
            .authorizedGrantTypes("password", "authorization_code");
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new JweTokenEnhancer(accessTokenConverter(), new JweTokenSerializer(jwkSignature()));
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwkSignature jwkSignature() {

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

            JwkSignature signature = new JwkSignature();
            signature.setBase64EncodedKey(encodedKey);
            return signature;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey(jwkSignature().getBase64EncodedKey());
        return tokenConverter;
    }

}
