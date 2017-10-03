package example.packt.com.implicitapp.client.profile;


import example.packt.com.implicitapp.client.profile.UserProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserProfileService {

    @GET("api/profile")
    Call<UserProfile> token(@Header("Authorization") String accessToken);

}
