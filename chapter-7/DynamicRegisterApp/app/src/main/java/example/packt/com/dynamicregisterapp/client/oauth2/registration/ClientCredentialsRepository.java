package example.packt.com.dynamicregisterapp.client.oauth2.registration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import example.packt.com.dynamicregisterapp.client.ClientAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientCredentialsRepository {

    private final SharedPreferences prefs;

    public ClientCredentialsRepository(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void getClientCredentialsFor(final OnClientRegistrationResult registrationResult) {
        boolean isRegistered = prefs.getBoolean("registered", false);

        final ClientCredentials credentials = new ClientCredentials();

        if (isRegistered) {
            String clientId = prefs.getString("client_id", null);
            String clientSecret = prefs.getString("client_secret", null);
            String redirectUri = prefs.getString("redirect_uri", null);

            credentials.setClientId(clientId);
            credentials.setClientSecret(clientSecret);
            credentials.setRedirectUri(redirectUri);

            registrationResult.onSuccessfulClientRegistration(credentials);
        } else {

            final String redirectUri = "oauth2://profile/callback";

            ClientRegistrationRequest request = new ClientRegistrationRequest();
            request.setScope("read_profile");
            request.setClientName("android-app");
            request.setClientUri("http://adolfoeloy.com.br/en");
            request.setSoftwareId("android-packt");
            request.getGrantTypes().add("authorization_code");
            request.getRedirectUris().add(redirectUri);

            Call<RegistrationResponse> call = ClientAPI.registration().register(request);
            call.enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                    RegistrationResponse credentialsResponse = response.body();
                    credentials.setClientId(credentialsResponse.getClientId());
                    credentials.setClientSecret(credentialsResponse.getClientSecret());
                    credentials.setRedirectUri(redirectUri);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("registered", true);
                    editor.putString("client_id", credentials.getClientId());
                    editor.putString("client_secret", credentials.getClientSecret());
                    editor.putString("redirect_uri", credentials.getRedirectUri());
                    editor.commit();

                    registrationResult.onSuccessfulClientRegistration(credentials);
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                    registrationResult.onFailedClientRegistration(
                        "Failed on trying to register client", t);
                }
            });

        }

    }

}
