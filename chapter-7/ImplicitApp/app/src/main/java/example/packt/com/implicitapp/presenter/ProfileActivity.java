package example.packt.com.implicitapp.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import example.packt.com.implicitapp.R;
import example.packt.com.implicitapp.client.ClientAPI;
import example.packt.com.implicitapp.client.oauth2.AccessTokenResponse;
import example.packt.com.implicitapp.client.oauth2.OAuth2StateManager;
import example.packt.com.implicitapp.client.profile.UserProfile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {

    private OAuth2StateManager oauth2StateManager;

    private TextView textName;
    private TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textName = (TextView) findViewById(R.id.profile_name);
        textEmail = (TextView) findViewById(R.id.profile_email);

        oauth2StateManager = new OAuth2StateManager(this);

        // extract data from redirection URI which is a uri scheme

        Uri callbackUri = Uri.parse(getIntent().getDataString());
        AccessTokenResponse response = AccessTokenResponse
                .createFromFragmentURI(callbackUri.getFragment());

        if (response.hasError()) {
            Toast.makeText(this, response.getErrorDescription(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!oauth2StateManager.isValidState(response.getState())) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<UserProfile> getUserProfile = ClientAPI.userProfile().token(response.getAccessToken().getValue());
        getUserProfile.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile userProfile = response.body();

                textName.setText(userProfile.getName());
                textEmail.setText(userProfile.getEmail());

            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("ProfileActivity", "Error trying to retrieve user profile", t);
            }
        });

    }

}