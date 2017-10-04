package example.packt.com.authcodeapp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import example.packt.com.authcodeapp.BuildConfig;
import example.packt.com.authcodeapp.R;
import example.packt.com.authcodeapp.client.ClientAPI;
import example.packt.com.authcodeapp.client.oauth2.AccessToken;
import example.packt.com.authcodeapp.client.oauth2.OAuth2StateManager;
import example.packt.com.authcodeapp.client.oauth2.TokenStore;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener {

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

    private String generateState() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void onClick(View view) {

        AccessToken accessToken = tokenStore.getToken();
        if (accessToken != null && !accessToken.isExpired()) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return;
        }

        // starts oauth flow if there is no valid access token
        oauth2StateManager.saveState(generateState());

        // creates the authorization URI to redirect user
        Uri authorizationUri = new Uri.Builder()
                .scheme("http")
                .encodedAuthority(ClientAPI.BASE_URL)
                .path("/oauth/authorize")
                .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
                .appendQueryParameter("scope", "read_profile")
                .appendQueryParameter("state", oauth2StateManager.getState())
                .build();

        Intent authorizationIntent = new Intent(Intent.ACTION_VIEW);
        authorizationIntent.setData(authorizationUri);
        startActivity(authorizationIntent);
    }
}
