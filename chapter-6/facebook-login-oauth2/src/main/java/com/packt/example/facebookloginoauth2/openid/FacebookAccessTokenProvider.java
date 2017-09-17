package com.packt.example.facebookloginoauth2.openid;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;

public class FacebookAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }

}
