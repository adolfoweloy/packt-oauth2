package example.packt.com.dynamicregisterapp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import example.packt.com.dynamicregisterapp.R;
import example.packt.com.dynamicregisterapp.client.ClientAPI;
import example.packt.com.dynamicregisterapp.client.oauth2.AccessToken;
import example.packt.com.dynamicregisterapp.client.oauth2.OAuth2StateManager;
import example.packt.com.dynamicregisterapp.client.oauth2.TokenStore;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentials;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentialsRepository;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.OnClientRegistrationResult;
import example.packt.com.dynamicregisterapp.client.profile.ProfileAuthorizationListener;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnClientRegistrationResult {

    private Button profileButton;

    private TokenStore tokenStore;

    private OAuth2StateManager oauth2StateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenStore = new TokenStore(this);
        oauth2StateManager = new OAuth2StateManager(MainActivity.this);
        profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        ClientCredentialsRepository credentialsRepo = new ClientCredentialsRepository(this);
        credentialsRepo.getClientCredentialsFor(this);
    }

    @Override
    public void onSuccessfulClientRegistration(ClientCredentials credentials) {

        AccessToken token = tokenStore.getToken();
        if (token != null && !token.isExpired()) {
            Intent intentProfile = new Intent(this, ProfileActivity.class);
            startActivity(intentProfile);
            return;
        }

        // if there isn't any valid access token start
        // oauth2 authorization code flow to retrieve an access token

        final String state = UUID.randomUUID().toString();

        oauth2StateManager.saveState(state);

        Uri authorizationUri = new Uri.Builder()
            .scheme("http")
            .encodedAuthority(ClientAPI.BASE_URL)
            .path("/oauth/authorize")
            .appendQueryParameter("client_id", credentials.getClientId())
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
            .appendQueryParameter("scope", "read_profile")
            .appendQueryParameter("state", state)
            .build();

        Intent authorizationIntent = new Intent(Intent.ACTION_VIEW);
        authorizationIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        authorizationIntent.setData(authorizationUri);
        startActivity(authorizationIntent);
    }

    @Override
    public void onFailedClientRegistration(String s, Throwable t) {
        Log.e("MainActivity", "Error trying to register client credentials", t);
    }

}
