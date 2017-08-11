package com.example.dynamicserver.oauth;

import java.util.HashSet;
import java.util.Set;

public class ClientMetadata {

    private Set<String> redirectUris = new HashSet<>();

    private TokenEndpointAuthMethod tokenEndpointAuthMethod = TokenEndpointAuthMethod.CLIENT_SECRET_BASIC;

    private Set<GrantType> grantTypes = new HashSet<>();

    private String responseTypes = "code";

    private String clientName;

    private String clientUri;

    private String logoUri;

    private String scope;

    private Set<String> contacts = new HashSet<>();

    private String tosUri;

    private String policyUri;

    private String softwareId;

    private String softwareVersion;

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public TokenEndpointAuthMethod getTokenEndpointAuthMethod() {
        return tokenEndpointAuthMethod;
    }

    public Set<GrantType> getGrantTypes() {
        return grantTypes;
    }

    public String getResponseTypes() {
        return responseTypes;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientUri() {
        return clientUri;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public String getScope() {
        return scope;
    }

    public Set<String> getContacts() {
        return contacts;
    }

    public String getTosUri() {
        return tosUri;
    }

    public String getPolicyUri() {
        return policyUri;
    }

    public String getSoftwareId() {
        return softwareId;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

}
