package com.example.dynamicserver.oauth;

//@formatter:off
public enum GrantType {

    AUTHORIZATION_CODE("authorization_code"),
    IMPLICIT("implicit"),
    PASSWORD("password"),
    CLIENT_CREDENTIALS("client_credentials"),
    REFRESH_TOKEN("refresh_token"),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    SAML_BEARER("urn:ietf:params:oauth:grant-type:saml2-bearer");

    private String value;

    private GrantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
//@formatter:on