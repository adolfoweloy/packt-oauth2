package example.packt.com.resourceownerpassword.web.oauth2;

import java.util.HashMap;
import java.util.Map;

public class PasswordGrantTypeRequest {

    private String scope;
    private String username;
    private String password;

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put("scope", scope);
        map.put("grant_type", "password");
        map.put("username", username);
        map.put("password", password);
        return map;
    }
}
