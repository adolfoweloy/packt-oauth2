package example.packt.com.authcodeapp.web.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken {

    @JsonProperty("access_token")
    private String value;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String scope;

    public String getValue() {
        return value;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }
}
