package com.packt.example.pop.oauth;

public class ConfirmationClaim {

    private Object jwk;

    @Deprecated
    ConfirmationClaim() { }

    public ConfirmationClaim(Object jwk) {
        this.jwk = jwk;
    }

    public Object getJwk() {
        return jwk;
    }

    public void setJwk(Object jwk) {
        this.jwk = jwk;
    }

}
