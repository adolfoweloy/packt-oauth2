package example.packt.com.resourceownerpassword.client.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Calendar;
import java.util.Date;

public class AccessToken {

    @JsonProperty("access_token")
    private String value;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String scope;

    public boolean hasExpired() {
        Calendar expiresInCalendar = Calendar.getInstance();
        expiresInCalendar.setTimeInMillis(expiresIn * 1000);

        Calendar today = Calendar.getInstance();
        today.setTime(new Date());

        return expiresInCalendar.after(today);
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
