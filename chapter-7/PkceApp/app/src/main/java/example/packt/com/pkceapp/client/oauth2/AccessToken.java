package example.packt.com.pkceapp.client.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class AccessToken {

    @JsonProperty("access_token")
    private String value;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String scope;

    public boolean isExpired() {
        Date now = new Date();
        return expiresIn > now.getTime();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
