package com.example.dynamicserver.oauth.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dynamicserver.oauth.model.DefaultClientDetails;
import com.example.dynamicserver.oauth.model.DynamicMetadata;
import com.example.dynamicserver.util.RandomHelper;

@Controller
@RequestMapping("/oauth")
public class DynamicClientRegistrationController {

    @Autowired
    private ClientRegistrationService jdbcClientRegistration;

    @Autowired
    private RandomHelper randomHelper;

    /**
     * RFC7591
     * Definitions this endpoint MAY be an OAuth 2.0 protected resource
     * and it MAY accept an initial access token.
     *
     * As we are using open registration, these endpoint MAY be rate-limited to prevent a denial-of-service attack.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody ClientMetadata clientMetadata) {
        System.out.println(clientMetadata);

        DefaultClientDetails clientDetails = new DefaultClientDetails();
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

        DynamicMetadata metadata = new DynamicMetadata();
        metadata.setClientName(clientMetadata.getClientName());
        metadata.setClientUri(clientMetadata.getClientUri());
        metadata.setSoftwareId(clientMetadata.getSoftwareId());
        metadata.setResponseTypes(clientMetadata.getResponseTypes());
        metadata.setTokenEndpointAuthMethod(clientMetadata.getTokenEndpointAuthMethod());

        clientDetails.setDynamicMetadata(metadata);

        jdbcClientRegistration.addClientDetails(clientDetails);
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.CREATED);

        return response;
    }

}
