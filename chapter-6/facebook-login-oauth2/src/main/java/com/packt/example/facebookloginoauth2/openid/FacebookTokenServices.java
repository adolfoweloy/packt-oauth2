package com.packt.example.facebookloginoauth2.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class FacebookTokenServices implements ClientTokenServices {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource,
                                Authentication authentication, OAuth2AccessToken accessToken) {

        Map<String, String> userInfo = userInfoService.getUserInfoFor(accessToken);
        Optional<FacebookUser> userAuth = repository.findByFacebookId(userInfo.get("id"));

        FacebookUser newUser = userAuth.orElseGet(() -> {
            FacebookLoginData loginData = new FacebookLoginData();
            loginData.setName(userInfo.get("name"));
            loginData.setId(userInfo.get("id"));
            loginData.setExpirationTime(accessToken.getExpiration().getTime());
            loginData.setToken(accessToken.getValue());
            FacebookUser user = new FacebookUser(loginData);
            return user;
        });

        repository.save(newUser);
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource,
                                            Authentication authentication) {
        throw new UnsupportedOperationException("Not supported operation");
    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails resource,
        Authentication authentication) {
        throw new UnsupportedOperationException("Not supported operation");
    }
}
