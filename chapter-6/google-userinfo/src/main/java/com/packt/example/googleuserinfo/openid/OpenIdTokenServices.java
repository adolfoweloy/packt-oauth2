package com.packt.example.googleuserinfo.openid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OpenIdTokenServices implements ClientTokenServices {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OpenIDTokenExtractor extractor;

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource,
                                Authentication authentication, OAuth2AccessToken accessToken) {

        OpenIDTokenExtractor.Claims claims = extractor.extractFrom(accessToken);
        Optional<GoogleUser> userAuth = repository.findByOpenIDSubject(claims.sub);

        GoogleUser newUserAuthentication = userAuth.orElseGet(() -> {
            OpenIDAuthentication openId = new OpenIDAuthentication();
            openId.setProvider(claims.iss);
            openId.setSubject(claims.sub);
            openId.setExpirationTime(claims.exp);
            openId.setToken(claims.at_hash);

            GoogleUser newUser = new GoogleUser(claims.email, openId);
            return newUser;
        });

        if (!newUserAuthentication.isCredentialsNonExpired()) {
            newUserAuthentication.getOpenIDAuthentication().setExpirationTime(claims.exp);
        }

        updateUserInfo(accessToken, newUserAuthentication.getOpenIDAuthentication());

        repository.save(newUserAuthentication);
    }

    private void updateUserInfo(OAuth2AccessToken accessToken, OpenIDAuthentication openIdAuth) {
        // Retrieves data from UserInfo endpoint to update GoogleUser's details
        Map<String, String> userInfo = userInfoService.getUserInfoFor(accessToken);
        String userName = userInfo.get("name");

        // validates the subject attribute from userInfo result with sub claim of the current token
        if (!userInfo.get("sub").equals(openIdAuth.getSubject())) {
            throw new RuntimeException("sub element of ID Token must be the same from UserInfo endpoint");
        }

        openIdAuth.setName(userName);
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
