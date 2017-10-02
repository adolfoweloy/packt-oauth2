package example.packt.com.authcodeapp.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import example.packt.com.authcodeapp.R;
import example.packt.com.authcodeapp.web.AccessTokenRequestCallback;
import example.packt.com.authcodeapp.web.oauth2.AuthorizationCodeRequestFactory;
import example.packt.com.authcodeapp.web.oauth2.AccessToken;
import example.packt.com.authcodeapp.web.WebClient;
import example.packt.com.authcodeapp.web.profile.UserProfile;
import example.packt.com.authcodeapp.web.profile.UserProfileReady;
import retrofit2.Call;

public class ProfileActivity extends AppCompatActivity {

    private TextView textName;
    private TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textName = (TextView) findViewById(R.id.profile_name);
        textEmail = (TextView) findViewById(R.id.profile_email);

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

        // to retrieve access requestToken our client app needs to authenticate using basic auth

        WebClient provider = WebClient.createBasicProtectedResource();

        // prepares the requestToken request

        Call<AccessToken> tokenCallback = provider
                .oauth2()
                .authorizationCodeService()
                .requestToken(AuthorizationCodeRequestFactory
                            .createTokenRequestFrom(code)
                            .getMap());

        AccessTokenRequestCallback accessTokenRequestCallback = new AccessTokenRequestCallback();
        accessTokenRequestCallback.addObserver(
            new UserProfileReady(new UserProfileReady.OnProfileResultCallback() {
                @Override
                public void onSuccess(UserProfile userProfile) {

                    textName.setText(userProfile.getName());
                    textEmail.setText(userProfile.getEmail());

                }

                @Override
                public void onError(String message, Throwable t) {
                    Log.e("ProfileActivity", message, t);
                }
        }));

        tokenCallback.enqueue(accessTokenRequestCallback);

    }

}