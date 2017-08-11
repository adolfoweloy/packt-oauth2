package com.example.dynamicserver.oauth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

public class DefaultClientDetails implements ClientDetails {
    private static final long serialVersionUID = 1L;

    private String clientId;
    private Set<String> resourceIds = new HashSet<>();
    private Set<String> authorizedGrantTypes = new HashSet<>();
    private String clientSecret;
    private Set<String> scopes = new HashSet<>();
    private Set<String> registeredRedirectUri = new HashSet<>();
    private Set<GrantedAuthority> authorities = new HashSet<>();
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
    private Map<String, Object> additionalInformation = new HashMap<>();
    private DynamicMetadata dynamicMetadata;

    public DynamicMetadata getDynamicMetadata() {
        // TODO - retrieve dynamicMetaData from additionalInformation
        return dynamicMetadata;
    }

    public void setDynamicMetadata(DynamicMetadata dynamicMetadata) {
        this.dynamicMetadata = dynamicMetadata;
        // TODO - build additionalInformation from dynamicMetaData
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public void addResourceId(String resourceId) {
        resourceIds.add(resourceId);
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return authorizedGrantTypes.containsAll(Arrays.asList(
                "authorization_code", "password", "client_credentials"));
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return scopes.size() > 0;
    }

    public void addScope(String scope) {
        this.scopes.add(scope);
    }

    @Override
    public Set<String> getScope() {
        return scopes;
    }

    public void addAuthorizedGrantTypes(String... authorizedGrantTypes) {
        for (String grantType : authorizedGrantTypes) {
            this.authorizedGrantTypes.add(grantType);
        }
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public void addRegisteredRedirectUri(String redirectUri) {
        registeredRedirectUri.add(redirectUri);
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    public void addAuthority(GrantedAuthority authority) {
        authorities.add(authority);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableSet(authorities);
    }

    public void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(
            Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        return false;
    }

    public void addAdditionalInformation(String key, String value) {
        additionalInformation.put(key, value);
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }

}
