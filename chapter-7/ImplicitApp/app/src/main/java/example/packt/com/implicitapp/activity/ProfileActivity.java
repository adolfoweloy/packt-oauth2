package example.packt.com.implicitapp.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import example.packt.com.implicitapp.R;
import example.packt.com.implicitapp.oauth2.service.WebClient;
import example.packt.com.implicitapp.profile.UserProfile;
import example.packt.com.implicitapp.util.URIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // extract data from redirection URI which is a uri scheme

        Uri callbackUri = Uri.parse(getIntent().getDataString());

        Map<String, String> parameters = URIUtils.getQueryParameters(callbackUri.getFragment());

        if (parameters.containsKey("error")) {
            Toast.makeText(this, parameters.get("error_description"), Toast.LENGTH_SHORT).show();
            return;
        }

        String token = parameters.get("access_token");
        String state = parameters.get("state");

        // validates state parameter

        SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(getApplicationContext());
        String savedState = preferences.getString("state", "");

        if (!savedState.equals(state)) {
            Toast.makeText(this, "CSRF Attack detected", Toast.LENGTH_SHORT).show();
            return;
        }

        WebClient provider = new WebClient();

        Call<UserProfile> getUserProfile = provider.userProfile().token("Bearer " + token);
        getUserProfile.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                UserProfile userProfile = response.body();

                final TextView textName = (TextView) findViewById(R.id.profile_name);
                final TextView textEmail = (TextView) findViewById(R.id.profile_email);

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