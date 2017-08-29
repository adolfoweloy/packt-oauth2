package com.packt.example.jweresource.oauth.jwt;

import java.util.Collection;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

public class JweTokenStore implements TokenStore {
    private final TokenStore delegate;
    private final JwtAccessTokenConverter converter;
    private JweTokenSerializer tokenSerializer;
    private String signKey;

    public JweTokenStore(String signKey,
            TokenStore delegate, JwtAccessTokenConverter converter,
            JweTokenSerializer tokenSerializer) {
        this.signKey = signKey;
        this.delegate = delegate;
        this.converter = converter;
        this.tokenSerializer = tokenSerializer;
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = convertAccessToken(tokenValue);
        return accessToken;
    }

    private OAuth2AccessToken convertAccessToken(String tokenValue) {
        return converter.extractAccessToken(tokenValue, tokenSerializer.decode(signKey, tokenValue));
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return converter.extractAuthentication(tokenSerializer.decode(signKey, token));
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    // delegating remaining methods

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        delegate.storeAccessToken(token, authentication);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        delegate.removeAccessToken(token);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        delegate.storeRefreshToken(refreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return delegate.readRefreshToken(tokenValue);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return delegate.readAuthenticationForRefreshToken(token);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        delegate.removeRefreshToken(token);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        delegate.removeAccessTokenUsingRefreshToken(refreshToken);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return delegate.getAccessToken(authentication);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return delegate.findTokensByClientIdAndUserName(clientId, userName);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return delegate.findTokensByClientId(clientId);
    }

}
