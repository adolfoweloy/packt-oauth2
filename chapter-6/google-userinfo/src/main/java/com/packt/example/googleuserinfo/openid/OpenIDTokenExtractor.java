package com.packt.example.googleuserinfo.openid;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OpenIDTokenExtractor {

    @Autowired
    private ObjectMapper jsonMapper;

    public Claims extractFrom(OAuth2AccessToken accessToken) {
        try {
            String idToken = accessToken.getAdditionalInformation().get("id_token").toString();
            Jwt decodedToken = JwtHelper.decode(idToken);
            return jsonMapper.readValue(decodedToken.getClaims(), Claims.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class Claims {
        public String iss;
        public String sub;
        public String at_hash;
        public String email;
        public Long exp;
    }
}
