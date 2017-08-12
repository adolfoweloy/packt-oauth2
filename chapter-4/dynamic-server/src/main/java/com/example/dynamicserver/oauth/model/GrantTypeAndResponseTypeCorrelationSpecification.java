package com.example.dynamicserver.oauth.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class GrantTypeAndResponseTypeCorrelationSpecification {

    private final static Map<String, String> correlation;

    static {
        correlation = new HashMap<>();
        correlation.put("authorization_code", "code");
        correlation.put("implicit", "token");
    }

    public boolean isSatisfiedBy(ClientMetadata clientDetails) {
        Set<String> responseTypes = clientDetails.getResponseTypes();
        Set<String> grantTypes = clientDetails.getGrantTypes();

        for (String grantType : grantTypes) {
            String responseCode = correlation.get(grantType);
            if (responseCode != null) {
                if (!responseTypes.contains(responseCode)) {
                    return false;
                }
            }
        }

        return true;
    }

}
