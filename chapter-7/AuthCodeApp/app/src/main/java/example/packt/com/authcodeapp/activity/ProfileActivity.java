package example.packt.com.authcodeapp.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import example.packt.com.authcodeapp.R;
import example.packt.com.authcodeapp.web.AuthorizationCallback;
import example.packt.com.authcodeapp.web.oauth2.AuthorizationCodeRequestFactory;
import example.packt.com.authcodeapp.web.oauth2.AccessToken;
import example.packt.com.authcodeapp.web.WebClient;
import example.packt.com.authcodeapp.web.profile.UserProfileReady;
import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // extract data from redirection URI which is a uri scheme

        Uri callbackUri = Uri.parse(getIntent().getDataString());
        String code = callbackUri.getQueryParameter("code");
        String state = callbackUri.getQueryParameter("state");

        // validates state parameter

        SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String savedState = preferences.getString("state", "");

        if (!savedState.equals(state)) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        // to retrieve access token our client app needs to authenticate using basic auth

        WebClient provider = WebClient.createBasicProtectedResource();

        // prepares the token request

        Call<AccessToken> tokenCallback = provider
                .oauth2()
                .authorizationCodeService()
                .token(AuthorizationCodeRequestFactory.create(code).getMap());

        AuthorizationCallback authorizationCallback
            = new AuthorizationCallback();
        authorizationCallback.addObserver(new UserProfileReady(ProfileActivity.this));

        tokenCallback.enqueue(authorizationCallback);

    }

}