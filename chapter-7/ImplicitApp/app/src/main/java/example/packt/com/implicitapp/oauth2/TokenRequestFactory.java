package example.packt.com.implicitapp.oauth2;


import example.packt.com.implicitapp.oauth2.dto.AccessTokenRequest;

public class TokenRequestFactory {
    public static AccessTokenRequest create(String code) {

        AccessTokenRequest tokenRequest = new AccessTokenRequest();
        tokenRequest.setCode(code);
        tokenRequest.setGrantType("authorization_code");
        tokenRequest.setRedirectUri("oauth2://profile/callback");
        tokenRequest.setScope("read_profile");

        return tokenRequest;
    }
}
