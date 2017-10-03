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
import example.packt.com.authcodeapp.client.oauth2.OAuth2StateManager;

public class MainActivity extends AppCompatActivity {

    private Button profileButton;

    private OAuth2StateManager oauth2StateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oauth2StateManager = new OAuth2StateManager(MainActivity.this);
        profileButton = (Button) findViewById(R.id.profile_button);

        // once the user click on profile button we
        // start the oauth2 authorization code flow
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // generates and save the state parameter
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
        });
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }

}
