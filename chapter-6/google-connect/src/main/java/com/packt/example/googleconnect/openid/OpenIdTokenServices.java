package com.packt.example.googleconnect.openid;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.packt.example.googleconnect.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class OpenIdTokenServices implements ClientTokenServices {

    @Autowired
    private OpenIDAuthenticationRepository repository;

    @Autowired
    private ObjectMapper jsonMapper;

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource,
        Authentication authentication) {
        if (authentication == null) return null;

        User user = (User) authentication.getPrincipal();
        Optional<OpenIDAuthentication> openIDAuthentication = repository.findByUser(user);

        if (openIDAuthentication.isPresent() && openIDAuthentication.get().hasExpired()) {
            return null;
        }

        return new DefaultOAuth2AccessToken(openIDAuthentication.get().getToken());
    }

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource,
                                Authentication authentication, OAuth2AccessToken accessToken) {

        Map<String, Object> claims = extractClaims(accessToken);
        Optional<OpenIDAuthentication> userAuth = repository.findBySubject((String) claims.get("sub"));

        OpenIDAuthentication newUserAuthentication = userAuth.orElseGet(() -> {
            OpenIDAuthentication openId = new OpenIDAuthentication();
            User newUser = new User((String) claims.get("email"));
            openId.setUser(newUser);
            openId.setProvider((String) claims.get("iss"));
            openId.setSubject((String) claims.get("sub"));
            openId.setExpirationTime((Integer) claims.get("exp"));
            openId.setToken((String) claims.get("at_hash"));
            return openId;
        });

        if (newUserAuthentication.hasExpired()) {
            newUserAuthentication.setExpirationTime((Integer) claims.get("exp"));
        }

        repository.save(newUserAuthentication);
    }

    private Map<String, Object> extractClaims(OAuth2AccessToken accessToken) {
        String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
        Jwt decodedToken = JwtHelper.decode(idToken);

        try {
            return jsonMapper.readValue(decodedToken.getClaims(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getDateTime(long timestamp) {
        return new Date(timestamp * 1000);
    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails resource,
        Authentication authentication) {
        throw new UnsupportedOperationException("Not supported operation");
    }
}
