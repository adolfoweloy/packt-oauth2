package example.packt.com.authcodeapp.web.oauth2;

public class AuthorizationCodeRequestFactory {
    public static AuthorizationCodeRequest create(String code) {

        AuthorizationCodeRequest tokenRequest = new AuthorizationCodeRequest();
        tokenRequest.setCode(code);
        tokenRequest.setRedirectUri("oauth2://profile/callback");
        tokenRequest.setScope("read_profile");

        return tokenRequest;
    }
}
