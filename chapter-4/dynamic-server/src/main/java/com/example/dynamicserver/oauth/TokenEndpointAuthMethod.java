package com.example.dynamicserver.oauth;

//@formatter:off
public enum TokenEndpointAuthMethod {

    NONE("none"),
    CLIENT_SECRET_POST("client_secret_post"),
    CLIENT_SECRET_BASIC("client_secret_basic");

    private String value;

    private TokenEndpointAuthMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
//@formatter:on