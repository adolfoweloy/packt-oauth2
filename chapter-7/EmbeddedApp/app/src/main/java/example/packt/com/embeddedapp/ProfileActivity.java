package example.packt.com.embeddedapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Uri callbackUri = Uri.parse(getIntent().getDataString());

        Map<String, String> parameters = URIUtils.getQueryParameters(callbackUri.getFragment());

        if (parameters.containsKey("error")) {
            Toast.makeText(this, parameters.get("error_description"), Toast.LENGTH_SHORT).show();
            return;
        }

        String token = parameters.get("access_token");
        String state = parameters.get("state");

        // validates state parameter
        OAuth2Manager oauth2Manager = new OAuth2Manager(this);
        String savedState = oauth2Manager.getState();

        if (!savedState.equals(state)) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        usernameText = (TextView) findViewById(R.id.profile_username);
        emailText = (TextView) findViewById(R.id.profile_email);

        WebClient client = new WebClient();
        Call<UserProfile> profileCallback = client.userProfile().token("Bearer " + token);
        profileCallback.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile userProfile = response.body();

                usernameText.setText(userProfile.getName());
                emailText.setText(userProfile.getEmail());
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error retrieving user profile",
                    Toast.LENGTH_SHORT).show();
            }
        });
    }
}
