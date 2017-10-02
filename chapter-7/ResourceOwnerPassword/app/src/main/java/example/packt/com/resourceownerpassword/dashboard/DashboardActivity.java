package example.packt.com.resourceownerpassword.dashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import example.packt.com.resourceownerpassword.AuthenticationManager;
import example.packt.com.resourceownerpassword.R;
import example.packt.com.resourceownerpassword.login.User;
import example.packt.com.resourceownerpassword.web.oauth2.AccessToken;
import example.packt.com.resourceownerpassword.web.AuthorizationCallback;
import example.packt.com.resourceownerpassword.web.oauth2.PasswordGrantTypeRequest;
import example.packt.com.resourceownerpassword.web.WebClient;
import retrofit2.Call;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final AuthenticationManager authenticationManager = new AuthenticationManager(this);
        if (authenticationManager.isAuthenticated()) {
            final User user = authenticationManager.getLoggedUser();
            createUserEntriesView(user);

            Button button = findViewById(R.id.profile_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DashboardUserProfileHelper profileHelper
                        = new DashboardUserProfileHelper(DashboardActivity.this);

                    AccessToken accessToken = authenticationManager.getAccessToken();
                    if (accessToken != null) {
                        if (!accessToken.hasExpired()) {
                            profileHelper.load(accessToken);
                            return;
                        }
                    }

                    PasswordGrantTypeRequest request = new PasswordGrantTypeRequest();
                    request.setScope("read_profile");
                    request.setUsername(user.getUsername());
                    request.setPassword(user.getPassword());

                    WebClient client = WebClient.createBasicProtectedResource();
                    Call<AccessToken> tokenRequestCallback = client
                            .oauth2().password().token(request.getMap());

                    tokenRequestCallback.enqueue(
                        new AuthorizationCallback(authenticationManager, profileHelper));

                }
            });
        }

    }

    private void createUserEntriesView(User user) {
        String[] entries = user.getEntries().toArray(new String[]{});
        ListView listView = findViewById(R.id.dashboard_entries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_list_item_1, entries);
        listView.setAdapter(adapter);
    }
}
