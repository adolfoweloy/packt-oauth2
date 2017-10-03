package example.packt.com.dynamicregisterapp.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;

import example.packt.com.dynamicregisterapp.R;
import example.packt.com.dynamicregisterapp.client.ClientAPI;
import example.packt.com.dynamicregisterapp.client.oauth2.AccessToken;
import example.packt.com.dynamicregisterapp.client.oauth2.AccessTokenRequestData;
import example.packt.com.dynamicregisterapp.client.oauth2.OAuth2StateManager;
import example.packt.com.dynamicregisterapp.client.oauth2.TokenResponseCallback;
import example.packt.com.dynamicregisterapp.client.profile.ProfileAuthorizationListener;
import example.packt.com.dynamicregisterapp.client.profile.UserProfile;

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
        OAuth2StateManager manager = new OAuth2StateManager(this);

        if (!manager.isValidState(state)) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        // to retrieve access requestToken our client app needs to authenticate using basic auth
        Call<AccessToken> tokenCallback = ClientAPI
                .oauth2().requestToken(AccessTokenRequestData.fromCode(code));

        TokenResponseCallback responseCallback = new TokenResponseCallback();
        responseCallback.addObserver(
            new ProfileAuthorizationListener(new ProfileAuthorizationListener.OnProfileResultCallback() {
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

        tokenCallback.enqueue(responseCallback);

    }

}