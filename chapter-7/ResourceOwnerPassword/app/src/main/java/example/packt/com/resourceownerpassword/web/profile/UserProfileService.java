package example.packt.com.resourceownerpassword.web.profile;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserProfileService {

    @GET("api/profile")
    Call<UserProfile> token(@Header("Authorization") String accessToken);

}
