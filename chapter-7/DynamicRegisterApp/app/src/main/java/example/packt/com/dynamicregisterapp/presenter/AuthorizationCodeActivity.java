package example.packt.com.dynamicregisterapp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import example.packt.com.dynamicregisterapp.R;
import example.packt.com.dynamicregisterapp.client.ClientAPI;
import example.packt.com.dynamicregisterapp.client.oauth2.AccessToken;
import example.packt.com.dynamicregisterapp.client.oauth2.AccessTokenRequestData;
import example.packt.com.dynamicregisterapp.client.oauth2.OAuth2StateManager;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentials;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentialsRepository;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.OnClientRegistrationResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationCodeActivity  extends AppCompatActivity implements OnClientRegistrationResult {

    private String code;
    private String state;
    private OAuth2StateManager manager;
    private ClientCredentialsRepository clientCredentialsRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        manager = new OAuth2StateManager(this);
        clientCredentialsRepo = new ClientCredentialsRepository(this);

        Uri callbackUri = Uri.parse(getIntent().getDataString());

        code = callbackUri.getQueryParameter("code");
        state = callbackUri.getQueryParameter("state");

        // validates state
        if (!manager.isValidState(state)) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        // retrieve access token with the given authorization code
        clientCredentialsRepo.getClientCredentialsFor(this);
    }

    @Override
    public void onSuccessfulClientRegistration(ClientCredentials credentials) {
        Call<AccessToken> accessTokenCall = ClientAPI
                .oauth2(credentials)
                .requestToken(AccessTokenRequestData.fromCode(code));

        accessTokenCall.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken token = response.body();

                // go to the other activity with an access token in hands!!!!!!!

                Intent intent = new Intent(AuthorizationCodeActivity.this, ProfileActivity.class);
                intent.putExtra("access_token", token.getValue());
                startActivity(intent);

                finish();

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e("AuthorizationCode", "Error retrieving access token", t);
            }
        });
    }

    @Override
    public void onFailedClientRegistration(String message, Throwable t) {
        Log.e("AuthorizationCode", message, t);
    }
}
