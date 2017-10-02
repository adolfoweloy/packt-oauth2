package example.packt.com.authcodeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

import example.packt.com.authcodeapp.BuildConfig;
import example.packt.com.authcodeapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // once the user click on profile button we
        // start the oauth2 authorization code flow

        Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // generates and save the state parameter

                String state = generateState();
                SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("state", state);
                editor.commit();

                // creates the authorization URI to redirect user

                Uri authorizationUri = new Uri.Builder()
                    .scheme("http")
                    .encodedAuthority("192.168.0.167:8080")
                    .path("/oauth/authorize")
                        .appendQueryParameter("client_id", BuildConfig.CLIENT_ID)
                        .appendQueryParameter("response_type", "code")
                        .appendQueryParameter("redirect_uri", "oauth2://profile/callback")
                        .appendQueryParameter("scope", "read_profile")
                        .appendQueryParameter("state", state)
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
