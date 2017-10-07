package example.packt.com.pkceapp.client;

import example.packt.com.pkceapp.client.interceptor.BearerTokenHeaderInterceptor;
import example.packt.com.pkceapp.client.interceptor.ErrorInterceptor;
import example.packt.com.pkceapp.client.interceptor.OAuth2ClientAuthenticationInterceptor;
import example.packt.com.pkceapp.client.oauth2.OAuth2API;
import example.packt.com.pkceapp.client.profile.UserProfileAPI;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ClientAPI {

    public static final String BASE_URL = "10.0.2.2:8080";

    private final Retrofit retrofit;

    public static UserProfileAPI userProfile() {
        ClientAPI api = new ClientAPI(null);
        return api.retrofit.create(UserProfileAPI.class);
    }

    public static OAuth2API oauth2() {
        ClientAPI api = new ClientAPI(new OAuth2ClientAuthenticationInterceptor());
        return api.retrofit.create(OAuth2API.class);
    }

    private ClientAPI(OAuth2ClientAuthenticationInterceptor clientAuthentication) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(logging);
        client.addInterceptor(new ErrorInterceptor());
        client.addInterceptor(new BearerTokenHeaderInterceptor());

        if (clientAuthentication != null) {
            client.addInterceptor(clientAuthentication);
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();
    }

}
