package example.packt.com.resourceownerpassword.client.profile;

import example.packt.com.resourceownerpassword.client.ClientAPI;
import example.packt.com.resourceownerpassword.client.oauth2.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Service that encapsulates the ClientAPI.userProfile usage
 */
public class UserProfileService {

    private final OnProfileResultCallback resultCallback;

    public UserProfileService(OnProfileResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    public void load(AccessToken accessToken) {
        Call<UserProfile> call = ClientAPI.userProfile().token(accessToken.getValue());
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile userProfile = response.body();
                resultCallback.onSuccess(userProfile);
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                resultCallback.onError("Error loading user profile", t);
            }
        });
    }
}
