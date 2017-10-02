package example.packt.com.resourceownerpassword.web;

import android.util.Log;

import example.packt.com.resourceownerpassword.AuthenticationManager;
import example.packt.com.resourceownerpassword.dashboard.DashboardUserProfileHelper;
import example.packt.com.resourceownerpassword.web.oauth2.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AuthorizationCallback
    implements Callback<AccessToken> {

    private final AuthenticationManager authenticationManager;

    private final DashboardUserProfileHelper helper;

    public AuthorizationCallback(AuthenticationManager authenticationManager,
             DashboardUserProfileHelper helper) {
        this.authenticationManager = authenticationManager;
        this.helper = helper;
    }

    @Override
    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
        AccessToken accessToken = response.body();

        authenticationManager.setAccessToken(accessToken);

        helper.load(accessToken);
    }

    @Override
    public void onFailure(Call<AccessToken> call, Throwable t) {
        Log.e("AuthorizationCallback", "Error trying to retrieve access token", t);
    }
}
