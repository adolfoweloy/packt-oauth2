package com.example.dynamicserver.oauth.model;

import java.util.HashSet;
import java.util.Set;

public class DynamicMetadata {

    private String softwareId;
    private String tokenEndpointAuthMethod;
    private Set<String> responseTypes = new HashSet<>();
    private String clientName;
    private String clientUri;

    public String getSoftwareId() {
        return softwareId;
    }
    public void setSoftwareId(String softwareId) {
        this.softwareId = softwareId;
    }
    public String getTokenEndpointAuthMethod() {
        return tokenEndpointAuthMethod;
    }
    public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
        this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    }
    public Set<String> getResponseTypes() {
        return responseTypes;
    }
    public void setResponseTypes(Set<String> responseTypes) {
        this.responseTypes = responseTypes;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getClientUri() {
        return clientUri;
    }
    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

}
