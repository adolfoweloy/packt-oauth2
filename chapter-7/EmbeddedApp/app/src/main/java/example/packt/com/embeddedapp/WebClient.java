package example.packt.com.embeddedapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Represents the OAuth2 Provider service
 */
public class WebClient {

    private final Retrofit retrofit;

    public static final String BASE_URL = "http://192.168.11.190:8080";

    public WebClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(logging);
        client.addInterceptor(new ErrorInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client.build())
                .build();

    }

    public UserProfileService userProfile() {
        return retrofit.create(UserProfileService.class);
    }

}
