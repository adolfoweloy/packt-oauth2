package com.packt.example.customclaimsjwt.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import com.packt.example.customclaimsjwt.security.ResourceOwnerUserDetails;

@Component
public class AdditionalClaimsTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalData = new HashMap<>();

        ResourceOwnerUserDetails resourceOwner = (ResourceOwnerUserDetails) authentication.getPrincipal();
        additionalData.put("email", resourceOwner.getEmail());

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        token.setAdditionalInformation(additionalData);

        return accessToken;
    }

}
