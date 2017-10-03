package example.packt.com.resourceownerpassword.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import example.packt.com.resourceownerpassword.client.oauth2.TokenResponseCallback;
import example.packt.com.resourceownerpassword.client.profile.OnProfileResultCallback;
import example.packt.com.resourceownerpassword.client.profile.ProfileAuthorizationListener;
import example.packt.com.resourceownerpassword.client.profile.UserProfile;
import example.packt.com.resourceownerpassword.client.profile.UserProfileService;
import example.packt.com.resourceownerpassword.login.AuthenticationManager;
import example.packt.com.resourceownerpassword.R;
import example.packt.com.resourceownerpassword.client.oauth2.PasswordAccessTokenRequest;
import example.packt.com.resourceownerpassword.login.User;
import example.packt.com.resourceownerpassword.client.oauth2.AccessToken;
import example.packt.com.resourceownerpassword.client.ClientAPI;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView usernameText;
    private TextView emailText;
    private AuthenticationManager authenticationManager;
    private UserProfileService userProfileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        usernameText = findViewById(R.id.profile_username);
        emailText = findViewById(R.id.profile_email);

        authenticationManager = new AuthenticationManager(this);
        userProfileService = new UserProfileService(getOnProfileResultCallback());

        if (authenticationManager.isAuthenticated()) {
            final User user = authenticationManager.getLoggedUser();
            createUserEntriesView(user);

            Button profileButton = findViewById(R.id.profile_button);
            profileButton.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
        User user = authenticationManager.getLoggedUser();
        AccessToken accessToken = authenticationManager.getAccessToken();

        // if the current app user already has an access token, just load her profile
        if (accessToken != null) {
            if (!accessToken.hasExpired()) {
                userProfileService.load(accessToken);
                return;
            }
        }

        // access token oauth2 request
        Call<AccessToken> call = ClientAPI.oauth2().token(
                PasswordAccessTokenRequest.from(
                        user.getUsername(), user.getPassword()));

        // create a token callback with a ProfileAuthorizationListener that will load user profile
        // when a access token is granted.
        TokenResponseCallback callback = new TokenResponseCallback(authenticationManager);
        callback.addObserver(new ProfileAuthorizationListener(userProfileService));
        call.enqueue(callback);
    }

    @NonNull
    private OnProfileResultCallback getOnProfileResultCallback() {
        return new OnProfileResultCallback() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                usernameText.setText(userProfile.getName());
                emailText.setText(userProfile.getEmail());
            }

            @Override
            public void onError(String message, Throwable t) {
                Log.e("DashboardActivity", message, t);
            }
        };
    }

    /**
     * creates some fake user entries
     * @param user
     */
    private void createUserEntriesView(User user) {
        String[] entries = user.getEntries().toArray(new String[]{});
        ListView listView = findViewById(R.id.dashboard_entries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);
    }

}
