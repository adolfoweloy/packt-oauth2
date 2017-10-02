package example.packt.com.authcodeapp.web.profile;


import android.util.Log;

import java.util.Observable;
import java.util.Observer;

import example.packt.com.authcodeapp.web.WebClient;
import example.packt.com.authcodeapp.web.oauth2.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileReady implements Observer {

    /**
     * Give a chance for the view to be updated
     */
    public interface OnProfileResultCallback{
        void onSuccess(UserProfile userProfile);
        void onError(String message, Throwable t);
    }

    private final OnProfileResultCallback resultCallback;

    public UserProfileReady(OnProfileResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof AccessToken) {
            AccessToken accessToken = (AccessToken) o;

            WebClient provider = WebClient.create();

            Call<UserProfile> userProfileCall = provider.userProfile()
                    .token("Bearer " + accessToken.getValue());

            userProfileCall.enqueue(new Callback<UserProfile>() {
                @Override
                public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                    UserProfile userProfile = response.body();
                    resultCallback.onSuccess(userProfile);
                }

                @Override
                public void onFailure(Call<UserProfile> call, Throwable t) {
                    resultCallback.onError("Error trying to retrieve user profile", t);
                }
            });

        } else {
            throw new RuntimeException("invalid access requestToken being observed");
        }

    }


}
