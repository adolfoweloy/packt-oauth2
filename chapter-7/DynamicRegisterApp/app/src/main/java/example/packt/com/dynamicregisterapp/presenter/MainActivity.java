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
import example.packt.com.dynamicregisterapp.client.oauth2.OAuth2StateManager;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentials;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.ClientCredentialsRepository;
import example.packt.com.dynamicregisterapp.client.oauth2.registration.OnClientRegistrationResult;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnClientRegistrationResult {

    private Button profileButton;

    private OAuth2StateManager oauth2StateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        authorizationIntent.setData(authorizationUri);
        startActivity(authorizationIntent);
    }

    @Override
    public void onFailedClientRegistration(String s, Throwable t) {
        Log.e("MainActivity", "Error trying to register client credentials", t);
    }

}
