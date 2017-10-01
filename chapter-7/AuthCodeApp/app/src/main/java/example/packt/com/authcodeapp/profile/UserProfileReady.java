package example.packt.com.authcodeapp.profile;


import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import example.packt.com.authcodeapp.R;
import example.packt.com.authcodeapp.oauth2.service.WebClient;
import example.packt.com.authcodeapp.oauth2.dto.AccessToken;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileReady implements Observer {

    private Activity profileActivity;

    public UserProfileReady(Activity profileActivity) {
        this.profileActivity = profileActivity;
    }

    @Override
    public void update(Observable observable, Object o) {

        if (o instanceof AccessToken) {
            AccessToken accessToken = (AccessToken) o;
            retrieveUserProfile(accessToken);
        } else {
            throw new RuntimeException("invalid access token being observed");
        }

    }

    private void retrieveUserProfile(AccessToken accessToken) {
        WebClient provider = WebClient.create();

        Call<UserProfile> userProfileCall = provider.userProfile()
                .token("Bearer " + accessToken.getValue());

        userProfileCall.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                UserProfile userProfile = response.body();

                final TextView textName = profileActivity.findViewById(R.id.profile_name);
                final TextView textEmail = profileActivity.findViewById(R.id.profile_email);

                textName.setText(userProfile.getName());
                textEmail.setText(userProfile.getEmail());
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e("Observer", "Error trying to retrieve user profile", t);
            }
        });

    }

}
