package example.packt.com.implicitapp.oauth2.service;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Represents the OAuth2 Provider service
 */
public class WebClient {

    private final Retrofit retrofit;

    public WebClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.167:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();

    }

    public UserProfileService userProfile() {
        return retrofit.create(UserProfileService.class);
    }

}
