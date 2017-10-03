package example.packt.com.authcodeapp.client.profile;


import java.util.Observable;
import java.util.Observer;

import example.packt.com.authcodeapp.client.ClientAPI;
import example.packt.com.authcodeapp.client.oauth2.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileAuthorizationListener implements Observer {

    private final OnProfileResultCallback resultCallback;

    public ProfileAuthorizationListener(OnProfileResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof AccessToken) {
            AccessToken accessToken = (AccessToken) o;

            Call<UserProfile> call = ClientAPI
                    .userProfile().token(accessToken.getValue());

            call.enqueue(new Callback<UserProfile>() {
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
            throw new RuntimeException("Invalid access requestToken being observed");
        }

    }

    public interface OnProfileResultCallback{
        void onSuccess(UserProfile userProfile);
        void onError(String message, Throwable t);
    }
}
