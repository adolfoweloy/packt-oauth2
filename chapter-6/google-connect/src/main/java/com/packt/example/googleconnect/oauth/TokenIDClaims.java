package com.packt.example.googleconnect.oauth;

import org.codehaus.jackson.annotate.JsonProperty;

public class TokenIDClaims {

    @JsonProperty("azp")
    private String authorizedParty;

    @JsonProperty("aud")
    private String audience;

    @JsonProperty("sub")
    private String subjectIdentifier;

    @JsonProperty("email")
    private String email;

    @JsonProperty("at_hash")
    private String accessTokenHashValue;

    @JsonProperty("iss")
    private String issuerIdentifier;

    @JsonProperty("iat")
    private long issuedAt;

    @JsonProperty("exp")
    private long expirationTime;

    public String getAuthorizedParty() {
        return authorizedParty;
    }

    public void setAuthorizedParty(String authorizedParty) {
        this.authorizedParty = authorizedParty;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }

    public void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessTokenHashValue() {
        return accessTokenHashValue;
    }

    public void setAccessTokenHashValue(String accessTokenHashValue) {
        this.accessTokenHashValue = accessTokenHashValue;
    }

    public String getIssuerIdentifier() {
        return issuerIdentifier;
    }

    public void setIssuerIdentifier(String issuerIdentifier) {
        this.issuerIdentifier = issuerIdentifier;
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}
