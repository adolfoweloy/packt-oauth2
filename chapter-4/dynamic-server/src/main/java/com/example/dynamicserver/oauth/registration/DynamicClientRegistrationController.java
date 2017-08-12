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

import com.example.dynamicserver.oauth.model.DynamicClientDetails;
import com.example.dynamicserver.oauth.model.GrantTypeAndResponseTypeCorrelationSpecification;
import com.example.dynamicserver.oauth.model.RegistrationError;

@Controller
public class DynamicClientRegistrationController {

    @Autowired
    private ClientRegistrationService clientRegistration;

    @Autowired
    private DynamicClientDetailsFactory clientDetailsFactory;

    @Autowired
    private GrantTypeAndResponseTypeCorrelationSpecification grantTypeAndResponseTypeCorrelation;

    /**
     * RFC7591
     * Definitions this endpoint MAY be an OAuth 2.0 protected resource
     * and it MAY accept an initial access token.
     *
     * As we are using open registration, these endpoint MAY be rate-limited to prevent a denial-of-service attack.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody ClientMetadata clientMetadata) {

        DynamicClientDetails clientDetails = clientDetailsFactory.create(clientMetadata);

        if (!grantTypeAndResponseTypeCorrelation.isSatisfiedBy(clientDetails)) {
            RegistrationError error = new RegistrationError(RegistrationError.INVALID_CLIENT_METADATA);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        clientRegistration.addClientDetails(clientDetails);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<ClientDetails>> list() {
        return new ResponseEntity<>(clientRegistration.listClientDetails(), HttpStatus.OK);
    }

}
