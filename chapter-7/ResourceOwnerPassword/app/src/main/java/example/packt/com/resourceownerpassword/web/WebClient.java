package example.packt.com.resourceownerpassword.web;

import example.packt.com.resourceownerpassword.web.interceptor.BasicAuthenticationInterceptor;
import example.packt.com.resourceownerpassword.web.interceptor.ErrorInterceptor;
import example.packt.com.resourceownerpassword.web.oauth2.OAuth2Service;
import example.packt.com.resourceownerpassword.web.profile.UserProfileService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WebClient {

    private final Retrofit retrofit;
    private final OAuth2Service oauth2Service;

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
                .baseUrl("http://192.168.0.167:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();

        oauth2Service = new OAuth2Service(retrofit);
    }

    public UserProfileService userProfile() {
        return retrofit.create(UserProfileService.class);
    }

    public OAuth2Service oauth2() {
        return oauth2Service;
    }
}
