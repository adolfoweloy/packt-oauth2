package com.packt.example.popclient.oauth;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class OAuth2ClientTokenSevices implements ClientTokenServices {

    private ConcurrentHashMap<String, ClientUser> users = new ConcurrentHashMap<>();

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication) {
        ClientUser clientUser = getClientUser(authentication);

        if (clientUser.getAccessToken() == null) return null;

        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(clientUser.getAccessToken());
        oAuth2AccessToken.setAdditionalInformation(clientUser.getAdditionalInformation());
        Calendar expirationCalendar = Calendar.getInstance();
        expirationCalendar.setTimeInMillis(clientUser.getExpirationTime());
        oAuth2AccessToken.setExpiration(expirationCalendar.getTime());

        return oAuth2AccessToken;
    }

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource,
            Authentication authentication, OAuth2AccessToken accessToken) {
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTime(accessToken.getExpiration());

        ClientUser clientUser = getClientUser(authentication);

        clientUser.setAccessToken(accessToken.getValue());
        clientUser.setExpirationTime(expirationDate.getTimeInMillis());
        clientUser.setAdditionalInformation(accessToken.getAdditionalInformation());

        users.put(clientUser.getUsername(), clientUser);
    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails resource,
            Authentication authentication) {
        ClientUser clientUser = getClientUser(authentication);

        users.remove(clientUser.getUsername());
    }

    private ClientUser getClientUser(Authentication authentication) {
        String username = ((User) authentication.getPrincipal()).getUsername();
        ClientUser clientUser = users.get(username);

        if (clientUser == null) {
            clientUser = new ClientUser(username);
        }

        return clientUser;
    }

    private static class ClientUser {
        private String username;
        private String accessToken;
        private Map<String, Object> additionalInformation;
        private long expirationTime;

        public ClientUser(String username) {
            this.username = username;
        }
        public String getUsername() {
            return username;
        }
        public String getAccessToken() {
            return accessToken;
        }
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        public Map<String, Object> getAdditionalInformation() {
            return additionalInformation;
        }
        public void setAdditionalInformation(Map<String, Object> additionalInformation) {
            this.additionalInformation = additionalInformation;
        }
        public long getExpirationTime() {
            return expirationTime;
        }
        public void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }

    }
}
