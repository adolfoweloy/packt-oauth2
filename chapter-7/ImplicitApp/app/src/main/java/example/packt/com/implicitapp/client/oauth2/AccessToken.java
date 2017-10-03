package example.packt.com.implicitapp.client.oauth2;

public class AccessToken {

    private String value;
    private String tokenType;
    private Long expiresIn;
    private String scope;

    public String getValue() {
        return value;
    }

    public AccessToken setValue(String value) {
        this.value = value;
        return this;
    }

    public String getTokenType() {
        return tokenType;
    }

    public AccessToken setTokenType(String tokenType) {
        this.tokenType = tokenType;
        return this;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public AccessToken setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public AccessToken setScope(String scope) {
        this.scope = scope;
        return this;
    }
}
