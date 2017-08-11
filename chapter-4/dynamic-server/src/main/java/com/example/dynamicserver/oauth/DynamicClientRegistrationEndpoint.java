package com.example.dynamicserver.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/oauth")
public class DynamicClientRegistrationEndpoint {

    private DynamicClientRegistrationService registrationService;

    @Autowired
    public DynamicClientRegistrationEndpoint(
            DynamicClientRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/register")
    public ResponseEntity<String> register(ClientMetadata clientMetadata) {
        System.out.println(clientMetadata);

        DefaultClientDetails clientDetails = new DefaultClientDetails();

        registrationService.addClientDetails(clientDetails);
        ResponseEntity<String> response = new ResponseEntity<>(
                HttpStatus.CREATED);

        return response;
    }

}
