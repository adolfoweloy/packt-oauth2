package example.packt.com.authcodeapp.web;

import example.packt.com.authcodeapp.web.interceptor.ErrorInterceptor;
import example.packt.com.authcodeapp.web.oauth2.OAuth2Service;
import example.packt.com.authcodeapp.web.profile.UserProfileService;
import example.packt.com.authcodeapp.web.interceptor.BasicAuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Represents the OAuth2 Provider service
 */
public class WebClient {

    public static final String BASE_URL = "10.0.2.2:8080";

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
        client.addInterceptor(new ErrorInterceptor());

        if (basicAuthentication != null) {
            client.addInterceptor(basicAuthentication);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + BASE_URL)
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
