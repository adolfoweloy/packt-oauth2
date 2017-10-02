package example.packt.com.resourceownerpassword.dashboard;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import example.packt.com.resourceownerpassword.R;
import example.packt.com.resourceownerpassword.web.oauth2.AccessToken;
import example.packt.com.resourceownerpassword.web.profile.UserProfile;
import example.packt.com.resourceownerpassword.web.WebClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardUserProfileHelper {

    private final Activity activity;

    public DashboardUserProfileHelper(Activity activity) {
        this.activity = activity;
    }

    public void load(AccessToken token) {
        final TextView usernameText = activity.findViewById(R.id.profile_username);
        final TextView emailText = activity.findViewById(R.id.profile_email);

        WebClient client = WebClient.create();
        Call<UserProfile> userProfileCallback = client.userProfile().token("Bearer " + token.getValue());

        userProfileCallback.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile profile = response.body();

                usernameText.setText(profile.getName());
                emailText.setText(profile.getEmail());
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(activity, "Error retrieving user profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
