package com.packt.example.popresourceserver.validator;

import java.util.Map;

import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenValidator {

    public void validateTokenInfo(Map<String, Object> tokenInfo) {
        if (!tokenInfo.containsKey("error")
            && !Boolean.TRUE.equals(tokenInfo.get("active"))) {
            throw new InvalidTokenException("invalid access token");
        }
    }

}
