package example.packt.com.dynamicregisterapp.client.oauth2;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenRequestData {

    public static Map<String, String> fromCode(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("scope", "read_profile");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "oauth2://profile/callback");
        return map;

    }

}
