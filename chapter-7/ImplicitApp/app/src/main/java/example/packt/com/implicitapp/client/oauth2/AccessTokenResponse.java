package example.packt.com.implicitapp.client.oauth2;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenResponse {
    private AccessToken accessToken;
    private String state;
    private String error;
    private String errorDescription;

    public static AccessTokenResponse createFromFragmentURI(String fragment) {
        String[] queryParams = fragment.split("&");
        Map<String, String> parameters = new HashMap<>();
        for (String keyValue : queryParams) {
            String[] parameter = keyValue.split("=");
            String key = parameter[0];
            String value = parameter[1];
            parameters.put(key, value);
        }

        return new AccessTokenResponse(parameters);
    }

    private AccessTokenResponse(Map<String, String> params) {

        if (params.containsKey("error")) {
            this.error = params.get("error");
            this.errorDescription = params.get("error_description");
        } else {
            AccessToken token = new AccessToken();
            token.setValue(params.get("access_token"));
            token.setTokenType(params.get("token_type"));
            token.setExpiresIn(Long.valueOf(params.get("expires_in")));
            token.setScope(params.get("scope"));

            this.accessToken = token;
            this.state = params.get("state");
        }
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public String getState() {
        return state;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public boolean hasError() {
        return error != null;
    }
}
