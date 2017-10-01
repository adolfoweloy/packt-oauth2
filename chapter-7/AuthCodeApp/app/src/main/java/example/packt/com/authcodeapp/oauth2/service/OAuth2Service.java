package example.packt.com.authcodeapp.oauth2.service;

import retrofit2.Retrofit;

public class OAuth2Service {

    private Retrofit retrofit;

    public OAuth2Service(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public AuthorizationCodeService authorizationCodeService() {
        return retrofit.create(AuthorizationCodeService.class);
    }
}
