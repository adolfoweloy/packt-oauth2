package com.example.dynamicserver.oauth.model;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RedirectFlowSpecification {

    public boolean isSatisfiedBy(DynamicClientDetails clientDetails) {
        List<String> flowsWithRedirection = Arrays.asList("authorization_code", "implicit");

        boolean hasFlowWithRedirection = clientDetails.getAuthorizedGrantTypes().stream()
            .filter(grantType -> flowsWithRedirection.contains(grantType))
            .findAny().isPresent();

        if (hasFlowWithRedirection) {
            return clientDetails.getRegisteredRedirectUri().size() > 0;
        }

        return false;
    }
}
