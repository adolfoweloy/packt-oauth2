package com.example.dynamicserver.oauth.registration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.dynamicserver.oauth.model.ClientMetadata;
import com.example.dynamicserver.oauth.model.DynamicClientDetails;
import com.example.dynamicserver.oauth.model.DynamicClientDetailsFactory;
import com.example.dynamicserver.oauth.model.GrantTypeAndResponseTypeCorrelationSpecification;
import com.example.dynamicserver.oauth.model.RedirectFlowSpecification;
import com.example.dynamicserver.oauth.model.RegistrationError;
import com.example.dynamicserver.oauth.model.TokenEndpointAuthSpecification;

@Controller
public class DynamicClientRegistrationController {

    @Autowired
    private ClientRegistrationService clientRegistration;

    @Autowired
    private DynamicClientDetailsFactory clientDetailsFactory;

    @Autowired
    private GrantTypeAndResponseTypeCorrelationSpecification grantTypeAndResponseTypeCorrelation;

    @Autowired
    private RedirectFlowSpecification redirectFlowSpecification;

    @Autowired
    private TokenEndpointAuthSpecification tokenEndpointAuthSpecification;

    /**
     * RFC7591
     * Definitions this endpoint MAY be an OAuth 2.0 protected resource
     * and it MAY accept an initial access token.
     *
     * As we are using open registration, these endpoint MAY be rate-limited to prevent a denial-of-service attack.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody ClientMetadata clientMetadata) {

        if (!grantTypeAndResponseTypeCorrelation.isSatisfiedBy(clientMetadata)) {
            RegistrationError error = new RegistrationError(RegistrationError.INVALID_CLIENT_METADATA);
            error.setErrorDescription("Check the correlation between authorized grant types and response code");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        if (!redirectFlowSpecification.isSatisfiedBy(clientMetadata)) {
            RegistrationError error = new RegistrationError(RegistrationError.INVALID_REDIRECT_URI);
            error.setErrorDescription("You must specify redirect_uri when using flows with redirection");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        if (!tokenEndpointAuthSpecification.isSatisfiedBy(clientMetadata)) {
            RegistrationError error = new RegistrationError(RegistrationError.INVALID_CLIENT_METADATA);
            error.setErrorDescription("When must specify token endpoint auth method when registering confidential client");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        DynamicClientDetails clientDetails = clientDetailsFactory.create(clientMetadata);

        clientRegistration.addClientDetails(clientDetails);
        return new ResponseEntity<>(createResponse(clientDetails), HttpStatus.CREATED);
    }

    private ClientRegistrationResponse createResponse(DynamicClientDetails clientDetails) {
        ClientRegistrationResponse response = new ClientRegistrationResponse();
        response.setClientId(clientDetails.getClientId());
        response.setClientSecret(clientDetails.getClientSecret());
        response.setClientName(clientDetails.getClientName());
        response.setClientUri(clientDetails.getClientUri());
        response.setGrantTypes(clientDetails.getAuthorizedGrantTypes());
        response.setRedirectUris(clientDetails.getRegisteredRedirectUri());
        response.setResponseTypes(clientDetails.getResponseTypes());
        response.setScope(clientDetails.getScope().stream().reduce((a, b) -> a + " " + b).get());
        response.setSoftwareId(clientDetails.getSoftwareId());
        response.setTokenEndpointAuthMethod(clientDetails.getTokenEndpointAuthMethod());
        return response;
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientDetails>> list() {
        return new ResponseEntity<>(clientRegistration.listClientDetails(), HttpStatus.OK);
    }

}
