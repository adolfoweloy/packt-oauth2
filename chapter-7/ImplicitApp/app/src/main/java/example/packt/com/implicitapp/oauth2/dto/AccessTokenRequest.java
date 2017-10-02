package example.packt.com.implicitapp.oauth2.dto;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenRequest {

    private String code;
    private String scope;
    private String grantType;
    private String redirectUri;

    public void setCode(String code) {
        this.code = code;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("scope", scope);
        map.put("grant_type", grantType);
        map.put("redirect_uri", redirectUri);
        return map;
    }
}
