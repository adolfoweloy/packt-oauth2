package example.packt.com.implicitapp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import example.packt.com.implicitapp.R;
import example.packt.com.implicitapp.client.ClientAPI;
import example.packt.com.implicitapp.client.oauth2.AccessToken;
import example.packt.com.implicitapp.client.oauth2.OAuth2StateManager;
import example.packt.com.implicitapp.client.oauth2.TokenStore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TokenStore tokenStore;
    private OAuth2StateManager oAuth2StateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenStore = new TokenStore(this);
        oAuth2StateManager = new OAuth2StateManager(this);

        Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        AccessToken accessToken = tokenStore.getToken();

        Intent intent;
        if (accessToken != null && !accessToken.isExpired()) {
            intent = new Intent(this, ProfileActivity.class);
        } else {
            String state = generateState();
            oAuth2StateManager.saveState(state);

            Uri authorizationUri = createAuthorizationURI(state);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setData(authorizationUri);
        }

        startActivity(intent);

    }

    private Uri createAuthorizationURI(String state) {
        return new Uri.Builder()
                .scheme("http")
                .encodedAuthority(ClientAPI.BASE_URL)
                .path("/oauth/authorize")
                .appendQueryParameter("client_id", "clientapp")
                .appendQueryParameter("response_type", "token")
                .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
                .appendQueryParameter("scope", "read_profile")
                .appendQueryParameter("state", state)
                .build();
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }

}
