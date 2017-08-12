package com.example.dynamicserver.oauth.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.dynamicserver.oauth.model.DynamicClientDetails;
import com.example.dynamicserver.util.RandomHelper;

@Component
public class DynamicClientDetailsFactory {

    private RandomHelper randomHelper;

    @Autowired
    DynamicClientDetailsFactory(RandomHelper randomHelper) {
        this.randomHelper = randomHelper;
    }

    public DynamicClientDetails create(ClientMetadata clientMetadata) {
        DynamicClientDetails clientDetails = new DynamicClientDetails();
        clientDetails.setClientId(randomHelper.nextString(10, 32));
        clientDetails.setClientSecret(randomHelper.nextString(32, 32));
        clientMetadata.getGrantTypes().forEach(grantType -> clientDetails.addAuthorizedGrantTypes(grantType));
        clientMetadata.getRedirectUris().forEach(uri -> clientDetails.addRegisteredRedirectUri(uri));

        if (clientMetadata.getScope() != null) {
            String[] scopes = clientMetadata.getScope().split("\\s");
            for (String scope : scopes) {
                clientDetails.addScope(scope);
            }
        }

        clientDetails.setClientName(clientMetadata.getClientName());
        clientDetails.setClientUri(clientMetadata.getClientUri());
        clientDetails.setSoftwareId(clientMetadata.getSoftwareId());
        clientDetails.setResponseTypes(clientMetadata.getResponseTypes());
        clientDetails.setTokenEndpointAuthMethod(clientMetadata.getTokenEndpointAuthMethod());

        return clientDetails;
    }

}
