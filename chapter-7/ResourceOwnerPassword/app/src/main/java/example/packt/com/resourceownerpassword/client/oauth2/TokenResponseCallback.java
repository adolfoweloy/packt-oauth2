package example.packt.com.resourceownerpassword.client.oauth2;

import android.util.Log;

import java.util.Observable;

import example.packt.com.resourceownerpassword.login.AuthenticationManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Observable callback that stores access token when received one from authorization flow
 * and notifies observers that are waiting for an access token to do their job.
 */
public class TokenResponseCallback
    extends Observable
    implements Callback<AccessToken> {

    private final AuthenticationManager authenticationManager;

    public TokenResponseCallback(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
        AccessToken accessToken = response.body();

        authenticationManager.setAccessToken(accessToken);

        setChanged();
        notifyObservers(accessToken);
    }

    @Override
    public void onFailure(Call<AccessToken> call, Throwable t) {
        Log.e("TokenResponseCallback", "Error trying to retrieve access token", t);
    }
}
