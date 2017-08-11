package com.example.dynamicserver.oauth;

import java.util.List;

import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

public class DynamicClientRegistrationService implements
        ClientRegistrationService, ClientDetailsService {

    @Override
    public void addClientDetails(ClientDetails clientDetails)
            throws ClientAlreadyExistsException {

    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails)
            throws NoSuchClientException {

    }

    @Override
    public void updateClientSecret(String clientId, String secret)
            throws NoSuchClientException {

    }

    @Override
    public void removeClientDetails(String clientId)
            throws NoSuchClientException {

    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return null;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId)
            throws ClientRegistrationException {
        return null;
    }

}
