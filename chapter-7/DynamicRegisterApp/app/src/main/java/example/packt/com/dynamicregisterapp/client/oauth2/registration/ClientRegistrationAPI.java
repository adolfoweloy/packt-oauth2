package example.packt.com.dynamicregisterapp.client.oauth2.registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ClientRegistrationAPI {

    @POST("/register")
    Call<RegistrationResponse> register(@Body ClientRegistrationRequest request);

}
