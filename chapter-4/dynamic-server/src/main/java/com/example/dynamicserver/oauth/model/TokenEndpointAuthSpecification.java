package com.example.dynamicserver.oauth.model;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TokenEndpointAuthSpecification {

    public boolean isSatisfiedBy(ClientMetadata clientDetails) {

        List<String> flowsForConfidentialClients = Arrays.asList(
           "authorization_code", "password", "client_credentials", "redirect_uri");

        boolean isConfidential = flowsForConfidentialClients.stream()
            .filter(grantType -> clientDetails.getGrantTypes().contains(grantType))
            .findAny().isPresent();

        if (isConfidential) {
            if ("none".equals(clientDetails.getTokenEndpointAuthMethod())) {
                return false;
            }
        }

        return true;
    }
}
