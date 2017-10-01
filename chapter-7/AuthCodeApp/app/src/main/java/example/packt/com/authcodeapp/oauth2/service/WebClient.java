package example.packt.com.authcodeapp.oauth2.service;

import example.packt.com.authcodeapp.oauth2.interceptor.BasicAuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Represents the OAuth2 Provider service
 */
public class WebClient {

    private final Retrofit retrofit;
    private final OAuth2Service oAuth2Service;

    public static WebClient create() {
        return new WebClient(null);
    }

    public static WebClient createBasicProtectedResource() {
        return new WebClient(new BasicAuthenticationInterceptor());
    }

    private WebClient(BasicAuthenticationInterceptor basicAuthentication) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(logging);

        if (basicAuthentication != null) {
            client.addInterceptor(basicAuthentication);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.167:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();

        oAuth2Service = new OAuth2Service(retrofit);
    }

    public OAuth2Service oauth2() {
        return oAuth2Service;
    }

    public UserProfileService userProfile() {
        return retrofit.create(UserProfileService.class);
    }

}
