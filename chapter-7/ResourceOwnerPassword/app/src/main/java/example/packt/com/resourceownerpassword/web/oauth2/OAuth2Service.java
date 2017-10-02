package example.packt.com.resourceownerpassword.web.oauth2;

import retrofit2.Retrofit;

public class OAuth2Service {

    private final Retrofit retrofit;

    public OAuth2Service(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public PasswordService password() {
        return retrofit.create(PasswordService.class);
    }
}
