package example.packt.com.authcodeapp.oauth2.service;


import example.packt.com.authcodeapp.profile.UserProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserProfileService {

    @GET("api/profile")
    Call<UserProfile> token(@Header("Authorization") String accessToken);

}
