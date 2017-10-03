package example.packt.com.authcodeapp.client.oauth2;

import android.util.Log;

import java.util.Observable;

import example.packt.com.authcodeapp.client.oauth2.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessTokenRequestCallback
        extends Observable
        implements Callback<AccessToken> {

    @Override
    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
        AccessToken accessToken = response.body();

        setChanged();
        notifyObservers(accessToken);
    }

    @Override
    public void onFailure(Call<AccessToken> call, Throwable t) {
        Log.e("ProfileActivity", "Error trying to retrieve access requestToken", t);
    }

}
